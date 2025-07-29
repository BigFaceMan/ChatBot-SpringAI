import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import ChatView from '../components/ChatView.vue'
import RagView from '../components/RagView.vue'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/chat' },
  { path: '/chat', component: ChatView },
  { path: '/rag', component: RagView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
