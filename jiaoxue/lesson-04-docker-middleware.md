# 第 4 课：Docker 中间件一键启动

> 课程定位：这一课解决“项目依赖的 MySQL、Redis、MinIO 怎么稳定启动”。前三课已经完成项目认识、环境检查和数据库初始化，本课开始把中间件容器化，让项目不再依赖手工安装一堆服务。Docker 不是为了炫技，而是为了让开发、部署、重启、排错更可控。

## 1. 本课目标

### 1.1 教学目标

学完本课后，学生应该能做到：

1. 理解 IIMS 为什么需要 MySQL、Redis、MinIO、Milvus。
2. 读懂项目里的 `docker-compose.yml`。
3. 使用 Docker Compose 启动 MySQL、Redis、MinIO。
4. 验证容器是否运行、端口是否监听、日志是否正常。
5. 理解端口映射、数据卷、容器名、镜像、环境变量。
6. 知道 Docker 服务、Docker CLI、Docker Compose 的区别。
7. 能处理 `docker.service does not exist`、端口占用、容器启动失败、镜像下载失败等问题。
8. 知道为什么 Milvus 不建议在 2GB ECS 第一阶段强行启动。
9. 能把中间件容器化写进部署文档和面试表达。

### 1.2 就业目标

真实项目中，本地和服务器经常需要启动多个依赖服务。手工安装 MySQL、Redis、MinIO 容易出现：

- 版本不一致。
- 配置不一致。
- 数据目录混乱。
- 服务无法重启。
- 端口被占用。
- 换电脑后重新配置很慢。

Docker Compose 的价值是：

> 用一个配置文件描述一组服务，统一启动、停止、查看日志、持久化数据。

这是一名后端工程师非常实用的部署能力。

## 2. 本课涉及的项目文件

本课重点文件：

```text
C:\Users\MoLin\Desktop\IIMS\deploy-bundle\docker-compose.yml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application.yml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application-dev.yml
C:\Users\MoLin\Desktop\IIMS\启动配置.txt
```

当前部署包中的 Compose 文件：

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: iims-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: iims
      TZ: Asia/Shanghai
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_0900_ai_ci --default-time-zone=+08:00
    ports:
      - "3306:3306"
    volumes:
      - ./data/mysql:/var/lib/mysql

  redis:
    image: redis:7
    container_name: iims-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - ./data/redis:/data

  minio:
    image: minio/minio
    container_name: iims-minio
    restart: always
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
      TZ: Asia/Shanghai
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - ./data/minio:/data
```

## 3. 课前概念

### 3.1 Docker 是什么

可以先理解为：

```text
Docker 用容器运行软件。
```

以前你要安装 MySQL：

```text
下载安装包
配置环境变量
配置服务
配置数据目录
配置端口
配置密码
启动服务
```

使用 Docker 后：

```text
拉取 mysql:8.0 镜像
根据配置启动容器
把 3306 映射到宿主机
把数据挂载到宿主机目录
```

### 3.2 镜像和容器

| 概念 | 理解 |
|---|---|
| 镜像 image | 软件模板，例如 `mysql:8.0` |
| 容器 container | 镜像运行起来后的实例，例如 `iims-mysql` |
| 数据卷 volume | 把容器里的数据保存到宿主机，避免容器删除后数据丢失 |
| 端口映射 ports | 把容器端口暴露到宿主机端口 |

简单类比：

```text
镜像像安装包。
容器像运行中的软件。
数据卷像软件的数据目录。
端口映射像把服务入口暴露出来。
```

### 3.3 Docker Compose 是什么

Docker Compose 用一个 YAML 文件管理多个容器。

如果不用 Compose，你要分别执行：

```text
docker run mysql ...
docker run redis ...
docker run minio ...
```

使用 Compose：

```powershell
docker compose up -d
```

一次启动多个服务。

### 3.4 Docker CLI、Docker Daemon、Docker Compose 的区别

| 名称 | 作用 |
|---|---|
| Docker Daemon | 后台服务，真正管理容器 |
| docker 命令 | 客户端命令，用来和 Daemon 通信 |
| Docker Compose | 编排工具，用 YAML 文件批量管理容器 |

如果 Docker Daemon 没启动，`docker ps` 也会失败。

## 4. IIMS 需要哪些中间件

### 4.1 基础中间件

| 服务 | 端口 | 项目用途 | 第一阶段是否必须 |
|---|---:|---|---|
| MySQL | 3306 | 用户、角色、菜单、档案、模型配置 | 必须 |
| Redis | 6379 | 登录密钥、Token、会话状态、缓存 | 必须 |
| MinIO | 9000 / 9001 | 文件上传、头像、档案附件、知识库文件 | 强烈建议 |

### 4.2 AI/RAG 增强中间件

| 服务 | 项目用途 | 第一阶段是否必须 |
|---|---|---|
| Milvus | 向量存储、知识库相似度检索 | RAG 阶段必须 |
| Ollama | 本地大模型或本地 Embedding | 可选 |

### 4.3 为什么第四课先讲 MySQL、Redis、MinIO

因为这三个服务支撑项目主体功能：

```text
登录 -> MySQL + Redis
菜单 -> MySQL
文件上传 -> MinIO + MySQL
档案附件 -> MinIO + MySQL
知识库文件 -> MinIO + MySQL
```

Milvus 和 Ollama 属于后续 AI 增强阶段，不建议第一天就全部压上去。

## 5. 服务器资源现实判断

你的阿里云 ECS 是轻量配置，约：

```text
2 vCPU
2 GB 内存
40 GB 磁盘
```

适合：

```text
Nginx
Spring Boot Jar
MySQL
Redis
MinIO
```

不太适合：

```text
大型 Ollama 模型
完整 Milvus 栈
高并发 AI 推理
```

教学建议：

```text
第一阶段：ECS 跑 Web 系统 + MySQL + Redis + MinIO。
第二阶段：AI 语言模型使用 OpenAI/DeepSeek 这类云 API。
第三阶段：RAG 再考虑 Milvus，可以本机、单独服务器或更高配置机器运行。
```

这不是偷懒，而是合理资源规划。

## 6. 安装和检查 Docker

### 6.1 Windows 本地检查

如果使用 Docker Desktop：

```powershell
docker version
docker compose version
docker ps
```

正确结果应该能看到：

```text
Client
Server
Docker Compose version
```

如果 `docker version` 只有 Client，没有 Server，通常是 Docker Desktop 没启动。

### 6.2 Linux 服务器检查

```bash
docker version
docker compose version
docker ps
```

检查 Docker 服务：

```bash
systemctl status docker
```

启动 Docker：

```bash
systemctl start docker
systemctl enable docker
```

### 6.3 常见错误：docker.service does not exist

你之前遇到：

```text
Failed to enable unit: Unit file docker.service does not exist.
```

含义：

```text
系统中没有名为 docker.service 的 Docker 服务。
```

常见原因：

1. Docker 没装。
2. 装的是 Podman，不是真 Docker。
3. 装了 `podman-docker` 兼容包，`docker` 命令像 Docker，但没有 `docker.service`。
4. Docker 安装不完整。

排查：

```bash
which docker
docker version
rpm -qa | grep -E 'docker|podman'
systemctl list-unit-files | grep docker
```

处理方向：

```text
如果是 podman-docker，先确认是否要换成 Docker CE。
如果确实需要 Docker Compose 部署，建议安装 Docker CE 和 compose plugin。
```

### 6.4 Podman 和 Docker 的区别

简单讲：

```text
Podman 可以兼容部分 docker 命令，但它不是 Docker Daemon 模型。
```

学习部署阶段，建议统一使用 Docker CE，减少命令差异。

## 7. 准备 Compose 工作目录

### 7.1 本地目录

当前项目已有：

```text
C:\Users\MoLin\Desktop\IIMS\deploy-bundle\docker-compose.yml
```

进入：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
```

### 7.2 服务器目录

服务器建议：

```bash
mkdir -p /opt/iims
cd /opt/iims
```

Compose 文件放在：

```text
/opt/iims/docker-compose.yml
```

数据目录会自动变成：

```text
/opt/iims/data/mysql
/opt/iims/data/redis
/opt/iims/data/minio
```

### 7.3 为什么要固定目录

因为 Compose 里的数据卷使用相对路径：

```yaml
volumes:
  - ./data/mysql:/var/lib/mysql
```

这里的 `./data/mysql` 是相对于你执行 `docker compose` 的目录。

所以你必须在 Compose 文件所在目录执行：

```bash
docker compose up -d
```

否则数据可能跑到别的目录。

## 8. 读懂 docker-compose.yml

### 8.1 services

```yaml
services:
  mysql:
  redis:
  minio:
```

`services` 下面每一项就是一个容器服务。

### 8.2 image

```yaml
image: mysql:8.0
image: redis:7
image: minio/minio
```

表示使用哪个镜像。

### 8.3 container_name

```yaml
container_name: iims-mysql
```

表示容器名字固定为 `iims-mysql`。

好处：

```text
查日志、进入容器、复制文件时更方便。
```

例如：

```powershell
docker logs iims-mysql
docker exec -it iims-mysql bash
```

### 8.4 restart

```yaml
restart: always
```

表示容器异常退出或 Docker 重启后自动拉起。

部署服务器时很有用。

### 8.5 environment

MySQL：

```yaml
environment:
  MYSQL_ROOT_PASSWORD: root
  MYSQL_DATABASE: iims
  TZ: Asia/Shanghai
```

含义：

```text
root 密码是 root。
初始化创建 iims 数据库。
时区是 Asia/Shanghai。
```

MinIO：

```yaml
environment:
  MINIO_ROOT_USER: minioadmin
  MINIO_ROOT_PASSWORD: minioadmin
```

含义：

```text
MinIO 控制台登录账号密码都是 minioadmin。
```

### 8.6 command

MySQL：

```yaml
command: --character-set-server=utf8mb4 --collation-server=utf8mb4_0900_ai_ci --default-time-zone=+08:00
```

含义：

```text
指定 MySQL 字符集、排序规则和默认时区。
```

MinIO：

```yaml
command: server /data --console-address ":9001"
```

含义：

```text
用 /data 作为对象存储目录。
9001 作为 Web 控制台端口。
```

### 8.7 ports

```yaml
ports:
  - "3306:3306"
```

格式：

```text
宿主机端口:容器端口
```

所以：

```text
宿主机 3306 -> 容器 3306
宿主机 6379 -> 容器 6379
宿主机 9000 -> 容器 9000
宿主机 9001 -> 容器 9001
```

### 8.8 volumes

```yaml
volumes:
  - ./data/mysql:/var/lib/mysql
```

含义：

```text
把容器内 /var/lib/mysql 挂载到宿主机 ./data/mysql。
```

好处：

```text
容器删除后，数据还在宿主机目录。
```

## 9. 启动中间件

### 9.1 进入 Compose 目录

本地：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
```

服务器：

```bash
cd /opt/iims
```

### 9.2 启动全部服务

```powershell
docker compose up -d
```

Linux 同样：

```bash
docker compose up -d
```

### 9.3 命令解释

| 命令 | 含义 |
|---|---|
| `docker compose` | 使用 Compose |
| `up` | 创建并启动服务 |
| `-d` | 后台运行 |

### 9.4 第一次启动会发生什么

第一次执行可能会：

```text
拉取 mysql:8.0 镜像
拉取 redis:7 镜像
拉取 minio/minio 镜像
创建网络
创建容器
创建 data 目录
启动容器
```

镜像下载慢是正常现象。

## 10. 检查容器状态

### 10.1 查看运行中的容器

```powershell
docker ps
```

应该看到：

```text
iims-mysql
iims-redis
iims-minio
```

状态应该是：

```text
Up
```

### 10.2 查看所有容器

如果没看到，查看所有容器：

```powershell
docker ps -a
```

如果状态是 `Exited`，说明容器启动后退出了，要看日志。

### 10.3 查看 Compose 状态

```powershell
docker compose ps
```

注意要在 Compose 文件目录执行。

## 11. 查看日志

### 11.1 MySQL 日志

```powershell
docker logs iims-mysql --tail=100
```

关注：

```text
ready for connections
```

### 11.2 Redis 日志

```powershell
docker logs iims-redis --tail=100
```

关注：

```text
Ready to accept connections
```

### 11.3 MinIO 日志

```powershell
docker logs iims-minio --tail=100
```

关注：

```text
API:
WebUI:
```

### 11.4 持续查看日志

```powershell
docker logs -f iims-mysql
```

退出：

```text
Ctrl + C
```

## 12. 验证 MySQL

### 12.1 进入 MySQL 容器

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

输入密码：

```text
root
```

### 12.2 查看数据库

```sql
SHOW DATABASES;
```

应该有：

```text
iims
```

### 12.3 选择数据库

```sql
USE iims;
SHOW TABLES;
```

如果刚启动容器，还没有导入 `init-data.sql`，这里可能没有项目表。

第四课的重点是容器启动，第 3 课负责 SQL 导入。

### 12.4 从宿主机连接 MySQL

```powershell
mysql -h 127.0.0.1 -P 3306 -uroot -p
```

密码：

```text
root
```

如果能连接，说明端口映射正常。

## 13. 验证 Redis

### 13.1 进入 Redis CLI

```powershell
docker exec -it iims-redis redis-cli
```

### 13.2 ping 测试

```text
PING
```

正确返回：

```text
PONG
```

### 13.3 查看 key

```text
KEYS *
```

初始可能为空。

登录后可能出现 Token、RSA 密钥或 Sa-Token 相关 key。

### 13.4 退出

```text
exit
```

## 14. 验证 MinIO

### 14.1 访问控制台

浏览器打开：

```text
http://127.0.0.1:9001
```

服务器公网访问：

```text
http://服务器IP:9001
```

如果服务器安全组没有开放 9001，公网打不开是正常的。

### 14.2 登录账号

```text
用户名：minioadmin
密码：minioadmin
```

### 14.3 创建 bucket

项目配置里：

```yaml
iims:
  minio:
    bucketName: iims-bucket
```

所以需要 bucket：

```text
iims-bucket
```

可以在 MinIO 控制台创建。

### 14.4 为什么 bucket 必须存在

项目上传文件时会使用配置：

```text
iims.minio.bucketName
```

如果 bucket 不存在，上传可能失败。

### 14.5 MinIO 端口区别

| 端口 | 用途 |
|---:|---|
| 9000 | API 端口，后端 Java 用它上传下载文件 |
| 9001 | 控制台端口，人在浏览器里管理 bucket |

后端配置应该指向：

```text
http://127.0.0.1:9000
```

不是：

```text
http://127.0.0.1:9001
```

## 15. 和 application-dev.yml 对齐

当前 `application-dev.yml` 中：

```yaml
iims:
  datasource:
    host: 127.0.0.1
    port: 3306
    database: iims
    username: root
    password: root

  minio:
    endpoint: http://127.0.0.1:9000
    accessKey: minioadmin
    secretKey: minioadmin
    bucketName: iims-bucket

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

需要和 Compose 对齐：

| 配置 | Compose | application-dev |
|---|---|---|
| MySQL 端口 | `3306:3306` | `port: 3306` |
| MySQL 密码 | `MYSQL_ROOT_PASSWORD: root` | `password: root` |
| MySQL 数据库 | `MYSQL_DATABASE: iims` | `database: iims` |
| Redis 端口 | `6379:6379` | `port: 6379` |
| MinIO API | `9000:9000` | `endpoint: http://127.0.0.1:9000` |
| MinIO 账号 | `minioadmin` | `accessKey: minioadmin` |
| MinIO 密码 | `minioadmin` | `secretKey: minioadmin` |
| Bucket | 需要创建 | `bucketName: iims-bucket` |

## 16. 停止、重启、删除

### 16.1 停止容器

```powershell
docker compose stop
```

停止但不删除容器和数据。

### 16.2 启动已停止容器

```powershell
docker compose start
```

### 16.3 重启服务

```powershell
docker compose restart
```

也可以重启单个：

```powershell
docker compose restart mysql
docker compose restart redis
docker compose restart minio
```

### 16.4 删除容器但保留数据

```powershell
docker compose down
```

因为数据挂载到：

```text
./data/mysql
./data/redis
./data/minio
```

所以一般数据还在。

### 16.5 删除容器和数据的危险操作

如果你手动删除：

```text
./data/mysql
./data/redis
./data/minio
```

数据会丢。

生产或重要环境不要随便删。

## 17. 数据持久化原理

### 17.1 不挂载数据会怎样

如果 MySQL 数据只在容器内部：

```text
删除容器后数据库数据可能一起没了。
```

### 17.2 当前项目怎么持久化

Compose 中：

```yaml
volumes:
  - ./data/mysql:/var/lib/mysql
```

含义：

```text
MySQL 真正的数据文件存在宿主机 ./data/mysql。
```

所以即使容器删除，只要 `./data/mysql` 还在，数据就能保留。

### 17.3 教学提醒

学生必须知道：

```text
容器不是数据。
数据目录才是数据。
```

这句话非常重要。

## 18. 端口占用排查

### 18.1 Windows 检查端口

```powershell
netstat -ano | findstr :3306
netstat -ano | findstr :6379
netstat -ano | findstr :9000
netstat -ano | findstr :9001
```

如果有输出，说明端口被占用。

### 18.2 查看进程

```powershell
tasklist /FI "PID eq 进程ID"
```

### 18.3 Linux 检查端口

```bash
ss -lntp | grep -E ':3306|:6379|:9000|:9001'
```

或：

```bash
netstat -lntp | grep 3306
```

### 18.4 端口占用怎么处理

两种方案：

1. 关闭占用端口的旧服务。
2. 修改 Compose 左侧宿主机端口。

例如本机已有 MySQL 占用 3306，可以改成：

```yaml
ports:
  - "3307:3306"
```

然后后端配置也要改：

```yaml
iims:
  datasource:
    port: 3307
```

注意：

```text
只改 Compose 不改 application-dev，后端还是连错端口。
```

## 19. 镜像下载慢或失败

### 19.1 表现

```text
pull access denied
timeout
connection reset
TLS handshake timeout
```

### 19.2 可能原因

1. 网络不稳定。
2. Docker Hub 访问慢。
3. 镜像名写错。
4. 服务器 DNS 问题。

### 19.3 处理方向

```text
配置 Docker 镜像加速。
换网络。
确认镜像名。
重试 docker compose pull。
```

命令：

```powershell
docker compose pull
docker compose up -d
```

## 20. 容器启动失败排查

### 20.1 查看状态

```powershell
docker ps -a
```

如果看到：

```text
Exited
Restarting
```

就看日志。

### 20.2 查看日志

```powershell
docker logs iims-mysql --tail=200
docker logs iims-redis --tail=200
docker logs iims-minio --tail=200
```

### 20.3 MySQL 常见失败

| 日志 | 可能原因 |
|---|---|
| permission denied | 数据目录权限问题 |
| port is already allocated | 端口占用 |
| database files are incompatible | 旧数据目录版本不兼容 |
| ready for connections | 正常 |

### 20.4 Redis 常见失败

| 日志 | 可能原因 |
|---|---|
| permission denied | 数据目录权限问题 |
| port is already allocated | 端口占用 |
| Ready to accept connections | 正常 |

### 20.5 MinIO 常见失败

| 日志 | 可能原因 |
|---|---|
| unable to use the drive | 数据目录权限问题 |
| invalid credentials | 账号密码配置不符合要求 |
| API / WebUI | 正常 |

## 21. 导入 SQL 到 Docker MySQL

虽然第三课已经讲过数据库初始化，但这里补充 Docker 场景。

### 21.1 宿主机有 mysql 客户端

```powershell
mysql -h 127.0.0.1 -P 3306 -uroot -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

### 21.2 宿主机没有 mysql 客户端

复制 SQL 到容器：

```powershell
docker cp C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql iims-mysql:/tmp/init-data.sql
```

进入 MySQL：

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

执行：

```sql
USE iims;
SOURCE /tmp/init-data.sql;
```

### 21.3 验证导入

```sql
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
SELECT COUNT(*) FROM iims_integral_user;
```

## 22. MinIO Bucket 初始化

### 22.1 手工创建

浏览器打开：

```text
http://127.0.0.1:9001
```

登录：

```text
minioadmin / minioadmin
```

创建 bucket：

```text
iims-bucket
```

### 22.2 为什么不建议初学者一开始自动脚本化

MinIO 可以用 `mc` 客户端自动创建 bucket，但第四课先手工做，原因是：

```text
学生需要先理解 9000 和 9001 的区别。
学生需要亲眼看到 bucket。
学生需要知道后端配置里的 bucketName 对应什么。
```

后续部署课再补自动化脚本。

## 23. 安全组和公网访问

### 23.1 本地和公网的区别

本地访问：

```text
127.0.0.1:3306
127.0.0.1:6379
127.0.0.1:9000
127.0.0.1:9001
```

公网访问：

```text
服务器IP:端口
```

公网能不能访问，取决于：

1. 容器是否监听。
2. 服务器防火墙是否开放。
3. 云厂商安全组是否开放。
4. 服务是否绑定到正确网卡。

### 23.2 哪些端口不建议公网开放

不建议公网开放：

```text
3306 MySQL
6379 Redis
```

原因：

```text
数据库和缓存直接暴露公网风险很高。
```

可以公网开放：

```text
80 前端
443 HTTPS
8090 后端测试阶段可临时开放
9001 MinIO 控制台仅学习阶段临时开放
```

生产更推荐：

```text
后端通过 Nginx 反向代理。
MinIO 控制台限制来源 IP。
数据库和 Redis 只允许内网访问。
```

### 23.3 学习阶段建议

你的学习项目可以：

```text
80 开放
8090 临时开放
9001 临时开放用于管理 MinIO
3306 和 6379 不开放公网
```

## 24. Milvus 什么时候加

### 24.1 项目哪里用 Milvus

源码中：

```text
iims-module-ai/src/main/java/cn/aitenry/iims/ai/rag/service/impl/CustomizeVectorStoreServiceImpl.java
iims-module-ai/src/main/java/cn/aitenry/iims/ai/rag/service/impl/MilvusStoreServiceImpl.java
```

配置：

```yaml
iims:
  vector:
    host: localhost
```

作用：

```text
知识库文档向量化后，存入 Milvus。
用户提问时，根据问题向量相似度检索相关文档片段。
```

### 24.2 为什么第四课不强制启动 Milvus

因为 Milvus 比 MySQL、Redis、MinIO 重很多。

2GB ECS 上同时跑：

```text
MySQL + Redis + MinIO + Spring Boot + Nginx + Milvus
```

很容易内存不足。

### 24.3 推荐策略

```text
第 4 课：先启动 MySQL、Redis、MinIO，保证基础系统可用。
第 27 课：讲 RAG 文档处理。
第 28 课：专门启动和配置 Milvus。
```

### 24.4 面试怎么讲

可以说：

> 项目第一阶段部署时，我先把 MySQL、Redis、MinIO 容器化，保证登录、权限、文件、档案这些基础业务稳定运行。Milvus 属于 RAG 增强链路，对资源要求更高，所以我把它放在知识库问答阶段单独配置，避免在低配服务器上一开始就把所有服务堆在一起导致系统不稳定。

## 25. 与后端启动的关系

### 25.1 正确启动顺序

```text
第 1 步：docker compose up -d
第 2 步：docker ps 检查 MySQL/Redis/MinIO
第 3 步：导入 SQL 到 MySQL
第 4 步：创建 MinIO bucket
第 5 步：启动 Spring Boot 后端
第 6 步：启动前端
第 7 步：登录验证
```

### 25.2 如果顺序反了

后端先启动，中间件没起来，可能出现：

```text
MySQL connection refused
Redis connection refused
MinIO upload failed
登录接口 500
文件上传失败
```

这不是代码一定坏了，而是依赖服务没准备好。

## 26. 课堂演示脚本

### 26.1 开场

可以这样讲：

> 前三课我们已经知道项目依赖 MySQL、Redis、MinIO。今天我们不用手工一个个安装，而是用 Docker Compose 启动它们。重点不是背命令，而是看懂每个服务为什么存在、端口怎么映射、数据为什么不会丢。

### 26.2 演示 Compose 文件

命令：

```powershell
Get-Content C:\Users\MoLin\Desktop\IIMS\deploy-bundle\docker-compose.yml
```

讲解：

> 这里有三个 services：mysql、redis、minio。每个服务都有 image、container_name、ports、volumes。我们要逐行读懂。

### 26.3 演示启动

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
docker compose up -d
```

讲解：

> 第一次启动会拉镜像，慢一点正常。`-d` 表示后台运行。

### 26.4 演示检查状态

```powershell
docker ps
docker compose ps
```

讲解：

> 看到 `iims-mysql`、`iims-redis`、`iims-minio` 都是 Up，说明容器运行起来了。

### 26.5 演示日志

```powershell
docker logs iims-mysql --tail=50
docker logs iims-redis --tail=50
docker logs iims-minio --tail=50
```

讲解：

> 容器启动失败时，不要猜，先看日志。

### 26.6 演示 MySQL 验证

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

进入后：

```sql
SHOW DATABASES;
```

### 26.7 演示 Redis 验证

```powershell
docker exec -it iims-redis redis-cli
PING
```

### 26.8 演示 MinIO 验证

浏览器打开：

```text
http://127.0.0.1:9001
```

登录：

```text
minioadmin / minioadmin
```

创建：

```text
iims-bucket
```

### 26.9 课堂收束

讲：

> 现在基础中间件已经具备了。下一步启动后端时，后端才能连 MySQL、Redis、MinIO。以后重启服务器后，只要数据目录还在，执行 `docker compose up -d` 就能把中间件恢复起来。

## 27. 学生课堂练习

### 27.1 练习 1：读 Compose

填写：

| 服务 | 镜像 | 容器名 | 宿主机端口 | 容器端口 | 数据目录 |
|---|---|---|---:|---:|---|
| MySQL |  |  |  |  |  |
| Redis |  |  |  |  |  |
| MinIO API |  |  |  |  |  |
| MinIO Console |  |  |  |  |  |

### 27.2 练习 2：启动服务

执行：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\deploy-bundle
docker compose up -d
docker ps
```

记录：

```text
MySQL 状态：
Redis 状态：
MinIO 状态：
```

### 27.3 练习 3：验证服务

MySQL：

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

Redis：

```powershell
docker exec -it iims-redis redis-cli
PING
```

MinIO：

```text
浏览器访问 http://127.0.0.1:9001
```

### 27.4 练习 4：端口排查

执行：

```powershell
netstat -ano | findstr :3306
netstat -ano | findstr :6379
netstat -ano | findstr :9000
netstat -ano | findstr :9001
```

记录每个端口是否被监听。

### 27.5 练习 5：错误归类

填写：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `docker: command not found` |  |  |
| `docker.service does not exist` |  |  |
| `port is already allocated` |  |  |
| `iims-mysql Exited` |  |  |
| MinIO 控制台打不开 |  |  |
| 后端连不上 Redis |  |  |

参考答案：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `docker: command not found` | Docker 未安装或 PATH 问题 | 安装 Docker 或检查 PATH |
| `docker.service does not exist` | Docker 服务不存在，可能装的是 Podman | 检查安装包，安装 Docker CE |
| `port is already allocated` | 端口占用 | 查占用进程或改端口 |
| `iims-mysql Exited` | 容器启动失败 | `docker logs iims-mysql` |
| MinIO 控制台打不开 | 端口/服务/安全组问题 | 查 9001、日志、安全组 |
| 后端连不上 Redis | Redis 未启动或配置不匹配 | 查 `docker ps` 和 `spring.data.redis` |

## 28. 本课验收标准

### 28.1 容器验收

必须看到：

```powershell
docker ps
```

包含：

```text
iims-mysql
iims-redis
iims-minio
```

并且状态是：

```text
Up
```

### 28.2 MySQL 验收

能进入：

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

并执行：

```sql
SHOW DATABASES;
```

看到：

```text
iims
```

### 28.3 Redis 验收

能执行：

```powershell
docker exec -it iims-redis redis-cli
PING
```

返回：

```text
PONG
```

### 28.4 MinIO 验收

能打开：

```text
http://127.0.0.1:9001
```

并创建：

```text
iims-bucket
```

### 28.5 认知验收

能解释：

1. `3306:3306` 的含义。
2. `./data/mysql:/var/lib/mysql` 的含义。
3. 9000 和 9001 的区别。
4. 为什么容器删除不等于数据一定删除。
5. 为什么不建议公网开放 MySQL 和 Redis。
6. 为什么 Milvus 不在第四课强制启动。

## 29. 本课作业

### 作业 1：Compose 注释

把 `docker-compose.yml` 每一段写上中文解释。

要求说明：

```text
image
container_name
restart
environment
command
ports
volumes
```

### 作业 2：启动记录

提交：

```text
执行目录：
启动命令：
docker ps 输出：
MySQL 验证结果：
Redis PING 结果：
MinIO 控制台访问结果：
bucket 是否创建：
```

### 作业 3：端口与配置对照表

填写：

| 服务 | Compose 端口 | 后端配置 | 是否一致 |
|---|---|---|---|
| MySQL |  |  |  |
| Redis |  |  |  |
| MinIO |  |  |  |

### 作业 4：错误复盘

写 200 字：

```text
如果 docker compose up -d 后后端仍然连不上 MySQL/Redis/MinIO，我会如何排查？
```

必须包含：

- `docker ps`
- `docker logs`
- 端口检查
- 后端配置检查
- 容器数据目录检查

### 作业 5：面试表达

准备 1 分钟说明：

```text
我如何用 Docker Compose 管理 IIMS 的基础中间件？
```

## 30. 面试表达

### 30.1 初级表达

> 我使用 Docker Compose 启动了 IIMS 项目依赖的 MySQL、Redis 和 MinIO。MySQL 存业务数据，Redis 支撑登录状态，MinIO 用来存文件。通过 `docker ps`、`docker logs`、Redis `PING` 和 MinIO 控制台验证服务是否正常。

### 30.2 更好的表达

> 我把 IIMS 的基础中间件用 Docker Compose 管理，包含 MySQL 8、Redis 7 和 MinIO。Compose 中固定了容器名、端口映射和数据挂载目录，例如 MySQL 的 `3306:3306` 对应后端 `application-dev.yml` 的数据库端口，`./data/mysql:/var/lib/mysql` 保证容器重建后数据不丢。启动后我会用 `docker ps` 查看状态，用 `docker logs` 排查启动错误，用 MySQL 客户端验证 `iims` 数据库，用 Redis `PING` 验证连接，用 MinIO 控制台创建 `iims-bucket`。Milvus 属于 RAG 增强链路，对资源要求更高，所以我放到知识库阶段单独部署。

### 30.3 面试官可能追问

#### 问：Docker Compose 解决了什么问题？

答：

> 它把多个中间件服务写成一个可复现的 YAML 文件，统一启动、停止、查看日志和挂载数据。这样换电脑或部署服务器时，不需要手工重复安装 MySQL、Redis、MinIO，也减少环境不一致问题。

#### 问：`3306:3306` 是什么意思？

答：

> 左边是宿主机端口，右边是容器端口。`3306:3306` 表示访问宿主机的 3306 会转发到 MySQL 容器的 3306。如果左边改成 3307，后端配置也要改成 3307。

#### 问：为什么要挂载数据卷？

答：

> 容器本身可以删除和重建，如果数据库数据只存在容器内部，删除容器可能导致数据丢失。挂载 `./data/mysql:/var/lib/mysql` 后，MySQL 数据保存在宿主机目录里，容器重建后仍可复用。

#### 问：为什么不建议公网开放 Redis？

答：

> Redis 默认不是面向公网暴露的服务，公网开放会带来未授权访问、数据泄露和被攻击风险。生产中 Redis 应该只允许内网访问，或者至少做强密码、访问控制和安全组限制。

#### 问：为什么没有一开始就把 Milvus 放进 Compose？

答：

> Milvus 主要服务于 RAG 向量检索，资源占用比 MySQL、Redis、MinIO 更高。项目第一阶段的目标是登录、权限、文件和基础业务可用，所以先启动基础中间件。等知识库问答阶段，再根据服务器资源单独部署 Milvus，更稳妥。

## 31. 本课最终交付物

本课结束后，学生应提交：

1. 注释版 `docker-compose.yml` 理解笔记。
2. Docker Compose 启动记录。
3. MySQL、Redis、MinIO 三个服务的验证结果。
4. 端口与后端配置对照表。
5. 一份中间件常见错误排查表。
6. 一段 1 分钟 Docker Compose 面试表达。

完成这些，第四课才算真正过关。

