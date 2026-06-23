# 第 15 课：Redis 在项目中的定位

> 课程定位：这一课解决“Redis 到底在这个项目里做什么、为什么登录需要 Redis、Redis 挂了会出现什么问题”。IIMS 中 Redis 不是摆设，它参与登录密钥、Token/会话、缓存和异步状态。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 找到 Redis 配置。
2. 理解 Redis 在登录 RSA 密钥中的作用。
3. 理解 Redis 和 Sa-Token 的关系。
4. 会用 redis-cli 检查 key。
5. 能排查 Redis 连接失败、登录状态异常。
6. 能说出缓存和业务数据的区别。

## 2. 源码定位

配置：

```text
iims-starter/src/main/resources/application.yml
```

相关代码：

```text
iims-module-integral/src/main/java/cn/aitenry/iims/integral/service/impl/UserServiceImpl.java
iims-module-auth/src/main/java/cn/aitenry/iims/auth/config/SaTokenConfigure.java
iims-module-integral/src/main/java/cn/aitenry/iims/integral/controller/UserController.java
```

Docker：

```text
deploy-bundle/docker-compose.yml
```

## 3. Redis 配置

```yaml
spring:
  data:
    redis:
      database: 0
      host: localhost
      port: 6379
      jedis:
        pool:
          max-active: 1000
          max-wait: -1ms
          max-idle: 16
          min-idle: 8
```

含义：

- 连接 `localhost:6379`。
- 使用 0 号库。
- Jedis 连接池。

## 4. Docker Redis

```yaml
redis:
  image: redis:7
  container_name: iims-redis
  restart: always
  ports:
    - "6379:6379"
  volumes:
    - ./data/redis:/data
```

验证：

```powershell
docker exec -it iims-redis redis-cli
PING
```

返回：

```text
PONG
```

## 5. Redis 在登录中的作用

登录密钥流程：

```text
前端请求 /login/key
后端生成 RSA 公钥和私钥
公钥返回前端
私钥放入 Redis
前端加密密码提交登录
后端从 Redis 取私钥解密
```

如果 Redis 不通：

- 登录密钥无法保存。
- 登录时找不到私钥。
- 登录失败。

## 6. Redis 和 Sa-Token

Sa-Token 可结合 Redis 存登录状态。

项目依赖中有：

```text
sa-token-dao-redis
sa-token-alone-redis
spring-boot-starter-data-redis
```

说明登录会话和 token 状态可能进入 Redis。

## 7. 缓存和数据库区别

MySQL：

```text
长期业务数据，不能随便丢。
```

Redis：

```text
临时状态、缓存、会话、密钥、热点数据。
```

Redis 数据丢失通常不应该导致业务主数据丢失，但可能导致用户重新登录或临时任务失败。

## 8. 查看 Redis key

进入：

```powershell
docker exec -it iims-redis redis-cli
```

查看：

```text
KEYS *
```

生产不建议大规模使用 `KEYS *`，因为会阻塞；学习环境可以用。

推荐：

```text
SCAN 0
```

## 9. 常见错误

### 9.1 Redis 连接失败

表现：

```text
RedisConnectionFailureException
Connection refused
```

排查：

```powershell
docker ps
docker logs iims-redis --tail=100
netstat -ano | findstr :6379
```

### 9.2 登录后立刻失效

排查：

- Redis 是否重启清空。
- token 是否写入。
- Sa-Token 配置是否正确。
- 前端请求头是否带 token。

### 9.3 本地能连，服务器不能连

排查：

- 后端和 Redis 是否同机。
- Redis 端口映射。
- host 是 localhost 还是容器服务名。
- 安全组不要公网开放 Redis。

## 10. 实操任务

1. 启动 Redis 容器。
2. 执行 `PING`。
3. 请求 `/iims/user/login/key`。
4. 查看 Redis key 是否变化。
5. 登录后再次查看 key。
6. 停 Redis，观察登录接口错误。

## 11. 验收标准

学生必须能回答：

1. Redis 在登录中保存什么？
2. Redis 挂了会影响哪些功能？
3. Redis 和 MySQL 的职责区别？
4. 为什么不建议公网开放 Redis？
5. 如何用 redis-cli 验证 Redis？

## 12. 面试表达

> IIMS 中 Redis 主要承担登录临时状态和会话缓存。登录前端先请求公钥，后端生成 RSA 密钥对后把私钥临时存入 Redis，登录时再取出私钥解密密码。同时项目引入了 Sa-Token Redis 相关依赖，登录状态也可能依赖 Redis。排查登录问题时，我会检查 Redis 容器状态、`PING`、key 写入情况、前端 token 请求头和后端 Sa-Token 配置。

