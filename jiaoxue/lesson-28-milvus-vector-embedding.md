# 第 28 课：Milvus 向量库与 Embedding 检索
> 课程定位：这一课深入 RAG 的底层：文本如何变成向量，向量如何进入 Milvus，用户问题如何通过相似度找回文档。学生要能把“向量数据库”讲得具体，而不是只会说一个名词。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 理解 Embedding 的作用。
2. 理解 Milvus 在 IIMS 中保存什么。
3. 找到向量库创建、写入、检索、删除相关代码。
4. 理解 collection、database、metric、index 的意义。
5. 能排查向量化失败和检索失败。

## 2. 源码定位

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/store/CustomizeVectorStoreServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/store/MilvusStoreServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/config/ModelsConfig.java
iims-module-integral/src/main/java/cn/aitenry/iims/integral/event/subscriber/DocumentEmbeddingSubscriber.java
```

配置项：

```text
iims.vector.host
```

默认：

```text
localhost
```

## 3. Embedding 是什么

Embedding 是把文本变成向量。

例如：

```text
"如何部署 IIMS 项目？"
```

会被转成：

```text
[0.012, -0.33, 0.48, ...]
```

向量的意义是：

```text
语义相近的文本，在向量空间中距离更近。
```

所以用户问：

```text
"怎么把项目上线到服务器？"
```

也可能检索到：

```text
"IIMS 阿里云部署步骤"
```

这就是语义检索。

## 4. Milvus 保存什么

Milvus 保存的不是原始文章全文，而是：

```text
文档片段
文档片段对应的向量
元数据
```

元数据可能包含：

```text
wikiId
documentId
articleId
title
```

这些元数据用于过滤和回溯。

## 5. IIMS 的 Milvus 配置

项目中构建 `MilvusVectorStore` 时，关键配置包括：

```text
database: iims
collection: wiki
indexType: IVF_FLAT
metricType: COSINE
```

含义：

- `database`：Milvus 数据库。
- `collection`：类似关系型数据库中的表。
- `indexType`：向量索引类型。
- `metricType`：相似度计算方式。

`COSINE` 表示使用余弦相似度。

## 6. 向量写入流程

知识库向量化事件触发后：

```text
DocumentEmbeddingSubscriber
  -> milvusStoreService.addDocumentByWiki(wikiId)
  -> 读取 wiki 下的文档
  -> EmbeddingModel 转向量
  -> MilvusVectorStore 保存
  -> 通知前端任务结果
```

这条链路中任何一步失败，知识库都检索不到。

## 7. 向量检索流程

用户提问时：

```text
MilvusStoreServiceImpl.loadDocumentByWikiReader(wikiIds, question, topK)
```

核心动作：

```text
SearchRequest.builder()
  .query(question)
  .topK(topK)
  .filterExpression(...)
```

重点：

- `query(question)`：把问题作为查询文本。
- `topK(topK)`：返回最相似的 K 条。
- `filterExpression`：限制 wikiId 范围。

## 8. 为什么要过滤 wikiId

如果不过滤：

```text
用户选择 A 知识库，却可能搜到 B 知识库内容。
```

这会导致：

- 回答不准确。
- 数据权限风险。
- 多知识库混乱。

所以检索必须带上用户选择的知识库范围。

## 9. 删除向量

当知识库、文档或文章删除时，Milvus 中的向量也应该同步处理。

否则会出现：

```text
页面上文档删除了，但 AI 还能检索到旧内容。
```

项目中需要关注：

```text
按 wiki 删除
按文档删除
按文章删除
```

相关能力通常在 `MilvusStoreServiceImpl`。

## 10. Milvus 部署检查

如果使用 Docker，需要确认：

```text
容器是否运行
端口是否开放
服务是否健康
后端配置 host 是否正确
```

常见命令：

```bash
docker ps
docker logs 容器名
```

如果后端在本地，Milvus 在 Docker：

```text
localhost 或 127.0.0.1 可能可用，取决于端口映射。
```

如果后端在服务器，Milvus 也在服务器 Docker：

```text
host 应该指向服务器可访问的地址或 Docker 网络服务名。
```

## 11. 常见错误

### 11.1 Milvus 没启动

表现：

```text
向量化失败。
连接拒绝。
```

排查：

```text
docker ps
docker logs
检查 iims.vector.host
```

### 11.2 EmbeddingModel 为空

表现：

```text
构建 VectorStore 失败。
向量化时报空指针或不支持。
```

原因：

```text
默认 embedding 模型没配置。
模型类型不支持 embedding。
```

### 11.3 collection 不存在

表现：

```text
检索或写入失败。
```

排查：

```text
确认 MilvusVectorStore 是否自动创建 collection。
确认 database 和 collection 名称。
```

### 11.4 向量维度不一致

表现：

```text
写入失败。
不同模型生成向量维度不同。
```

原因：

```text
之前用 A embedding 模型建库，后来换成 B embedding 模型。
```

修复：

```text
清理 collection，统一 embedding 模型后重新向量化。
```

## 12. 教学演示脚本

1. 打开 `CustomizeVectorStoreServiceImpl`。
2. 讲 database、collection、index、metric。
3. 打开 `MilvusStoreServiceImpl`。
4. 讲 `loadDocumentByWikiReader`。
5. 打开 `DocumentEmbeddingSubscriber`。
6. 讲异步向量化任务。
7. 准备一篇文章触发向量化。
8. 查询或观察检索结果。
9. 故意换 embedding 模型，解释维度风险。

## 13. 学生实操

任务：

1. 启动 Milvus。
2. 配置一个 embedding 模型。
3. 创建知识库文章。
4. 触发向量化。
5. 发起知识库问答。
6. 记录 topK 检索结果。
7. 写出向量化失败时的排查顺序。

## 14. 验收标准

学生必须能回答：

1. Embedding 是什么？
2. Milvus 保存什么？
3. collection 相当于什么？
4. COSINE 是什么含义？
5. 为什么换 embedding 模型可能导致维度问题？
6. 删除文档后为什么要清理向量？

## 15. 作业

写一份 Milvus 和 Embedding 说明：

```text
文本如何变向量
向量如何写入 Milvus
问题如何检索文档
wikiId 如何过滤
topK 如何影响结果
向量库常见故障
```

## 16. 面试表达

可以这样说：

> 知识库文档会先通过 EmbeddingModel 转成向量，并写入 Milvus 的 wiki collection。用户提问时，系统把问题也转成向量，再用余弦相似度检索 topK 相关文档，同时通过 wikiId 过滤用户选择的知识库范围。检索结果会作为上下文参与 Prompt 组装。排查时我会关注 Milvus 服务状态、embedding 模型配置、向量维度一致性和检索过滤条件。

## 17. 最终交付物

```text
Milvus 配置说明
Embedding 模型配置记录
一次向量化成功记录
一次 topK 检索分析
向量库排查清单
```

