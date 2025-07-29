<!-- src/views/LoginView.vue -->
<template>
  <div class="d-flex justify-content-center align-items-center vh-100 bg-light">
    <div class="card p-4" style="width: 350px;">
      <h4 class="mb-3 text-center">用户登录</h4>
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label for="username" class="form-label">用户名</label>
          <input type="text" id="username" v-model="username" class="form-control" required />
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">密码</label>
          <input type="password" id="password" v-model="password" class="form-control" required />
        </div>
        <button type="submit" class="btn btn-primary w-100">登录</button>
        <div v-if="error" class="alert alert-danger mt-3" role="alert">
          {{ error }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const username = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    const res = await axios.post('/user/login', null, {
      params: { username: username.value, password: password.value },
    })

    const { code, msg} = res.data
    console.log("data : ", msg)
    if (code === 1) {
      localStorage.setItem('token', msg)
      router.push('/chat')
    } else {
      error.value = msg || '登录失败'
    }
  } catch (err) {
    error.value = '请求失败，请检查网络或服务状态'
  }
}
</script>
