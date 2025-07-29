<!-- <template>
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
 -->



<template>
  <div id="app">
    <div class="main-layout">
      <!-- 左侧导航栏 -->
      <aside class="sidebar">
        <ul>
          <li :class="{ active: $route.path === '/chat' }">
            <router-link to="/chat">对话</router-link>
          </li>
          <li :class="{ active: $route.path === '/rag' }">
            <router-link to="/rag">RAG 管理</router-link>
          </li>
        </ul>
      </aside>

      <!-- 右侧主内容 -->
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
// 无需手动导入组件，router-view 会自动加载
</script>

<style lang="scss">
html, body, #app {
  height: 100%;
  margin: 0;
  font-family: sans-serif;
  overflow: hidden; /* 禁止滚动条 */
}

.main-layout {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 200px;
  background-color: #2c3e50;
  color: white;

  ul {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 1rem;

      a {
        color: white;
        text-decoration: none;
        display: block;
      }

      &.active {
        background-color: #1abc9c;
      }

      &:hover {
        background-color: #34495e;
      }
    }
  }
}

.main-content {
  flex: 1;
  padding: 1rem;
  overflow: auto;
  background-color: #f8f8f8;
}
</style>

