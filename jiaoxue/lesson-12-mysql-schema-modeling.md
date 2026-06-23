# 第 12 课：MySQL 表结构与业务数据建模

> 课程定位：这一课解决“如何从 SQL 反推业务、如何知道每张表服务哪个功能”。IIMS 的 SQL 初始化脚本不只是建表，它还包含菜单、角色、用户、档案、知识库、模型配置等业务数据。能读懂这些表，就能读懂项目一半。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 按业务域整理 IIMS 的核心表。
2. 说明用户、角色、菜单、文件、档案、知识库、AI 模型表的作用。
3. 读懂 JSON 字段在角色菜单、用户角色、档案属性中的使用。
4. 理解表结构和 Java 实体、Mapper XML 的对应关系。
5. 能根据一个功能定位相关表。
6. 能排查“表存在但数据不对”的问题。

## 2. 源码定位

SQL：

```text
resources/sql/init-data.sql
deploy-bundle/sql/init-data.sql
```

实体：

```text
iims-module-common/src/main/java/cn/aitenry/iims/common/model/entity
iims-module-integral/src/main/java/cn/aitenry/iims/integral/model/entity
iims-module-ai/src/main/java/cn/aitenry/iims/ai/chat/model/entity
iims-module-archive/src/main/java
```

Mapper XML：

```text
iims-module-integral/src/main/resources/mapper
iims-module-ai/src/main/resources/mapper
iims-module-archive/src/main/resources/mapper
```

## 3. 业务域表分类

| 业务域 | 典型表 |
|---|---|
| 用户权限 | `iims_integral_user`、`iims_integral_role`、`iims_integral_menu`、`iims_integral_permission` |
| 组织管理 | `iims_integral_organization` |
| 字典配置 | 字典相关表 |
| 文件云库 | `iims_integral_warehouse` |
| 知识库 | `iims_integral_wiki`、`iims_integral_wiki_catalog` |
| 文章内容 | 文章、评论、标签相关表 |
| 档案管理 | `iims_archive_metadata`、`iims_archive_type`、`iims_archive_mapper` |
| AI 对话 | `iims_ai_chat_topic`、`iims_ai_chat_dialogue` |
| AI 模型 | `iims_ai_chat_models`、`iims_ai_chat_settings`、`iims_ai_agent` |
| 统计日志 | `iims_integral_statistics`、日志相关表 |

## 4. 用户权限表

### 4.1 用户表

```text
iims_integral_user
```

用途：

```text
登录账号、用户资料、角色绑定、状态控制。
```

重点字段：

```text
username
password
role
is_disable
is_deleted
avatar
```

### 4.2 角色表

```text
iims_integral_role
```

用途：

```text
角色定义和菜单授权。
```

重点字段：

```text
role_name
role_en
menus
systemic
```

### 4.3 菜单表

```text
iims_integral_menu
```

用途：

```text
前端动态路由、目录、菜单、按钮权限。
```

重点字段：

```text
path
component
icon
type
perms
is_show
```

## 5. 文件云库表

```text
iims_integral_warehouse
```

用途：

```text
保存文件元数据。
```

真实文件在 MinIO，数据库保存：

- 文件 ID。
- 文件名。
- 文件类型。
- 文件大小。
- 存储路径。
- 上传人。
- 状态。

这就是对象存储常见设计：

```text
数据库存元数据，MinIO 存二进制对象。
```

## 6. 知识库表

```text
iims_integral_wiki
iims_integral_wiki_catalog
```

用途：

- 知识库基本信息。
- 知识库目录。
- 文档组织。
- 后续触发 RAG 文档处理。

知识库不等于向量库。

```text
MySQL 存知识库业务结构。
Milvus 存文档向量。
MinIO 存文档文件。
```

## 7. 档案表

```text
iims_archive_metadata
iims_archive_type
iims_archive_mapper
```

用途：

- 档案类型。
- 档案元数据。
- 档案与字段/表单映射。

档案系统比普通 CRUD 复杂，因为它存在不同档案类型、动态属性、文件关联和查询条件。

## 8. AI 模型表

### 8.1 模型表

```text
iims_ai_chat_models
```

用途：

```text
保存 OpenAI、DeepSeek、Ollama、Agent 等模型配置。
```

重点字段：

| 字段 | 含义 |
|---|---|
| `name` | 模型真实名称 |
| `rename` | 展示名称 |
| `key` | API Key，加密后存储 |
| `token` | 上下文 token |
| `type` | API 类型 |
| `url` | baseUrl |
| `model_type` | 模型类型，如语言、Embedding |
| `is_online` | 是否在线 |

### 8.2 用户模型设置

```text
iims_ai_chat_settings
```

用途：

```text
保存用户默认聊天模型和默认 Embedding 模型。
```

如果 AI 对话或 RAG 默认模型为空，要查这张表。

### 8.3 对话表

```text
iims_ai_chat_topic
iims_ai_chat_dialogue
```

用途：

- 主题列表。
- 用户消息。
- AI 回复。
- 收藏/反馈。

## 9. JSON 字段

项目中多处使用 JSON 字符串：

```text
用户 role: ["10"]
角色 menus: ["100", "101", ...]
档案 metadata: {...}
```

优点：

```text
结构灵活，适合菜单数组、动态属性。
```

缺点：

```text
查询复杂，不如关系表规范。
约束弱。
需要 JSON_CONTAINS 等函数。
```

面试时要能说出利弊。

## 10. 表和代码怎么对应

查用户：

```text
iims_integral_user
User.java
UserMapper.java
UserMapper.xml
UserServiceImpl
UserController
```

查模型：

```text
iims_ai_chat_models
AiModel.java
AiChatModelsMapper.xml
AiChatModelsServiceImpl
AiModelController
settings/model/Index.vue
```

查档案：

```text
iims_archive_metadata
archive module Controller/Service/Mapper
前端 system/archives
```

## 11. 实操任务

### 11.1 列出所有表

```sql
USE iims;
SHOW TABLES;
```

### 11.2 核心表计数

```sql
SELECT COUNT(*) FROM iims_integral_user;
SELECT COUNT(*) FROM iims_integral_role;
SELECT COUNT(*) FROM iims_integral_menu;
SELECT COUNT(*) FROM iims_ai_chat_models;
SELECT COUNT(*) FROM iims_archive_metadata;
```

### 11.3 反推功能

选择一个功能，例如模型管理：

1. 找前端页面。
2. 找 API 文件。
3. 找 Controller。
4. 找 Service。
5. 找 Mapper XML。
6. 找表。

## 12. 常见错误

### 12.1 表存在但登录失败

可能是：

- 用户数据没有。
- 用户被禁用。
- 密码不对。
- 角色数据不对。
- Redis 不通。

### 12.2 菜单表存在但页面没菜单

可能是：

- 角色 menus 没绑定。
- 菜单 `is_show` 不对。
- 前端 component 路径不对。

### 12.3 模型表存在但 AI 不可用

可能是：

- 只有默认 Agent，没有真实语言模型。
- API Key 为空。
- baseUrl 错。
- 用户默认模型没设置。

## 13. 验收标准

学生必须能回答：

1. 用户登录依赖哪些表？
2. 菜单显示依赖哪些表？
3. 文件上传为什么既要 MySQL 又要 MinIO？
4. 知识库为什么涉及 MySQL、MinIO、Milvus？
5. AI 对话和模型配置分别对应哪些表？
6. JSON 字段的优点和缺点是什么？

## 14. 作业

1. 整理 20 张核心表的用途。
2. 选择 3 个功能画出“前端 -> 后端 -> 表”链路。
3. 写 300 字说明 IIMS 的数据库设计特点。
4. 找出 3 个 JSON 字段，并说明为什么这么设计。

## 15. 面试表达

> IIMS 的 MySQL 设计按业务域划分，用户权限域包含用户、角色、菜单表，文件域用数据库保存文件元数据、MinIO 保存真实文件，知识库域用 MySQL 保存业务结构、后续结合 Milvus 保存向量。AI 模型配置保存在 `iims_ai_chat_models`，用户默认模型保存在 `iims_ai_chat_settings`，对话主题和消息分别保存在 topic/dialogue 表。项目中角色菜单、用户角色和档案动态属性使用 JSON 字段，灵活性较高，但查询和约束会比标准中间表复杂。

