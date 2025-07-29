<script setup lang="ts">
import { ref, onBeforeUnmount, nextTick } from 'vue'
import axios from 'axios' 
import { renderMarkdown } from "../utils/markdown"

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const chatHistory = ref<Message[]>([])
const inputText = ref('')
const chatContainer = ref<HTMLDivElement | null>(null)

const requestType = ref<'stream' | 'tools' | 'rag'>('stream')

let eventSource: EventSource | null = null

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return

  chatHistory.value.push({
    role: 'user',
    content: inputText.value
  })

  const message = inputText.value
  inputText.value = ''

  if (eventSource) {
    eventSource.close()
    eventSource = null
  }

  chatHistory.value.push({
    role: 'assistant',
    content: ''
  })

  if (requestType.value === 'stream') {
    eventSource = new EventSource(`/chat/ollama/stream?message=${encodeURIComponent(message)}`)

    eventSource.onmessage = (event) => {
      if (event.data) {
        const lastMsg = chatHistory.value[chatHistory.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant') {
          lastMsg.content += event.data
          scrollToBottom()
        }
      }
    }

    eventSource.onerror = () => {
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
    }
  } else if (requestType.value === 'tools') {
    axios.get(`/chat/ollama/basicTools`, {
        params: { message }
      }).then(res => {
        const lastMsg = chatHistory.value[chatHistory.value.length - 1]
        if (lastMsg && lastMsg.role === 'assistant') {
          lastMsg.content = res.data
          scrollToBottom()
        }
      }).catch(err => {
        console.error('请求错误:', err)
      })
  } else if (requestType.value === 'rag') {
    try {
      const res = await fetch(`/rag/base?userInput=${encodeURIComponent(message)}`)
      const text = await res.text()
      const lastMsg = chatHistory.value[chatHistory.value.length - 1]
      if (lastMsg && lastMsg.role === 'assistant') {
        lastMsg.content = text
        scrollToBottom()
      }
    } catch (e) {
      const lastMsg = chatHistory.value[chatHistory.value.length - 1]
      if (lastMsg && lastMsg.role === 'assistant') {
        lastMsg.content = "请求失败：" + (e instanceof Error ? e.message : e)
      }
    }
  }

  scrollToBottom()
}

const showRequestTypeMenu = ref(false)

const toggleMenu = () => {
  showRequestTypeMenu.value = !showRequestTypeMenu.value
}

const selectRequestType = (type: 'stream' | 'tools' | 'rag') => {
  requestType.value = type
  showRequestTypeMenu.value = false
}

// 点击页面其他地方关闭菜单
const onClickOutside = (event: MouseEvent) => {
  const menu = document.getElementById('requestTypeMenu')
  const btn = document.getElementById('requestTypeBtn')
  if (menu && btn && !menu.contains(event.target as Node) && !btn.contains(event.target as Node)) {
    showRequestTypeMenu.value = false
  }
}

window.addEventListener('click', onClickOutside)

onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  window.removeEventListener('click', onClickOutside)
})
</script>

<template>
  <div class="container">
    <div ref="chatContainer" class="chat-container">
      <div
        v-for="(msg, idx) in chatHistory"
        :key="idx"
        :class="['chat-message', msg.role === 'user' ? 'user' : 'assistant']"
      >
        <div class="message-content" v-html="renderMarkdown(msg.content)"></div>
      </div>
    </div>

    <!-- 输入框 -->
    <textarea
      v-model="inputText"
      rows="2"
      class="input-textarea"
      placeholder="输入消息，回车发送"
      @keydown.enter.prevent="sendMessage"
    ></textarea>

    <!-- 模式选择和发送按钮容器 -->
    <div class="action-row">
      <div class="mode-select-wrapper">
        <button id="requestTypeBtn" class="mode-select-btn" @click="toggleMenu">
          模式：{{ requestType }} ▼
        </button>

        <div v-if="showRequestTypeMenu" id="requestTypeMenu" class="mode-select-menu">
          <div
            v-for="type in ['stream', 'tools', 'rag']"
            :key="type"
            @click="selectRequestType(type as 'stream' | 'tools' | 'rag')"
            :class="['mode-select-item', requestType === type ? 'selected' : '']"
            @mouseover="(e) => (e.currentTarget.style.backgroundColor = '#e0e7ff')"
            @mouseout="(e) => (e.currentTarget.style.backgroundColor = requestType === type ? '#3b82f6' : 'white')"
          >
            {{ type }}
          </div>
        </div>
      </div>

      <button class="send-btn" @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  height: 100vh;
  max-width: 600px;
  margin: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.chat-message {
  margin-bottom: 10px;
  max-width: 80%;
  white-space: pre-wrap;
  display: flex;
}

.chat-message.user {
  justify-content: flex-end;
}

.chat-message.assistant {
  justify-content: flex-start;
}

.message-content {
  display: inline-block;
  padding: 8px 12px;
  border-radius: 12px;
  background-color: #e5e7eb;
  color: black;
}

.chat-message.user .message-content {
  background-color: #3b82f6;
  color: white;
}

.input-textarea {
  width: 100%;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #ccc;
  resize: none;
  margin-bottom: 8px;
  box-sizing: border-box;
  font-size: 14px;
}

/* 新增：模式选择和发送按钮一行 */
.action-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

/* 模式选择按钮 */
.mode-select-wrapper {
  position: relative;
}

.mode-select-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background-color: white;
  cursor: pointer;
  white-space: nowrap;
}

.mode-select-menu {
  position: absolute;
  bottom: 100%;
  left: 0;
  margin-bottom: 6px;
  width: 140px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 0.15);
  z-index: 1000;
}

.mode-select-item {
  padding: 8px 12px;
  cursor: pointer;
  user-select: none;
  border-radius: 6px;
  background-color: white;
  color: black;
  transition: background-color 0.2s ease;
}

.mode-select-item.selected {
  background-color: #3b82f6;
  color: white;
}

.mode-select-item:hover {
  background-color: #e0e7ff;
}

/* 发送按钮 */
.send-btn {
  padding: 0 16px;
  border-radius: 8px;
  background-color: #3b82f6;
  color: white;
  border: none;
  white-space: nowrap;
  cursor: pointer;
}
</style>
