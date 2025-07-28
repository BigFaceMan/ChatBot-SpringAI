<script setup lang="ts">
import { ref, onBeforeUnmount, nextTick } from 'vue'
import {renderMarkdown } from "../utils/markdown"

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const chatHistory = ref<Message[]>([])
const inputText = ref('')
const chatContainer = ref<HTMLDivElement | null>(null)

let eventSource: EventSource | null = null

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const sendMessage = () => {
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

  eventSource = new EventSource(`http://localhost:8088/chat/ollama/stream?message=${encodeURIComponent(message)}`)

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

  scrollToBottom()
}

onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
})

</script>

<template>
  <div style="height: 100vh; display: flex; flex-direction: column; padding: 12px; max-width: 600px; margin: auto;">
    <div
      ref="chatContainer"
      style="flex: 1; overflow-y: auto; border: 1px solid #ccc; padding: 12px; border-radius: 8px; margin-bottom: 12px;"
    >
      <div v-for="(msg, idx) in chatHistory" :key="idx" :style="{ textAlign: msg.role === 'user' ? 'right' : 'left', marginBottom: '10px' }">
        <div
          :style="{
            display: 'inline-block',
            padding: '8px 12px',
            borderRadius: '12px',
            backgroundColor: msg.role === 'user' ? '#3b82f6' : '#e5e7eb',
            color: msg.role === 'user' ? 'white' : 'black',
            maxWidth: '80%',
            whiteSpace: 'pre-wrap',
          }"
          v-html="renderMarkdown(msg.content)"
        ></div>
      </div>
    </div>

    <div style="display: flex; gap: 8px; margin-bottom: 24px;">
      <textarea
        v-model="inputText"
        rows="2"
        style="flex: 1; padding: 8px; border-radius: 8px; border: 1px solid #ccc; resize: none;"
        placeholder="输入消息，回车发送"
        @keydown.enter.prevent="sendMessage"
      />
      <button @click="sendMessage" style="padding: 0 16px; border-radius: 8px; background-color: #3b82f6; color: white; border: none;">
        发送
      </button>
    </div>
  </div>
</template>
