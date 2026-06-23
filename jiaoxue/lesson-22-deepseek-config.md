# 第 22 课：DeepSeek 模型配置与项目联调
> 课程定位：这一课讲 DeepSeek 的接入方式。DeepSeek 在很多求职项目中很常见，因为成本相对友好、中文效果也不错。学生要把它配置成 IIMS 的语言模型，并理解它在当前项目中更适合承担聊天生成，而不是向量化。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 在模型管理中新增 DeepSeek 类型模型。
2. 理解 DeepSeek 的 base URL、API Key、模型名如何填写。
3. 理解当前项目中 DeepSeek 主要用于 ChatModel。
4. 理解为什么 DeepSeek 不应作为默认 EmbeddingModel。
5. 能排查 DeepSeek 调用失败的常见错误。
6. 能把 DeepSeek 接入过程讲成简历项目亮点。

## 2. 源码定位

模型构建：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/ModelServiceImpl.java
```

模型配置管理：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/AiChatModelsServiceImpl.java
iims-client/src/views/settings/model/Index.vue
```

聊天调用：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/service/impl/ChatServiceImpl.java
```

用户默认模型：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/AiChatSettingServiceImpl.java
```

## 3. DeepSeek 在项目中的定位

DeepSeek 在 IIMS 中适合作为：

```text
LANGUAGE 模型
```

也就是：

- 普通对话。
- 知识库问答中的最终回答生成。
- 文档总结。
- 内容润色。
- 结构化输出。

当前项目的 `ModelServiceImpl` 中，DeepSeek 和 OpenAI、Ollama 一样属于可构建 ChatModel 的接口类型。

但是要注意：

```text
当前项目中 DeepSeek 不适合作为默认 EmbeddingModel。
```

如果 embedding 模型构建逻辑里没有支持 DeepSeek，就算页面里配置了 DeepSeek EMBEDDING，知识库向量化也可能失败。

## 4. 推荐配置

DeepSeek 聊天模型示例：

```text
name: deepseek-chat
rename: DeepSeek Chat
type: DEEPSEEK
modelType: LANGUAGE
url: https://api.deepseek.com
key: sk-your-deepseek-key
description: 用于普通聊天和知识库问答生成
```

如果使用数字枚举：

```text
type: 3
modelType: 1
```

注意：

```text
模型名以 DeepSeek 官方或你使用的网关文档为准。
```

如果你通过 OpenAI 兼容网关调用 DeepSeek，也可能不是选择 `DEEPSEEK` 类型，而是选择 `OPENAI` 类型。这取决于项目代码如何构建客户端。

## 5. DeepSeek 与 OpenAI 兼容的关系

DeepSeek 接口通常兼容 OpenAI 风格，但项目里依然单独保留了 `DEEPSEEK` 类型。

这意味着有两种可能：

### 5.1 使用 DEEPSEEK 类型

适合：

```text
代码中为 DeepSeek 单独构建客户端。
```

配置：

```text
type: DEEPSEEK
url: https://api.deepseek.com
name: deepseek-chat
```

### 5.2 使用 OPENAI 类型

适合：

```text
你使用的服务提供 OpenAI compatible endpoint。
项目的 OPENAI 构建逻辑更稳定。
```

配置：

```text
type: OPENAI
url: DeepSeek 或中转服务的 OpenAI 兼容 base URL
name: deepseek-chat
```

教学时建议：

```text
优先按照项目代码支持的类型配置。
如果 DEEPSEEK 类型失败，再检查是否应走 OPENAI 兼容方式。
```

## 6. 新增 DeepSeek 模型步骤

1. 登录 IIMS。
2. 进入模型管理页面。
3. 点击新增模型。
4. 填写模型名称、显示名称、接口地址、Key。
5. 接口类型选择 `DEEPSEEK`。
6. 模型用途选择 `LANGUAGE`。
7. 保存。
8. 到数据库检查记录。
9. 到用户模型设置中设为默认聊天模型。
10. 进入 AI 对话页测试。

## 7. 数据库检查

保存后检查：

```sql
select id, name, rename, type, model_type, url, is_deleted
from iims_ai_chat_models
order by id desc;
```

重点看：

- `name` 是否是真实模型名。
- `type` 是否对应 DeepSeek。
- `model_type` 是否对应语言模型。
- `url` 是否是 base URL。
- `is_deleted` 是否为未删除。

不要在课堂上展示真实 key。

## 8. 用户默认模型设置

新增 DeepSeek 模型后，如果不设置默认模型，聊天可能仍然使用旧模型。

需要检查：

```text
iims_ai_chat_settings
```

默认语言模型字段应指向 DeepSeek 这条模型记录的 id。

教学重点：

```text
模型配置是“系统里有哪些模型”。
用户设置是“当前用户默认用哪个模型”。
```

这两件事不是一件事。

## 9. DeepSeek 不能做什么

在当前项目认知下，DeepSeek 不建议承担：

```text
默认 EmbeddingModel
```

原因：

```text
项目 embedding 构建逻辑主要支持 Ollama 和 OpenAI。
DeepSeek/Agent 如果返回 null，就无法完成知识库向量化。
```

所以推荐搭配：

```text
聊天模型：DeepSeek Chat
向量模型：Ollama embedding 或 OpenAI embedding
向量库：Milvus
```

## 10. 联调流程

### 10.1 验证服务器能访问 DeepSeek

在后端所在服务器执行：

```bash
curl -I https://api.deepseek.com
```

如果失败，先解决网络问题。

### 10.2 验证 Key 和模型名

使用服务商文档提供的 curl 示例测试。

关注：

- 401：Key 问题。
- 404：地址或模型名问题。
- 429：频率或额度问题。
- 5xx：服务商侧问题或网关问题。

### 10.3 IIMS 页面测试

发送简单问题：

```text
请用三句话介绍 IIMS 项目的技术栈。
```

如果有流式输出，说明 ChatModel 调用链路基本通。

## 11. 常见错误

### 11.1 type 选成 OPENAI 但代码按 DEEPSEEK 配

表现：

```text
配置看似正确，但构建的客户端不符合预期。
```

排查：

```text
看 ModelServiceImpl 中 OPENAI 和 DEEPSEEK 分支分别怎么处理。
```

### 11.2 modelType 选成 EMBEDDING

表现：

```text
聊天时找不到语言模型。
知识库向量化也不能成功。
```

修复：

```text
DeepSeek Chat 配为 LANGUAGE。
```

### 11.3 base URL 填完整接口路径

表现：

```text
404 或路径重复。
```

修复：

```text
填 base URL，不要随便填 /chat/completions 完整路径。
```

### 11.4 服务器时间或网络异常

表现：

```text
本地能访问，服务器不能访问。
```

排查：

```text
在服务器 curl。
检查 DNS。
检查安全组和防火墙。
检查是否需要代理。
```

## 12. 教学演示脚本

1. 展示 DeepSeek 在模型配置中的字段。
2. 新增一条 `DEEPSEEK + LANGUAGE` 模型。
3. 打开数据库确认保存结果。
4. 设置为默认聊天模型。
5. 发起普通对话。
6. 故意把 `modelType` 改成 EMBEDDING，演示错误。
7. 恢复配置。
8. 总结 DeepSeek 和向量模型的分工。

## 13. 学生实操

任务：

1. 创建 DeepSeek 语言模型。
2. 验证服务器到 DeepSeek 地址可达。
3. 设置为默认聊天模型。
4. 完成一次 AI 对话。
5. 写出如果对话失败的三层排查路径：

```text
模型服务层
IIMS 后端层
IIMS 前端 SSE 层
```

## 14. 验收标准

学生需要能证明：

- 模型配置已保存。
- 类型是 DeepSeek 或符合项目实际的 OpenAI 兼容方式。
- 用途是 LANGUAGE。
- 默认聊天模型指向该记录。
- 对话页面能返回内容。
- 能解释为什么 DeepSeek 不作为默认 embedding 模型。

## 15. 作业

写一份 DeepSeek 接入说明：

```text
1. 配置字段
2. 数据库记录
3. 默认模型设置
4. 调用链路
5. 常见错误
6. 面试表达
```

不要写真实 API Key。

## 16. 面试表达

可以这样说：

> 我在项目中接入了 DeepSeek 作为语言模型，通过模型管理页面保存模型名、接口地址、类型和加密后的 Key。用户对话时后端根据默认模型设置读取 DeepSeek 配置，动态构建 ChatModel，并通过 SSE 返回流式响应。知识库向量化没有使用 DeepSeek，而是单独配置 EmbeddingModel，避免聊天模型和向量模型职责混淆。

## 17. 最终交付物

```text
DeepSeek LANGUAGE 模型配置
默认聊天模型设置记录
一次对话成功记录
DeepSeek 错误排查表
DeepSeek 在简历中的项目描述
```

