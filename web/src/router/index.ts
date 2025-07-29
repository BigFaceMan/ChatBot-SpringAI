import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import ChatView from '../components/ChatView.vue'
import RagView from '../components/RagView.vue'
import LoginView from '../components/LoginView.vue'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/chat', component: ChatView },
  { path: '/rag', component: RagView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
