# Spring AI Demo

## 简介



仿GPT聊天机器人

api用的deepseek

小小指挥官功能：对话分析，战场局势，给出智能建议



## 架构

### 前端

vue3

### 后端

SpirngAI + Mysql + Ollama

- 使用stream流式返回
- 支持RAG、ToolsCall、会话Mem
- Mem利用SpringAi自带的库，外加了一个会话表来组织具体会话记录

## Todo

- [x] 了解ChatClient、ChatModel等基本架构
- [x] RAG
  - [x] 利用Embedding模型做编码
  - [x] 使用vector_store来存储编码后的数据
  - [x] 利用vector_store查询某个query_data（要先用embedding编码
  - [x] 基于上面的API，可以做ETL这个Pipeline了
    - [x] E从source中提取陈document
    - [x] T转换document承诺document（例如按照文本、关键字等方式拆分
    - [x] L使用vector_store，加载进向量数据库（调vector_store会调用api 做embeddingjj:w

- [x] Tool Calling
  - [x] 注解来声明Tools的描述，ChatClient发送消息的时候配置.Tools即可
- [x] 加入会话memory



