<template>
  <div id="app">
    <div class="main-layout">
      <main class="chat-main">
        <ChatView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import ChatView from './components/ChatView.vue'
</script>

<style lang="scss">
html, body, #app {
  height: 100%;
  margin: 0;
  overflow: hidden; /* 禁止滚动条 */
}


.main-layout {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-main {
  height: 110%;
  flex: 1;
  overflow: hidden;
}

</style>

