<template>
  <div id="app" class="d-flex vh-100">
    <!-- 左侧导航栏 -->
    <aside class="bg-dark text-white" style="width: 200px;">
      <ul class="nav flex-column py-3">
        <li class="nav-item">
          <router-link
            to="/chat"
            class="nav-link"
            :class="{ active: $route.path === '/chat' }"
          >对话</router-link>
        </li>
        <li class="nav-item">
          <router-link
            to="/rag"
            class="nav-link"
            :class="{ active: $route.path === '/rag' }"
          >RAG 管理</router-link>
        </li>
      </ul>
    </aside>

    <!-- 右侧主内容 -->
    <main class="flex-grow-1 p-3 overflow-auto bg-light">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
// 无需手动导入组件，router-view 会自动加载
</script>

<style scoped>
.nav-link.active {
  background-color: #17a2b8; /* Bootstrap info color */
  color: white !important;
}
</style>



