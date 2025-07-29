<template>
    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 24px; position: relative;">
      
      <!-- 模式选择按钮 -->
      <div style="position: relative;">
        <button
          id="requestTypeBtn"
          @click="toggleMenu"
          style="padding: 6px 12px; border-radius: 6px; border: 1px solid #ccc; background-color: white; cursor: pointer; white-space: nowrap;"
        >
          模式：{{ requestType }} ▼
        </button>

        <div
          v-if="showRequestTypeMenu"
          id="requestTypeMenu"
          style="position: absolute; top: 100%; left: 0; background: white; border: 1px solid #ccc; border-radius: 6px; box-shadow: 0 2px 8px rgb(0 0 0 / 0.15); width: 140px; z-index: 1000;"
        >
          <div
            v-for="type in ['stream', 'tools', 'rag']"
            :key="type"
            @click="selectRequestType(type as 'stream' | 'tools' | 'rag')"
            :style="{
              padding: '8px 12px',
              cursor: 'pointer',
              backgroundColor: requestType === type ? '#3b82f6' : 'white',
              color: requestType === type ? 'white' : 'black',
              borderRadius: '6px',
              userSelect: 'none'
            }"
            @mouseover="(e) => e.currentTarget.style.backgroundColor = '#e0e7ff'"
            @mouseout="(e) => e.currentTarget.style.backgroundColor = requestType === type ? '#3b82f6' : 'white'"
          >
            {{ type }}
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <textarea
        v-model="inputText"
        rows="2"
        style="flex: 1; padding: 8px; border-radius: 8px; border: 1px solid #ccc; resize: none;"
        placeholder="输入消息，回车发送"
        @keydown.enter.prevent="sendMessage"
      />

      <!-- 发送按钮 -->
      <button
        @click="sendMessage"
        style="padding: 0 16px; border-radius: 8px; background-color: #3b82f6; color: white; border: none; white-space: nowrap;"
      >
        发送
      </button>
    </div>
  </template>