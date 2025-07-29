<template>
  <div class="container p-4 bg-white rounded shadow-sm" style="max-width: 600px; margin: auto;">
    <h2 class="mb-4">RAG 管理</h2>

    <div class="mb-3 d-flex gap-2 align-items-center">
      <input
        type="file"
        accept="application/pdf"
        @change="onFileChange"
        class="form-control"
      />
      <button
        class="btn btn-success"
        @click="uploadFile"
        :disabled="!selectedFile"
      >
        上传 PDF 到向量库
      </button>
    </div>

    <div v-if="uploadStatus" class="alert alert-info">
      {{ uploadStatus }}
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const selectedFile = ref<File | null>(null)
const uploadStatus = ref('')

function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0]
    uploadStatus.value = ''
  }
}

async function uploadFile() {
  if (!selectedFile.value) return

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const response = await axios.post('/document/readers/upload-pdf', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    uploadStatus.value = response.data
  } catch (error: any) {
    uploadStatus.value = error.response?.data || '上传失败，请检查服务端日志'
  }
}
</script>

<style scoped>
.rag-container {
  padding: 2rem;
  background: #fff;
  border-radius: 8px;
  max-width: 600px;
  margin: 0 auto;
}

.upload-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}

button {
  padding: 0.5rem 1rem;
  background-color: #1abc9c;
  border: none;
  color: white;
  cursor: pointer;
  border-radius: 4px;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.status {
  margin-top: 1rem;
  font-weight: bold;
  color: #2c3e50;
}
</style>
