# 第 20 课：模型配置表与前端模型管理
> 课程定位：这一课解决“模型到底在哪里配置”的问题。IIMS 不是让你在代码里写死 API Key，而是通过前端模型管理页面维护模型配置，再由后端从数据库读取并动态构建模型。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 找到模型管理前端页面和 API 文件。
2. 找到模型配置后端 Controller、Service、Mapper。
3. 理解 `iims_ai_chat_models` 表字段含义。
4. 理解新增模型时 API Key 为什么要加密。
5. 理解模型接口类型和模型用途类型的区别。
6. 能独立判断一条模型配置是否可用。

## 2. 源码定位

前端：

```text
iims-client/src/views/settings/model/Index.vue
iims-client/src/api/settings/model.ts
```

后端：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/controller/AiChatModelsController.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/AiChatModelsServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/mapper/AiChatModelsMapper.java
iims-module-ai/src/main/resources/mapper/AiChatModelsMapper.xml
iims-module-ai/src/main/java/cn/aitenry/iims/ai/entity/AiChatModels.java
```

模型构建：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/ModelServiceImpl.java
```

数据库：

```text
iims_ai_chat_models
```

## 3. 为什么模型要做成配置表

如果把模型写死在代码中：

```java
String apiKey = "sk-xxx";
String model = "gpt-4o-mini";
```

会带来几个问题：

- 换模型要改代码。
- API Key 容易泄露。
- 不同用户很难使用不同模型。
- 不能通过页面管理。
- 部署环境和开发环境难以区分。

IIMS 的做法是把模型抽象成数据库配置：

```text
模型名称
模型别名
接口类型
模型用途
接口地址
密钥
token
描述
在线状态
检测时间
```

后端使用时，根据模型 id 查询配置，再构建对应的 Spring AI 模型对象。

## 4. 前端页面字段

模型管理页面表单大致包含：

```text
name
rename
type
modelType
url
key
token
description
```

可以这样理解：

| 字段 | 含义 | 示例 |
| --- | --- | --- |
| name | 模型真实名称 | gpt-4o-mini |
| rename | 页面显示名称 | OpenAI 小模型 |
| type | 接口类型 | OPENAI / OLLAMA / DEEPSEEK |
| modelType | 模型用途 | LANGUAGE / EMBEDDING |
| url | 接口地址 | https://api.openai.com |
| key | API Key | sk-xxxx |
| token | 额外 token 或预留字段 | 可为空 |
| description | 描述 | 用于普通聊天 |

教学时要告诉学生：

```text
name 是真正传给模型服务的模型名。
rename 更偏向展示名。
type 决定后端用哪种构建方式。
modelType 决定这个模型能用来聊天、向量化还是其他用途。
```

## 5. 前端 API

`model.ts` 中通常包括：

```text
/model/page
/model/create
/model/update
/model/del/{id}
```

它们对应：

- 分页查询模型。
- 新增模型。
- 修改模型。
- 删除模型。

前端开发时，如果页面点新增没反应，优先看：

```text
iims-client/src/api/settings/model.ts
```

确认：

- URL 是否写对。
- 请求方法是否正确。
- 参数结构是否和后端 DTO 匹配。
- Axios baseURL 是否指向后端。

## 6. 后端新增模型逻辑

在 `AiChatModelsServiceImpl` 中，新增模型会做几件事：

1. 接收前端传入的模型配置。
2. 如果 key 不为空，对 key 进行加密。
3. 设置在线状态。
4. 设置未删除状态。
5. 设置检测时间。
6. 插入数据库。

关键点是：

```text
API Key 不应该明文保存。
```

项目中使用：

```text
AESEncryptionUtil
```

新增和修改时加密，真正调用模型时再解密。

## 7. 后端读取模型逻辑

真正调用模型时，入口在：

```text
ModelServiceImpl.getChatModel(Long modelId)
```

大致流程：

```text
modelId -> 查询 iims_ai_chat_models -> 判断 type -> 解密 key -> 构建 ChatModel
```

因此一条模型记录必须满足：

- id 存在。
- type 正确。
- modelType 正确。
- name 正确。
- url 可访问。
- key 如果需要则必须正确。

## 8. 字段配置详解

### 8.1 name

`name` 是模型服务真正识别的模型名。

例如：

```text
gpt-4o-mini
deepseek-chat
qwen2.5:7b
nomic-embed-text
```

如果 name 写错，后端能连上接口，但模型服务会报：

```text
model not found
```

### 8.2 rename

`rename` 更像页面显示名。

例如：

```text
DeepSeek 聊天模型
Ollama 本地向量模型
```

它不应该影响模型调用。

### 8.3 type

`type` 决定调用方式。

```text
OPENAI：走 OpenAI 风格接口。
DEEPSEEK：走 DeepSeek 相关构建逻辑。
OLLAMA：走 Ollama 本地服务。
AGENT：项目预留智能体类型。
```

### 8.4 modelType

`modelType` 决定模型用途。

```text
LANGUAGE：聊天、问答、生成。
EMBEDDING：向量化。
VISION：视觉。
MULTIMODAL：多模态。
```

当前课程重点是：

```text
LANGUAGE + EMBEDDING
```

### 8.5 url

`url` 是模型服务地址。

常见示例：

```text
https://api.openai.com
https://api.deepseek.com
http://127.0.0.1:11434
```

部署到服务器时，要注意：

```text
127.0.0.1 是服务器自己的本机，不是你的 Windows 电脑。
```

如果后端部署在阿里云，而 Ollama 跑在你本地 Windows，那么服务器访问 `127.0.0.1:11434` 是访问服务器自己，不会访问你的电脑。

## 9. 枚举数字和页面选项

如果页面提交的是数字，要确认数字与后端枚举顺序匹配。

当前理解：

```text
AiApiType:
0 AGENT
1 OPENAI
2 OLLAMA
3 DEEPSEEK

AiModelType:
0 EMBEDDING
1 LANGUAGE
2 VISION
3 MULTIMODAL
```

排查模型类型错乱时，先看数据库里保存的是数字还是字符串。

如果保存的是数字，就要非常谨慎：

- 不要改枚举顺序。
- 不要随便插入新的枚举到中间。
- 导入 SQL 时要确认数字含义。

## 10. 一条可用的语言模型配置应该长什么样

以 OpenAI 兼容接口为例：

```text
name: gpt-4o-mini
rename: OpenAI 小模型
type: OPENAI
modelType: LANGUAGE
url: https://api.openai.com
key: sk-xxxx
description: 普通聊天模型
```

如果是数据库数字：

```text
type: 1
model_type: 1
```

具体字段名以实体和表结构为准。

## 11. 一条可用的向量模型配置应该长什么样

以 Ollama 为例：

```text
name: nomic-embed-text
rename: Ollama 本地向量模型
type: OLLAMA
modelType: EMBEDDING
url: http://127.0.0.1:11434
key: 空
description: 知识库向量化
```

如果是数据库数字：

```text
type: 2
model_type: 0
```

注意：

```text
向量模型不是聊天模型。
```

如果把向量模型设置成默认聊天模型，聊天大概率会失败。

## 12. 修改模型时的 Key 处理

修改模型配置有一个常见坑：

```text
页面不填写 key，是表示不修改原 key，还是表示清空 key？
```

项目服务层需要看代码判断。

当前实现逻辑中，`updateModel` 会在 key 非空时加密。

教学时要让学生注意：

- 如果前端每次编辑都把空 key 提交给后端，可能导致 key 不变或被清空，取决于后端实现。
- 如果后端返回了加密后的 key，不应该直接展示给用户当真实 key。
- 正确体验通常是：编辑时 key 留空表示不修改，需要更换时重新填写。

## 13. 模型在线状态

模型表中通常会有在线状态或检测时间。

但要注意：

```text
在线状态字段不一定代表实时可用。
```

更可靠的验证方式是：

1. 后端能访问 `url`。
2. API Key 正确。
3. 模型名称正确。
4. 发起一次真实调用。

如果页面显示在线但调用失败，要以真实调用错误为准。

## 14. 教学演示脚本

教师演示：

1. 打开 `iims-client/src/views/settings/model/Index.vue`，看表单字段。
2. 打开 `iims-client/src/api/settings/model.ts`，看前端请求地址。
3. 打开后端模型 Controller，找新增、修改、分页、删除接口。
4. 打开 `AiChatModelsServiceImpl`，看 key 加密逻辑。
5. 打开 `ModelServiceImpl`，看 key 解密和模型构建逻辑。
6. 打开数据库 `iims_ai_chat_models`，解释每个字段。
7. 手动新增一条测试模型配置。
8. 在数据库检查 key 是否被加密。

## 15. 学生实操

任务一：源码定位

```text
找到模型管理页面
找到模型管理 API
找到模型管理 Controller
找到模型管理 Service
找到模型实体类
找到模型构建服务
```

任务二：字段解释

给一条模型配置，逐字段写出含义。

任务三：错误判断

判断下面配置有什么问题：

```text
name: nomic-embed-text
type: OPENAI
modelType: LANGUAGE
url: http://127.0.0.1:11434
key: sk-xxx
```

参考答案：

- `nomic-embed-text` 是常见 embedding 模型，不适合当语言模型。
- `url` 是 Ollama 地址，但 type 写成了 OPENAI。
- key 对 Ollama 通常不需要。
- 如果后端部署在服务器，127.0.0.1 指服务器本机。

## 16. 常见错误

### 16.1 Key 明文入库

表现：

```text
数据库中能直接看到完整 API Key。
```

风险：

```text
泄露后别人可以消耗你的额度。
```

排查：

```text
看新增模型是否走了 AiChatModelsServiceImpl.insertModel。
不要绕过服务层直接写表。
```

### 16.2 url 多写了路径

有些 SDK 需要 base URL，有些需要完整路径。

常见错误：

```text
https://api.openai.com/v1/chat/completions
```

可能应该写成：

```text
https://api.openai.com
```

具体以 `ModelServiceImpl` 构建方式为准。

### 16.3 模型用途选错

表现：

```text
聊天接口调用 embedding 模型。
知识库调用 language 模型做 embedding。
```

排查：

```text
检查 modelType。
```

### 16.4 前端 baseURL 指错

表现：

```text
模型管理页面请求 404 或跨域。
```

排查：

```text
iims-client/.env
VITE_APP_API_URL
```

## 17. 验收标准

学生需要能完成：

1. 在页面新增一条模型配置。
2. 在数据库找到这条配置。
3. 判断 key 是否加密。
4. 说明 `type` 和 `modelType` 的区别。
5. 说明为什么不能把模型写死在代码里。
6. 说明服务器部署时 `127.0.0.1` 的含义。

## 18. 作业

创建一张模型配置说明表，包含：

```text
字段名
页面含义
后端用途
错误示例
排查方法
```

至少覆盖：

```text
name
rename
type
modelType
url
key
token
description
```

## 19. 面试表达

可以这样说：

> 项目中的模型不是写死在配置文件里，而是维护在模型配置表中。前端提供模型管理页面，后端新增或修改模型时会对 API Key 加密保存。实际调用时，模型服务根据模型 id 查询配置，解密 key，并根据接口类型动态构建对应的 Spring AI ChatModel 或 EmbeddingModel。

## 20. 最终交付物

本课结束时，学生应提交：

```text
模型配置字段说明表
一条语言模型配置样例
一条向量模型配置样例
模型管理源码定位截图或笔记
常见模型配置错误清单
```

