<template>
  <div class="container d-flex flex-column vh-100">
    <div
      ref="chatContainer"
      class="flex-grow-1 overflow-auto border rounded p-3 mb-3 bg-white"
    >
      <div
        v-for="(msg, idx) in chatHistory"
        :key="idx"
        :class="['d-flex mb-3', msg.role === 'user' ? 'justify-content-end' : 'justify-content-start']"
      >
        <div
          class="p-2 rounded"
          :class="msg.role === 'user' ? 'bg-primary text-white' : 'bg-secondary text-white'"
          style="max-width: 70%; white-space: pre-wrap;"
          v-html="renderMarkdown(msg.content)"
        ></div>
      </div>
    </div>

    <textarea
      v-model="inputText"
      rows="2"
      class="form-control mb-2"
      placeholder="输入消息，回车发送"
      @keydown.enter.prevent="sendMessage"
    ></textarea>

    <div class="d-flex justify-content-end align-items-center gap-2">
      <div class="dropdown">
        <button
          class="btn btn-outline-secondary dropdown-toggle"
          type="button"
          id="requestTypeBtn"
          @click="toggleMenu"
        >
          模式：{{ requestType }}
        </button>
        <ul
          v-if="showRequestTypeMenu"
          class="dropdown-menu show"
          aria-labelledby="requestTypeBtn"
          id="requestTypeMenu"
        >
          <li
            v-for="type in ['stream', 'tools', 'rag']"
            :key="type"
            @click="selectRequestType(type)"
            class="dropdown-item"
            :class="{ active: requestType === type }"
            style="cursor: pointer;"
          >
            {{ type }}
          </li>
        </ul>
      </div>

      <button class="btn btn-primary" @click="sendMessage">发送</button>
    </div>
  </div>
</template>


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