<template>
  <div id="app" class="vh-100">
    <!-- 只保留右侧主内容区域 -->
    <main class="h-100 p-3 overflow-auto bg-light">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
// 无需手动导入组件，router-view 会自动加载
</script>

<style scoped>
/* 可选：保留或移除 nav-link 样式，如果不再使用可以删掉 */
.nav-link.active {
  background-color: #17a2b8;
  color: white !important;
}
</style>
