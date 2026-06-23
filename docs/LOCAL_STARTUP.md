# IIMS 本地启动清单

## 1. 已确认的基础配置

后端默认端口：

```text
http://localhost:8090
```

前端默认端口：

```text
http://localhost:8089
```

前端接口地址来自 `iims-client/.env`：

```env
VITE_APP_API_URL='http://localhost:8090/iims'
```

后端开发环境配置来自：

```text
iims-server/iims-starter/src/main/resources/application.yml
iims-server/iims-starter/src/main/resources/application-dev.yml
```

## 2. 基础中间件

项目基础功能需要：

| 服务 | 地址 | 用途 |
| --- | --- | --- |
| MySQL 8 | `127.0.0.1:3306` | 主业务数据库 |
| Redis | `127.0.0.1:6379` | 登录态、缓存 |
| MinIO | `127.0.0.1:9000` | 文件对象存储 |

当前配置要求：

```text
MySQL 用户名：root
MySQL 密码：root
数据库：iims

MinIO 用户名：minioadmin
MinIO 密码：minioadmin
Bucket：iims-bucket
```

启动部署包内基础服务：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
docker compose up -d
```

如果你机器上已经有 `my-mysql`、`my-redis`、`my-minio` 占用了 3306、6379、9000，可以直接复用已有容器，不要再启动 `deploy-bundle/docker-compose.yml`，否则端口会冲突。

导入数据库：

```powershell
docker exec -i my-mysql mysql -uroot -proot < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

如果使用部署包里的 `iims-mysql`，命令改为：

```powershell
docker exec -i iims-mysql mysql -uroot -proot < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

创建 MinIO bucket：

```powershell
docker exec my-minio sh -c "mc alias set local http://127.0.0.1:9000 minioadmin minioadmin && mc mb -p local/iims-bucket"
```

如果 bucket 已存在，会提示已存在，可以忽略。

## 3. AI/RAG 运行时

AI 完整功能额外需要：

| 服务 | 地址 | 用途 |
| --- | --- | --- |
| Ollama | `127.0.0.1:11434` | 本地模型推理 |
| Milvus | `127.0.0.1:19530` | 知识库向量检索 |

本仓库已补充：

```text
deploy-bundle/docker-compose-ai.yml
```

启动 AI 运行时：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
docker compose -f docker-compose-ai.yml up -d
```

当前已拉取的轻量聊天模型：

```powershell
docker exec iims-ollama ollama pull qwen2.5:0.5b
```

当前已拉取的 Embedding 模型：

```powershell
docker exec iims-ollama ollama pull nomic-embed-text
```

查看本地模型：

```powershell
docker exec iims-ollama ollama list
```

## 4. 启动后端

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn -pl iims-starter -am -DskipTests package
java -jar .\iims-starter\target\iims-starter-1.0.0.jar
```

健康检查可以访问：

```text
http://localhost:8090/iims/user/login/key
```

## 5. 启动前端

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm run dev
```

访问：

```text
http://localhost:8089
```

## 6. 登录账号

初始化 SQL 中已有用户：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `Aitenry` | `admin` | 超级管理员 |
| `G-001` | `admin` | 档案管理员 |
| `Q-001` | `admin` | 教务管理员 |

密码字段是 `admin` 的 MD5：

```text
21232f297a57a5a743894a0e4a801fc3
```

## 7. AI 模型配置提醒

数据库初始化脚本默认只有一个 Agent 模型记录。本仓库已补充本地开发模型初始化脚本：

```text
resources/sql/local-ai-models.sql
deploy-bundle/sql/local-ai-models.sql
```

导入命令：

```powershell
Get-Content -Raw -Encoding UTF8 C:\Users\MoLin\Desktop\IIMS\resources\sql\local-ai-models.sql | docker exec -i my-mysql mysql -uroot -proot
```

脚本会新增：

```text
模型类型：OLLAMA
模型名称：qwen2.5:0.5b
接口地址：http://localhost:11434
```

Embedding 模型建议配置：

```text
模型类型：OLLAMA
模型名称：nomic-embed-text
接口地址：http://localhost:11434
```

并为初始化用户 `17`、`18`、`21` 设置默认聊天模型和默认 embedding 模型。

## 8. 快速检查命令

```powershell
Test-NetConnection 127.0.0.1 -Port 3306
Test-NetConnection 127.0.0.1 -Port 6379
Test-NetConnection 127.0.0.1 -Port 9000
Test-NetConnection 127.0.0.1 -Port 19530
Test-NetConnection 127.0.0.1 -Port 11434
```

全部为 `TcpTestSucceeded: True` 时，中间件基本就绪。

## 9. 当前已验证状态

当前机器已验证：

| 检查项 | 状态 |
| --- | --- |
| Docker | 可用 |
| MySQL `3306` | 已连通 |
| Redis `6379` | 已连通 |
| MinIO `9000` | 已连通 |
| MinIO bucket `iims-bucket` | 已存在 |
| 数据库 `iims` | 已存在，已有业务表 |
| Milvus `19530` | 已通过 `docker-compose-ai.yml` 启动并连通 |
| 后端 `8090` | 已打包并启动成功 |
| 前端 `8089` | 已启动成功 |
| Ollama `11434` | 已启动并连通 |
| Ollama 聊天模型 | 已拉取 `qwen2.5:0.5b` |
| Ollama Embedding 模型 | 已拉取 `nomic-embed-text` |

Ollama 服务重启命令：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
docker compose -f docker-compose-ai.yml up -d ollama
```

后端启动不要使用下面这种命令：

```powershell
mvn -pl iims-starter -am spring-boot:run
```

它会先在父 POM 上执行 Spring Boot 插件，容易报：

```text
Unable to find a suitable main class
```
