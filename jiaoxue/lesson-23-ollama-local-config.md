# 第 23 课：Ollama 本地模型配置
> 课程定位：这一课讲 Ollama。本地模型是求职项目非常好的演示方案，因为它不依赖付费 API Key。但也要让学生知道：Ollama 对机器配置有要求，2 核 2G 的轻量服务器通常不适合跑大模型。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 安装并启动 Ollama。
2. 拉取一个语言模型和一个向量模型。
3. 在 IIMS 中配置 Ollama LANGUAGE 模型。
4. 在 IIMS 中配置 Ollama EMBEDDING 模型。
5. 理解本地 Windows、阿里云服务器、Docker 容器之间的地址差异。
6. 判断 Ollama 适合本地开发还是服务器部署。

## 2. 源码定位

模型构建：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/service/impl/ModelServiceImpl.java
```

默认 EmbeddingModel 配置：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/config/ModelsConfig.java
```

向量库：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/store/CustomizeVectorStoreServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/store/MilvusStoreServiceImpl.java
```

模型管理页面：

```text
iims-client/src/views/settings/model/Index.vue
```

## 3. Ollama 的定位

Ollama 是本地模型运行服务。

它可以做两件事：

```text
运行语言模型
运行 embedding 模型
```

IIMS 中，Ollama 可以作为：

- 普通聊天模型。
- 知识库向量化模型。

尤其是 embedding 模型，Ollama 很适合本地开发。

## 4. 安装与启动

安装后查看版本：

```bash
ollama -v
```

启动服务：

```bash
ollama serve
```

默认地址通常是：

```text
http://127.0.0.1:11434
```

验证服务：

```bash
curl http://127.0.0.1:11434/api/tags
```

如果返回模型列表，说明 Ollama 服务正常。

## 5. 拉取模型

语言模型示例：

```bash
ollama pull qwen2.5:3b
```

向量模型示例：

```bash
ollama pull nomic-embed-text
```

查看已有模型：

```bash
ollama list
```

注意：

```text
模型名必须和 ollama list 显示的一致。
```

例如模型列表里是：

```text
qwen2.5:3b
```

IIMS 的 `name` 就要写：

```text
qwen2.5:3b
```

## 6. 配置 Ollama 语言模型

在模型管理页面新增：

```text
name: qwen2.5:3b
rename: Ollama Qwen 聊天模型
type: OLLAMA
modelType: LANGUAGE
url: http://127.0.0.1:11434
key: 空
description: 本地开发用聊天模型
```

如果保存数字：

```text
type: 2
modelType: 1
```

然后设置为默认聊天模型。

## 7. 配置 Ollama 向量模型

在模型管理页面新增：

```text
name: nomic-embed-text
rename: Ollama 本地向量模型
type: OLLAMA
modelType: EMBEDDING
url: http://127.0.0.1:11434
key: 空
description: 知识库文档向量化
```

如果保存数字：

```text
type: 2
modelType: 0
```

然后设置为默认 embedding 模型。

## 8. 地址问题是最大坑

Ollama 的 `127.0.0.1` 只代表“当前机器自己”。

### 8.1 后端在 Windows 本地

如果 Spring Boot 在 Windows 本地运行，Ollama 也在 Windows 本地运行：

```text
http://127.0.0.1:11434
```

通常可以。

### 8.2 后端在阿里云服务器

如果 Spring Boot 在阿里云服务器运行，而 Ollama 在你的 Windows：

```text
http://127.0.0.1:11434
```

对服务器来说，指的是服务器自己，不是 Windows。

这时会失败。

解决方案：

1. 把 Ollama 也安装在服务器上。
2. 或让服务器访问你的本地 Ollama 公网地址。
3. 或开发阶段后端也跑在本地。

### 8.3 后端在 Docker 容器

如果 Spring Boot 在 Docker 容器里，`127.0.0.1` 指容器自己。

可能需要：

```text
host.docker.internal
```

或者 Docker 网络中的服务名。

## 9. 2 核 2G 服务器能不能跑 Ollama

结论：

```text
可以安装，但不适合跑较大的语言模型。
```

2 核 2G 的阿里云服务器更适合：

- 跑 Spring Boot。
- 跑 MySQL、Redis、MinIO。
- 跑 Nginx。
- 做项目演示。

不适合：

- 跑 7B 语言模型。
- 同时跑多个中间件和大模型。
- 做稳定高并发推理。

求职项目建议：

```text
服务器部署业务系统。
AI 模型使用云端 API 或轻量 OpenAI 兼容接口。
本地开发时用 Ollama 演示。
```

## 10. Ollama 与知识库

知识库向量化流程：

```text
文章内容 -> Ollama EmbeddingModel -> 向量 -> Milvus
```

提问检索流程：

```text
用户问题 -> Ollama EmbeddingModel -> 问题向量 -> Milvus 搜索 -> 相关文档
```

如果 embedding 模型没配好，会出现：

- 知识库向量化任务失败。
- 知识库问答没有参考内容。
- Milvus 中没有向量数据。

## 11. ModelsConfig 的意义

项目中有：

```text
ModelsConfig.java
```

其中会配置默认 `EmbeddingModel` Bean，例如 OllamaEmbeddingModel。

要让学生理解：

```text
Spring 容器里的默认 Bean 和数据库里的用户模型配置，可能是两条线。
```

如果某些代码直接注入 `EmbeddingModel`，它用的是配置类创建的 Bean。

如果某些代码通过用户默认模型动态构建，则走数据库模型配置。

排查时必须看具体调用路径。

## 12. 最小联调流程

1. 启动 Ollama。
2. `ollama list` 确认模型存在。
3. `curl http://127.0.0.1:11434/api/tags` 确认服务可访问。
4. 在 IIMS 新增 Ollama LANGUAGE 模型。
5. 设置为默认聊天模型。
6. 发起普通对话。
7. 在 IIMS 新增 Ollama EMBEDDING 模型。
8. 设置为默认向量模型。
9. 对一个知识库执行向量化。
10. 发起知识库问答。

## 13. 常见错误

### 13.1 Ollama 没启动

表现：

```text
Connection refused
```

修复：

```bash
ollama serve
```

### 13.2 模型没拉取

表现：

```text
model not found
```

修复：

```bash
ollama pull 模型名
```

### 13.3 模型名写错

表现：

```text
接口可访问，但调用失败。
```

修复：

```bash
ollama list
```

按列表里的名字填写。

### 13.4 服务器访问本地 Ollama

表现：

```text
本地测试成功，服务器部署失败。
```

原因：

```text
服务器上的 127.0.0.1 不是你的电脑。
```

### 13.5 内存不足

表现：

```text
模型加载慢。
进程被 kill。
系统卡死。
回答极慢。
```

解决：

```text
换小模型。
本地运行。
使用云 API。
升级服务器。
```

## 14. 教学演示脚本

1. 运行 `ollama list`。
2. 拉取 `nomic-embed-text`。
3. 调用 `/api/tags`。
4. 在 IIMS 添加 Ollama 聊天模型。
5. 设置默认聊天模型。
6. 测试普通对话。
7. 添加 Ollama 向量模型。
8. 设置默认向量模型。
9. 触发知识库向量化。
10. 解释为什么服务器上不能直接写本地 `127.0.0.1`。

## 15. 学生实操

学生需要完成：

```text
安装 Ollama
拉取一个聊天模型
拉取一个 embedding 模型
配置 IIMS 语言模型
配置 IIMS 向量模型
完成一次普通聊天
完成一次知识库向量化尝试
```

如果机器配置不足，可以只完成 embedding 模型，聊天模型改用云端 API。

## 16. 验收标准

验收内容：

- `ollama list` 能看到模型。
- IIMS 模型表中有 Ollama LANGUAGE。
- IIMS 模型表中有 Ollama EMBEDDING。
- 默认模型设置正确。
- 普通对话或 embedding 至少一项能成功。
- 学生能解释 `127.0.0.1` 在不同部署环境的含义。

## 17. 作业

写一份 Ollama 接入报告：

```text
机器环境：
Ollama 版本：
聊天模型：
向量模型：
IIMS 配置：
遇到的问题：
最终方案：
是否适合部署到 2C2G 服务器：
```

## 18. 面试表达

可以这样说：

> 我在本地开发环境中使用 Ollama 接入了语言模型和 embedding 模型。语言模型用于普通对话，embedding 模型用于知识库向量化。部署时我区分了本地地址、服务器地址和 Docker 网络地址，避免把本机 127.0.0.1 错当成服务器可访问地址。由于轻量服务器资源有限，生产演示更倾向使用云端 API，本地使用 Ollama 做开发验证。

## 19. 最终交付物

```text
Ollama 安装验证记录
ollama list 结果
IIMS Ollama LANGUAGE 配置
IIMS Ollama EMBEDDING 配置
地址差异说明
资源评估结论
```

