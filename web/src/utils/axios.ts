/*
 * @Author: ssp
 * @Date: 2025-07-29 19:46:22
 * @LastEditTime: 2025-07-29 19:47:53
 */
// src/utils/axios.ts
import axios from 'axios'

// 创建 Axios 实例
const instance = axios.create({
  baseURL: '/', // 替换成你的后端地址
  timeout: 10000,
})

// 请求拦截器：自动加上 JWT Token
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器（可选）
instance.interceptors.response.use(response => {
  return response
}, error => {
  // 处理 token 过期或其他错误
  if (error.response && error.response.status === 401) {
    console.warn("未授权或登录过期")
    // 可以自动跳转到登录页面
    // window.location.href = "/login"
  }
  return Promise.reject(error)
})

export default instance
