<template>
  <div class="d-flex vh-100 overflow-hidden">
    <!-- 左侧固定侧边栏 -->
    <aside class="bg-dark text-white d-flex flex-column p-3" style="width: 260px; flex-shrink: 0;">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h5 class="mb-0">会话列表</h5>
        <button class="btn btn-sm btn-outline-light" @click="showCreateModal = true">+</button>
      </div>

      <ul class="list-group list-group-flush">
        <li
          v-for="conv in conversations"
          :key="conv.id"
          class="list-group-item list-group-item-action bg-dark text-white border-0 rounded mb-1"
          :class="{ 'active bg-info text-white': conv.id === selectedConversationId }"
          @click="loadConversation(conv.id)"
          style="cursor: pointer;"
        >
          {{ conv.title }}
        </li>
      </ul>
    </aside>

    <!-- 右侧聊天区域 -->
    <div class="flex-grow-1 d-flex flex-column p-4 bg-light">
      <!-- 聊天内容区域 -->
      <div
        ref="chatContainer"
        class="flex-grow-1 overflow-auto mb-3 rounded border bg-white p-3 shadow-sm"
      >
        <div
          v-for="(msg, idx) in chatHistory"
          :key="idx"
          class="mb-3 d-flex"
          :class="msg.type === 'USER' ? 'justify-content-end' : 'justify-content-start'"
        >
          <div
            class="p-3 rounded"
            :class="msg.type === 'USER' ? 'bg-primary text-white' : 'bg-secondary text-white'"
            style="max-width: 75%; white-space: pre-wrap;"
            v-html="renderMarkdown(msg.content)"
          ></div>
        </div>
      </div>

      <!-- 输入栏 -->
      <div class="input-group">
        <textarea
          v-model="inputText"
          rows="2"
          class="form-control"
          placeholder="输入消息，回车发送"
          @keydown.enter.prevent="sendMessage"
        ></textarea>
        <button class="btn btn-primary" @click="sendMessage">发送</button>
      </div>

      <!-- 当前会话提示 -->
      <div class="mt-2 small text-muted">当前会话：{{ selectedConversationId || '无' }}</div>
    </div>

    <!-- 新建会话 Modal -->
    <div v-if="showCreateModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg">
          <div class="modal-header">
            <h5 class="modal-title">新建会话</h5>
            <button type="button" class="btn-close" @click="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <input
              type="text"
              class="form-control"
              v-model="newConversationTitle"
              placeholder="请输入会话标题"
            />
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="showCreateModal = false">取消</button>
            <button class="btn btn-primary" @click="createNewConversation">创建</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { renderMarkdown } from '../utils/markdown'

interface Conversation {
  id: string
  title: string
  userId: number
  createdAt: any
}

interface Message {
  conversationId: string
  content: string
  type: 'USER' | 'ASSISTANT'
  timestamp: number
}

const conversations = ref<Conversation[]>([])
const selectedConversationId = ref<string | null>(null)
const chatHistory = ref<Message[]>([])
const inputText = ref('')
const chatContainer = ref<HTMLDivElement | null>(null)
let eventSource: EventSource | null = null

const token = localStorage.getItem('token') || ''

const loadConversations = async () => {
  const res = await axios.get('/user/conversations/list', {
    headers: { Authorization: token }
  })
  conversations.value = res.data.data || []
}

const createNewConversation = async () => {
  const title = newConversationTitle.value.trim() || '新的会话'
  try {
    const res = await axios.post(
      `/user/conversations/create`,
      { title }, // 请求体 data
      {
        headers: { Authorization: token }
      }
    )
    const conv = res.data.data
    conversations.value.unshift(conv)
    loadConversation(conv.id)
  } catch (e) {
    console.error('创建会话失败', e)
  } finally {
    showCreateModal.value = false
    newConversationTitle.value = ''
  }
}


const loadConversation = async (id: string) => {
  selectedConversationId.value = id
  const res = await axios.get(`/user/conversations/content`, {
    params: { conversationId: id },
    headers: { Authorization: token }
  })
  chatHistory.value = res.data.data || []
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputText.value.trim() || !selectedConversationId.value) return

  const message = inputText.value
  inputText.value = ''

  chatHistory.value.push({
    conversationId: selectedConversationId.value,
    content: message,
    type: 'USER',
    timestamp: Date.now()
  })

  chatHistory.value.push({
    conversationId: selectedConversationId.value,
    content: '',
    type: 'ASSISTANT',
    timestamp: Date.now()
  })

  if (eventSource) {
    eventSource.close()
  }

  eventSource = new EventSource(`/user/chat/ollama/stream?message=${encodeURIComponent(message)}&conversationId=${selectedConversationId.value}&token=${encodeURIComponent(token)}`)

  eventSource.onmessage = (event) => {
    if (event.data) {
      const lastMsg = chatHistory.value[chatHistory.value.length - 1]
      if (lastMsg && lastMsg.type === 'ASSISTANT') {
        lastMsg.content += event.data
        scrollToBottom()
      }
    }
  }

  eventSource.onerror = () => {
    if (eventSource) eventSource.close()
    eventSource = null
  }
}

const showCreateModal = ref(false)
const newConversationTitle = ref('')



onMounted(() => {
  loadConversations()
})

onBeforeUnmount(() => {
  if (eventSource) eventSource.close()
})
</script>

<style scoped>
.list-group-item {
  transition: background-color 0.2s, transform 0.1s;
}

.list-group-item:hover {
  background-color: #495057 !important;
  transform: translateX(2px);
  color: white;
}

textarea.form-control {
  resize: none;
}

.list-group-item.active {
  background-color: #17a2b8;
  border-color: #17a2b8;
  color: white;
}

.modal {
  z-index: 1050;
}

.modal .modal-dialog {
  margin-top: 10%;
}

.list-group-item.active {
  background-color: #17a2b8;
  border-color: #17a2b8;
  color: white;
}
</style>