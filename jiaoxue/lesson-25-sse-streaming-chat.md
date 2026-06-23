# 第 25 课：SSE 流式对话实现
> 课程定位：这一课解决“为什么 AI 回答是一点点出现的”。IIMS 的对话不是普通请求一次性返回 JSON，而是使用 SSE 建立长连接，由后端不断把模型生成的片段推给前端。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 理解 SSE 和普通 HTTP 请求的区别。
2. 找到 IIMS 前端 SSE 工具和后端 SSE Controller。
3. 理解 `SseEmitter` 的作用。
4. 理解 `ChatModel.stream` 如何和 SSE 结合。
5. 能使用浏览器 Network 调试 EventStream。
6. 能排查流式接口常见问题。

## 2. 源码定位

前端：

```text
iims-client/src/utils/request-sse.ts
iims-client/src/api/ai/chat.ts
```

后端：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/controller/ChatController.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/service/impl/ChatServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/util/ChatUtil.java
```

模型：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/ModelServiceImpl.java
```

## 3. 什么是 SSE

SSE 全称是 Server-Sent Events。

它的特点是：

```text
客户端发起请求。
服务端保持连接。
服务端持续向客户端推送事件。
客户端一边接收一边渲染。
```

它适合 AI 对话，因为大模型生成答案本来就是逐 token 或逐片段产生。

## 4. SSE 和普通请求的区别

普通请求：

```text
前端请求 -> 后端处理 10 秒 -> 一次性返回完整结果
```

用户体验：

```text
等很久，突然出现一大段。
```

SSE 请求：

```text
前端请求 -> 后端立即建立连接 -> 模型边生成 -> 后端边推送 -> 前端边显示
```

用户体验：

```text
马上看到内容开始输出。
```

## 5. 前端 SSE 请求

前端在 `chat.ts` 中构造 SSE URL：

```text
${VITE_APP_API_URL}/ai/chat/receive/answer/${uuid}
```

真正发请求的工具在：

```text
request-sse.ts
```

它使用类似 `fetchEventSource` 的方式建立连接，并携带：

```text
Content-Type
Cache-Control
Connection
token
```

其中 `token` 很重要。因为后端接口仍然要经过登录鉴权。

## 6. 后端 SSE 入口

后端入口：

```text
/iims/ai/chat/receive/answer/{uuid}
```

Controller 接收到请求后，会进入 ChatService。

服务层创建：

```java
SseEmitter emitter = new SseEmitter(0L);
```

`0L` 通常表示不主动超时。AI 回答可能比较久，所以这里不能设置很短的超时时间。

## 7. 为什么需要 uuid

SSE 对话中，`uuid` 可以用来标识一次对话连接。

它可能用于：

- 存储当前连接状态。
- 停止指定回答。
- 让前端和后端对应同一次请求。
- 防止多个流式回答互相混淆。

停止回答接口：

```text
/iims/ai/chat/stop/answer/{uuid}
```

这说明项目不仅能开始流式回答，还预留了中断能力。

## 8. ChatModel.stream

核心调用大致是：

```java
modelService.getChatModel(modelId).stream(new Prompt(messages))
```

含义：

```text
根据 modelId 构建 ChatModel。
把历史消息、用户问题、知识库上下文组装成 Prompt。
调用 stream 方法获得流式结果。
```

普通调用可能返回一个完整对象。

流式调用返回的是不断产生的片段。

## 9. 后端如何把模型流转成 SSE

大致流程：

```text
ChatModel.stream
  -> 获得模型输出片段
  -> ChatUtil 处理片段
  -> SseEmitter.send
  -> 前端 EventStream 接收
  -> 页面追加显示
```

这里的关键不是“拿到完整答案再发”，而是：

```text
拿到一点，发一点。
```

## 10. 浏览器调试方法

打开浏览器开发者工具：

```text
Network -> 找到 receive/answer 请求 -> 查看 EventStream
```

重点观察：

- 请求是否是 pending。
- 响应头是否是 event-stream。
- 是否持续收到数据。
- 是否很快断开。
- 是否返回登录失效或异常 JSON。

如果 SSE 请求一打开就结束，说明后端流没有持续推送。

## 11. Nginx 对 SSE 的影响

部署到服务器后，如果前面有 Nginx，需要注意代理缓冲。

SSE 不适合被 Nginx 缓冲成一整块再返回。

常见配置方向：

```nginx
proxy_buffering off;
proxy_cache off;
```

如果不关缓冲，可能出现：

```text
本地开发流式正常。
部署后前端一直不显示。
最后一次性显示全部内容。
```

这就是代理层把流式响应“攒起来”了。

## 12. 常见错误

### 12.1 SSE 请求 401

原因：

```text
没有带 token。
token 过期。
Sa-Token 拦截。
```

排查：

```text
看 request-sse.ts 请求头。
看浏览器 Network Request Headers。
```

### 12.2 请求 404

原因：

```text
VITE_APP_API_URL 配错。
Nginx 转发路径错。
后端 context-path 不一致。
```

排查：

```text
确认后端真实地址是 /iims/ai/chat/receive/answer/{uuid}
```

### 12.3 一次性返回，不是流式

原因：

```text
模型接口不是流式。
后端没有使用 stream。
Nginx 开启缓冲。
前端处理方式错误。
```

### 12.4 后端线程上下文丢失

项目中异步处理时需要设置：

```text
BaseContext.setCurrentId(userId)
```

否则可能出现：

```text
异步线程拿不到当前用户。
保存消息时 userId 为空。
```

## 13. 教学演示脚本

1. 打开 `request-sse.ts`。
2. 找到 token 请求头。
3. 打开 `ChatController`。
4. 找到 `/receive/answer/{uuid}`。
5. 打开 `ChatServiceImpl`。
6. 找到 `SseEmitter` 创建位置。
7. 找到 `ChatModel.stream` 调用。
8. 发起一次对话。
9. 在浏览器 Network 中观察 EventStream。
10. 停止回答，观察 stop 接口。

## 14. 学生实操

任务：

1. 发起一次 AI 对话。
2. 截图或记录 SSE 请求地址。
3. 记录请求头中的 token。
4. 观察 EventStream 内容。
5. 故意退出登录后再次请求，观察鉴权失败。
6. 总结 SSE 请求和普通 Axios 请求的差异。

## 15. 验收标准

学生必须能回答：

1. SSE 为什么适合 AI 对话？
2. IIMS 前端在哪里发 SSE 请求？
3. 后端用什么对象维持 SSE 连接？
4. `ChatModel.stream` 和普通调用有什么区别？
5. Nginx 为什么可能破坏流式效果？
6. SSE 请求为什么也要带 token？

## 16. 作业

写一份 SSE 排查手册，至少包括：

```text
请求地址
请求头
响应状态
EventStream
后端日志
Nginx 缓冲
模型流式支持
```

## 17. 面试表达

可以这样说：

> AI 对话接口使用 SSE 实现流式响应。前端通过 fetchEventSource 建立长连接，并携带登录 token；后端使用 SseEmitter 维持连接，调用 Spring AI ChatModel.stream 获取模型增量输出，再把片段实时推送给前端。部署时我还关注了 Nginx 代理缓冲问题，避免流式内容被攒成一次性返回。

## 18. 最终交付物

```text
SSE 请求截图或记录
前后端 SSE 源码定位笔记
一次流式对话成功记录
SSE 常见错误排查表
```

