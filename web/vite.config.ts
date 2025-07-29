import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
      proxy: {
        '/document': {
          target: 'http://localhost:8088',
          changeOrigin: true,
        },
        '/chat': {
          target: 'http://localhost:8088',
          changeOrigin: true,
        },
        '/rag': {
          target: 'http://localhost:8088',
          changeOrigin: true,
        }
      },
  },
})
