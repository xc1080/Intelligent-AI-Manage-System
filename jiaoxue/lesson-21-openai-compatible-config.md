# 第 21 课：OpenAI 兼容接口配置与联调
> 课程定位：这一课专门解决“OpenAI 兼容接口怎么配”。很多国产模型、中转服务、私有网关都提供 OpenAI 风格接口。只要项目的构建逻辑兼容，就可以把它们当成 OPENAI 类型接入。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 理解什么叫 OpenAI 兼容接口。
2. 在模型管理页面新增一个 OPENAI 类型语言模型。
3. 判断 base URL、模型名称、API Key 是否填写正确。
4. 使用服务器命令验证接口可达。
5. 在 IIMS 中把该模型设置为默认聊天模型。
6. 排查 401、404、model not found、connection refused 等错误。

## 2. 就业目标

企业项目里经常不会直接裸调 OpenAI 官方接口，而是使用：

- 公司内部模型网关。
- 云厂商模型服务。
- 第三方 OpenAI 兼容中转。
- 私有部署的大模型服务。

面试表达可以是：

> 我做过 OpenAI 兼容接口接入，核心是区分 base URL、模型名和 API Key。后端通过 Spring AI 的 OpenAI 客户端构建 ChatModel，因此只要服务端协议兼容，就可以通过数据库配置动态切换模型。

## 3. 源码定位

模型构建：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/ModelServiceImpl.java
```

模型管理：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/AiChatModelsServiceImpl.java
iims-client/src/views/settings/model/Index.vue
iims-client/src/api/settings/model.ts
```

对话入口：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/controller/ChatController.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/service/impl/ChatServiceImpl.java
```

## 4. 什么是 OpenAI 兼容接口

OpenAI 兼容接口不是一定指 OpenAI 官方。

它通常表示服务端接口风格接近：

```text
POST /v1/chat/completions
Authorization: Bearer <api-key>
model: xxx
messages: [...]
stream: true
```

只要某个模型服务支持类似协议，后端就可以用 OpenAI 客户端去调用。

常见服务包括：

- OpenAI 官方。
- Azure/OpenAI 兼容网关。
- 公司统一 AI Gateway。
- 第三方大模型聚合服务。
- 某些本地推理服务的 OpenAI compatible endpoint。

## 5. 配置字段

一条 OpenAI 兼容语言模型通常这样填：

```text
name: gpt-4o-mini
rename: OpenAI 兼容聊天模型
type: OPENAI
modelType: LANGUAGE
url: https://api.openai.com
key: sk-xxxx
description: 用于普通对话和知识库问答生成
```

如果页面保存的是数字：

```text
type: 1
modelType: 1
```

具体以页面和数据库字段为准。

## 6. base URL 怎么填

最容易错的是 `url`。

很多人会把完整接口地址填进去：

```text
https://api.openai.com/v1/chat/completions
```

但多数客户端需要的是 base URL：

```text
https://api.openai.com
```

或者：

```text
https://api.openai.com/v1
```

到底填哪一个，要看 `ModelServiceImpl` 里 Spring AI 客户端是如何设置 baseUrl 的。

排查方法：

1. 看代码里是否自动拼接 `/v1`。
2. 看报错是 404 还是 401。
3. 用 curl 手动请求模型服务。
4. 对照服务提供商文档。

## 7. 模型名称怎么填

`name` 必须是服务端识别的模型名。

例如：

```text
gpt-4o-mini
gpt-4.1-mini
qwen-plus
deepseek-chat
```

如果模型名写错，常见报错：

```text
model not found
The model does not exist
404
```

注意：

```text
rename 只是页面显示名，不等于真实模型名。
```

## 8. API Key 怎么填

API Key 填服务商给你的密钥。

新增模型后，后端会加密保存。调用时 `ModelServiceImpl` 会解密：

```text
AESEncryptionUtil.decrypt(chatApi.getKey())
```

所以不要：

- 把已经加密后的 key 再复制回页面。
- 在 SQL 文件中明文提交真实 key。
- 把 key 写到简历、截图、课程文档里。

教学演示可以使用占位：

```text
sk-your-api-key
```

## 9. 服务器可达性测试

如果后端部署在阿里云，必须在阿里云服务器上测试。

示例：

```bash
curl -I https://api.openai.com
```

如果是兼容接口：

```bash
curl -I https://your-gateway.example.com
```

如果服务器无法访问，会出现：

```text
Connection timed out
Could not resolve host
Connection refused
```

这不是前端问题，也不是 Spring Boot 问题，而是服务器网络问题。

## 10. 最小联调流程

### 10.1 新增模型

在模型管理页面新增：

```text
接口类型：OPENAI
模型用途：LANGUAGE
模型名称：服务商真实模型名
接口地址：服务商 base URL
API Key：服务商密钥
```

### 10.2 设置默认模型

新增模型只是把模型放进系统，还不代表聊天一定使用它。

还需要到用户模型设置中，把它设为默认聊天模型。

如果没有默认设置，聊天代码可能找不到 modelId。

### 10.3 发起聊天

打开 AI 对话页面，发送：

```text
你好，请用一句话介绍你自己。
```

观察：

- 前端是否建立 SSE 连接。
- 后端是否报错。
- 是否有流式内容返回。
- 数据库是否保存对话记录。

## 11. 用 curl 做最小验证

如果页面失败，不要盲目改前端，先验证模型服务。

OpenAI 风格请求示例：

```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer sk-your-api-key" \
  -d "{\"model\":\"gpt-4o-mini\",\"messages\":[{\"role\":\"user\",\"content\":\"hello\"}]}"
```

Windows PowerShell 示例可以写成：

```powershell
$headers = @{
  "Content-Type" = "application/json"
  "Authorization" = "Bearer sk-your-api-key"
}
$body = @{
  model = "gpt-4o-mini"
  messages = @(@{ role = "user"; content = "hello" })
} | ConvertTo-Json -Depth 5
Invoke-RestMethod -Uri "https://api.openai.com/v1/chat/completions" -Method Post -Headers $headers -Body $body
```

如果 curl 都失败，IIMS 一定也会失败。

## 12. 常见错误和排查

### 12.1 401 Unauthorized

原因：

```text
API Key 错误
Key 没有权限
Key 被禁用
请求头格式不对
```

排查：

```text
重新生成 key。
用 curl 验证。
检查后端是否正确解密 key。
```

### 12.2 404 Not Found

原因：

```text
base URL 填错。
路径重复拼接。
模型服务不支持该路径。
```

常见错误：

```text
url 填了 /v1/chat/completions，客户端又拼了一次路径。
```

### 12.3 model not found

原因：

```text
name 不是服务端模型名。
服务账号没有该模型权限。
```

排查：

```text
去服务商控制台查看可用模型名。
检查 name 而不是 rename。
```

### 12.4 连接超时

原因：

```text
服务器不能访问外网。
DNS 问题。
安全组或防火墙限制。
代理配置缺失。
```

排查：

```text
在后端所在机器上 curl。
不要只在自己 Windows 上测试。
```

### 12.5 SSE 连接成功但没有内容

原因：

```text
模型流式返回异常。
后端处理 stream 时异常。
前端没有正确处理增量事件。
```

排查：

```text
看后端日志。
看 Network 的 EventStream。
看 request-sse.ts。
```

## 13. 安全注意事项

API Key 属于敏感信息。

不要放在：

- GitHub 仓库。
- 简历截图。
- 课程文档。
- 前端代码。
- 聊天记录截图。

如果误提交了 Key：

1. 立刻去服务商控制台删除或重置。
2. 清理 Git 历史不能完全保证安全。
3. 简历和演示环境统一使用占位 Key。

## 14. 教学演示脚本

1. 打开模型管理页面。
2. 新增 OPENAI 类型语言模型。
3. 打开数据库查看记录。
4. 打开 `AiChatModelsServiceImpl` 说明 key 加密。
5. 打开 `ModelServiceImpl` 说明 key 解密和 ChatModel 构建。
6. 设置默认模型。
7. 发起一次对话。
8. 打开浏览器 Network 看 SSE。
9. 故意把模型名改错，演示 `model not found`。
10. 恢复正确配置。

## 15. 学生实操

学生需要完成：

1. 新增一条 OpenAI 兼容语言模型。
2. 用 curl 或 PowerShell 直接验证服务商接口。
3. 在 IIMS 中设置为默认聊天模型。
4. 发起一次普通对话。
5. 记录完整排查过程。

## 16. 验收标准

本课验收：

- 页面中能看到新增模型。
- 数据库中能看到模型记录。
- API Key 不是明文保存。
- 默认模型设置指向该模型。
- AI 对话页面能返回答案。
- 学生能解释 base URL 和完整 API URL 的区别。

## 17. 作业

写一份联调报告：

```text
模型服务商：
base URL：
模型名称：
接口类型：
模型用途：
curl 验证结果：
IIMS 页面验证结果：
遇到的问题：
最终解决方式：
```

注意不要写真实 API Key。

## 18. 面试表达

可以这样说：

> 我接入 OpenAI 兼容接口时，重点处理了模型配置动态化和密钥安全。模型的 base URL、模型名、接口类型、用途类型都保存在数据库中，新增和修改时对 API Key 加密。对话时后端根据用户默认模型读取配置，通过 Spring AI 构建 ChatModel，并使用 SSE 返回流式内容。

## 19. 最终交付物

```text
一条可用的 OPENAI 类型语言模型配置
一次 curl 验证记录
一次 IIMS 对话成功截图或日志记录
OpenAI 兼容接口错误排查表
```

