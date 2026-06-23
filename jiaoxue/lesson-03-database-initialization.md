# 第 3 课：数据库初始化与启动错误修复

> 课程定位：这一课解决“后端为什么连不上库、登录为什么报表不存在、SQL 为什么导入不完整”。IIMS 的登录、菜单、角色、档案、知识库、模型配置都依赖 MySQL 初始化数据。数据库不正确，前端页面再漂亮、后端代码再完整，也无法正常运行。

## 1. 本课目标

### 1.1 教学目标

学完本课后，学生应该能做到：

1. 理解 IIMS 为什么必须先初始化 MySQL。
2. 创建 `iims` 数据库，并使用正确字符集。
3. 导入 `resources/sql/init-data.sql`。
4. 验证核心表是否存在。
5. 验证初始用户、角色、菜单、模型配置数据是否存在。
6. 读懂后端 MySQL 连接配置来自哪里。
7. 理解 `application.yml` 与 `application-dev.yml` 的覆盖关系。
8. 理解 Druid 密码解密配置为什么会影响本地启动。
9. 能处理 `Table doesn't exist`、`Access denied`、`Unknown database`、`Communications link failure` 等常见错误。
10. 能用数据库排错过程支撑后续面试表达。

### 1.2 就业目标

企业后台项目里，数据库初始化问题特别常见：

- 开发环境和生产环境数据库不一致。
- SQL 导入到一半失败。
- 代码连接了错误的库。
- 表名写错。
- 字符集导致中文乱码。
- 本地密码是明文，生产密码是密文。
- ORM 报错看起来像 Java 问题，实际是表结构问题。

这一课训练的是后端工程师必须具备的能力：

> 看到数据库相关报错时，能沿着“配置 -> 连接 -> 库 -> 表 -> 数据 -> SQL 映射”一步一步排查，而不是只盯着报错最后一行。

## 2. 本课涉及的项目文件

本课重点文件：

```text
C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application.yml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application-dev.yml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-integral\src\main\resources\mapper\UserMapper.xml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-ai\src\main\resources\mapper\AiChatModelsMapper.xml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-ai\src\main\resources\mapper\AiChatSettingsMapper.xml
```

本课重点表：

```text
iims_integral_user
iims_integral_role
iims_integral_menu
iims_integral_permission
iims_ai_chat_models
iims_ai_chat_settings
iims_integral_wiki
iims_integral_warehouse
iims_archive_metadata
```

## 3. 课前准备

### 3.1 需要的软件

至少准备一种 MySQL 操作方式：

| 工具 | 适合人群 |
|---|---|
| MySQL 命令行 | 最稳定，适合学习底层操作 |
| Navicat | 图形化方便，但通常收费 |
| DBeaver | 免费图形化工具，适合学习 |
| DataGrip | JetBrains 数据库 IDE |
| Docker MySQL | 推荐项目部署时使用 |

本课会优先用命令行讲解，因为命令最可复现。

### 3.2 MySQL 版本建议

推荐：

```text
MySQL 8.0
```

原因：

- 项目使用 MySQL Connector 9.0。
- SQL 里有 JSON 字段和 JSON 查询。
- 后端 JDBC URL 已经包含 MySQL 8 常见参数。

### 3.3 先不要急着启动后端

本课开始前，不建议先运行后端。

正确顺序：

```text
先确认 MySQL 服务启动
再创建 iims 数据库
再导入 init-data.sql
再检查核心表和数据
再启动后端
最后测试登录相关接口
```

如果反过来，后端会因为数据库问题报一堆错误，反而不好判断。

## 4. 数据库在 IIMS 中的作用

### 4.1 为什么数据库是基础设施

IIMS 不是静态页面，它的所有后台能力都要查数据库：

| 功能 | 依赖数据库内容 |
|---|---|
| 登录 | 用户名、密码、用户状态 |
| 菜单 | 菜单表、角色菜单绑定 |
| 权限 | 角色、权限字符 |
| 档案 | 档案类型、档案元数据 |
| 文件 | 文件元数据、对象存储路径 |
| 知识库 | Wiki、目录、文件关联 |
| AI 模型 | 模型名称、类型、URL、API Key |
| AI 默认设置 | 用户默认聊天模型、Embedding 模型 |

所以数据库初始化不是可选步骤。

### 4.2 登录为什么最先暴露数据库问题

登录链路大概是：

```text
前端登录页
-> 请求后端登录接口
-> 后端查询 iims_integral_user
-> 校验密码和用户状态
-> 查询用户角色 iims_integral_role
-> 查询菜单 iims_integral_menu
-> 生成 Token
-> 返回用户信息和权限
```

所以只要用户表、角色表、菜单表任意缺失，登录或登录后的菜单加载都会异常。

### 4.3 典型错误

你之前遇到：

```text
Uncaught (in promise) Error: Table 'iims.iims_integral_user' doesn't exist
```

这句话说明：

```text
前端收到了后端返回的错误。
后端正在访问 MySQL 的 iims 数据库。
SQL 里查询了 iims_integral_user 表。
但是 MySQL 中没有这张表。
```

它不属于：

```text
Vue 页面错误。
Vite 错误。
按钮点击错误。
AI 模型错误。
Redis 错误。
```

它属于：

```text
MySQL 表结构初始化错误。
```

## 5. 查看后端数据库配置

### 5.1 主配置 application.yml

文件：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application.yml
```

关键配置：

```yaml
spring:
  profiles:
    active: dev
  datasource:
    druid:
      driver-class-name: ${iims.datasource.driver-class-name}
      url: jdbc:mysql://${iims.datasource.host}:${iims.datasource.port}/${iims.datasource.database}?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull
      username: ${iims.datasource.username}
      password: ${iims.datasource.password}
      connection-properties: config.decrypt=true;config.decrypt.key=${iims.datasource.publicKey}
      filter:
        config:
          enabled: true
```

这里要看懂三件事：

1. 后端使用 Druid 数据源。
2. JDBC URL 用了 `${iims.datasource.*}` 占位符。
3. 主配置默认开启了 Druid 密码解密。

### 5.2 开发配置 application-dev.yml

文件：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application-dev.yml
```

关键配置：

```yaml
iims:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: 127.0.0.1
    port: 3306
    database: iims
    username: root
    password: root
    publicKey: root

spring:
  datasource:
    druid:
      filter:
        config:
          enabled: false
      connection-properties: config.decrypt=false
```

这说明本地开发连接：

```text
host: 127.0.0.1
port: 3306
database: iims
username: root
password: root
```

同时本地关闭：

```text
Druid 密码解密
```

### 5.3 为什么要关闭 Druid 解密

主配置里有：

```yaml
connection-properties: config.decrypt=true;config.decrypt.key=${iims.datasource.publicKey}
```

这通常用于生产环境：

```text
数据库密码不是明文，而是 Druid 加密后的密文。
```

但本地开发时，我们通常写：

```yaml
password: root
```

这是明文密码。

如果不关闭解密，Druid 可能会把 `root` 当成密文去解，导致连接失败。

所以本地开发建议：

```yaml
spring:
  datasource:
    druid:
      filter:
        config:
          enabled: false
      connection-properties: config.decrypt=false
```

### 5.4 教学重点

学生必须理解：

```text
application.yml 是主配置。
spring.profiles.active=dev 表示还会加载 application-dev.yml。
application-dev.yml 可以覆盖主配置里的部分配置。
最终生效的是合并后的配置。
```

## 6. 创建数据库

### 6.1 登录 MySQL

本地命令行：

```powershell
mysql -u root -p
```

输入密码后进入 MySQL。

如果 MySQL 在 Docker 中：

```powershell
docker exec -it iims-mysql mysql -uroot -p
```

然后输入容器里的 root 密码。

### 6.2 创建数据库

进入 MySQL 后执行：

```sql
CREATE DATABASE IF NOT EXISTS iims
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 6.3 为什么用 utf8mb4

不要用老的 `utf8`。

原因：

```text
MySQL 的 utf8 不是完整 UTF-8，最多 3 字节。
utf8mb4 才能完整支持中文、特殊符号、emoji 等字符。
```

虽然后台系统不一定需要 emoji，但使用 `utf8mb4` 是更稳妥的习惯。

### 6.4 查看数据库是否存在

```sql
SHOW DATABASES;
```

应该看到：

```text
iims
```

切换数据库：

```sql
USE iims;
```

查看当前数据库：

```sql
SELECT DATABASE();
```

应该返回：

```text
iims
```

## 7. 导入初始化 SQL

### 7.1 SQL 文件位置

```text
C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

### 7.2 命令行导入方式一：PowerShell 中导入

在 PowerShell 执行：

```powershell
mysql -u root -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

输入密码后等待导入完成。

如果没有输出，不一定是失败。MySQL 命令行很多成功操作是静默的。

### 7.3 命令行导入方式二：进入 MySQL 后 source

先进入：

```powershell
mysql -u root -p
```

然后：

```sql
USE iims;
SOURCE C:/Users/MoLin/Desktop/IIMS/resources/sql/init-data.sql;
```

注意：

```text
MySQL source 路径里建议使用正斜杠 /
```

### 7.4 Docker MySQL 导入方式

如果 MySQL 在 Docker 容器中，有两种方式。

方式一：从宿主机导入到映射端口：

```powershell
mysql -h 127.0.0.1 -P 3306 -u root -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

方式二：复制 SQL 到容器再导入：

```powershell
docker cp C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql iims-mysql:/tmp/init-data.sql
docker exec -it iims-mysql mysql -uroot -p iims
```

进入后：

```sql
SOURCE /tmp/init-data.sql;
```

### 7.5 图形化工具导入注意事项

如果用 DBeaver、Navicat、DataGrip：

1. 先连接 MySQL。
2. 先创建 `iims` 数据库。
3. 选中 `iims` 数据库。
4. 执行 SQL 脚本。
5. 看执行日志，不要只看界面有没有报红。

注意：

```text
不要把 SQL 导入到 sys、mysql、test 或其他数据库。
```

## 8. 验证核心表

导入后进入 MySQL：

```sql
USE iims;
SHOW TABLES;
```

### 8.1 验证用户表

```sql
SHOW TABLES LIKE 'iims_integral_user';
```

应该看到：

```text
iims_integral_user
```

查看用户数量：

```sql
SELECT COUNT(*) FROM iims_integral_user;
```

查看初始用户：

```sql
SELECT id, name, username, role, is_disable, is_deleted
FROM iims_integral_user;
```

根据当前 SQL，初始用户包括：

```text
Aitenry
G-001
Q-001
```

密码字段示例：

```text
21232f297a57a5a743894a0e4a801fc3
```

这是 `admin` 的 MD5 值。

所以常见初始登录理解为：

```text
用户名：Aitenry
密码：admin
```

是否最终能登录，还要以后端登录逻辑为准。

### 8.2 验证角色表

```sql
SHOW TABLES LIKE 'iims_integral_role';
SELECT id, role_name, role_en, systemic
FROM iims_integral_role;
```

应该有：

```text
超级管理员
档案管理员
教务管理员
```

角色表很重要，因为用户表里的 `role` 字段保存了角色 ID 数组。

### 8.3 验证菜单表

```sql
SHOW TABLES LIKE 'iims_integral_menu';
SELECT COUNT(*) FROM iims_integral_menu;
```

如果菜单表为空，登录后可能没有菜单。

### 8.4 验证 AI 模型表

```sql
SHOW TABLES LIKE 'iims_ai_chat_models';
SELECT id, rename, name, type, model_type, is_online, is_deleted
FROM iims_ai_chat_models;
```

根据当前 SQL，默认有一条智能体相关数据：

```text
自主规划智能体 / react-agent
```

但注意：

```text
这不等于已经配置了可用 OpenAI/DeepSeek/Ollama 语言模型。
```

后续第 20 到 24 课会专门配置模型。

### 8.5 验证 AI 设置表

```sql
SHOW TABLES LIKE 'iims_ai_chat_settings';
SELECT * FROM iims_ai_chat_settings;
```

如果为空，说明：

```text
用户默认模型可能还没配置。
```

这不会阻止基础登录，但会影响 AI 对话默认模型选择。

### 8.6 验证档案和知识库表

```sql
SHOW TABLES LIKE 'iims_archive_metadata';
SHOW TABLES LIKE 'iims_integral_wiki';
SHOW TABLES LIKE 'iims_integral_warehouse';
```

这些表用于后续：

- 档案管理。
- 知识库。
- 文件云库。

## 9. SQL 导入是否完整的判断方法

### 9.1 不要只看“没有报错”

有些图形化工具执行 SQL 时可能：

- 中间报错后继续执行。
- 只执行了选中的一部分。
- 默认只执行当前语句。
- 导入到了错误数据库。

所以必须验证核心表。

### 9.2 最小表数量检查

执行：

```sql
SELECT COUNT(*) AS table_count
FROM information_schema.tables
WHERE table_schema = 'iims';
```

如果表数量明显很少，例如只有 1、2 张，说明导入不完整。

### 9.3 核心表检查 SQL

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'iims'
AND table_name IN (
  'iims_integral_user',
  'iims_integral_role',
  'iims_integral_menu',
  'iims_ai_chat_models',
  'iims_ai_chat_settings',
  'iims_integral_wiki',
  'iims_integral_warehouse',
  'iims_archive_metadata'
);
```

结果应该能返回这些核心表。

## 10. 后端如何使用这些表

### 10.1 用户表被哪里查询

文件：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-integral\src\main\resources\mapper\UserMapper.xml
```

里面会查询：

```sql
select * from iims_integral_user
```

以及通过用户角色联查角色和菜单。

所以当报：

```text
iims_integral_user doesn't exist
```

要回到：

```text
resources/sql/init-data.sql 是否导入
application-dev.yml 是否连接 iims 数据库
```

### 10.2 模型表被哪里查询

文件：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-ai\src\main\resources\mapper\AiChatModelsMapper.xml
```

里面会查询：

```sql
select * from iims_ai_chat_models where id = #{id} limit 1
```

AI 模型服务代码：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-ai\src\main\java\cn\aitenry\iims\ai\chat\service\impl\ModelServiceImpl.java
```

会根据模型表里的：

```text
type
url
key
name
token
model_type
```

动态创建 OpenAI、DeepSeek 或 Ollama 模型。

### 10.3 用户默认模型设置被哪里查询

文件：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-module-ai\src\main\resources\mapper\AiChatSettingsMapper.xml
```

查询：

```sql
select * from iims_ai_chat_settings where user_id = #{userId} limit 1
```

所以如果 AI 对话或 RAG 报默认模型为空，要检查：

```text
iims_ai_chat_settings
```

## 11. 启动后端前的数据库检查清单

启动后端前，至少完成：

```sql
SHOW DATABASES LIKE 'iims';
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
SELECT COUNT(*) FROM iims_integral_user;
SHOW TABLES LIKE 'iims_integral_role';
SELECT COUNT(*) FROM iims_integral_role;
SHOW TABLES LIKE 'iims_integral_menu';
SELECT COUNT(*) FROM iims_integral_menu;
SHOW TABLES LIKE 'iims_ai_chat_models';
SELECT COUNT(*) FROM iims_ai_chat_models;
```

如果这些都正常，再启动后端。

## 12. 启动后端并验证数据库连接

### 12.1 IDEA 启动

运行：

```text
IimsStarterApplication
```

### 12.2 命令行启动

如果已经打包：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\target
java -jar iims-starter-1.0.0.jar
```

### 12.3 成功标志

日志里看到：

```text
Tomcat started on port 8090
Started IimsStarterApplication
```

### 12.4 验证接口

```powershell
Invoke-WebRequest http://127.0.0.1:8090/iims/user/login/key
```

如果返回 200，说明：

```text
后端服务已启动。
登录密钥接口可访问。
基础路由正常。
```

这一步不等于所有数据库功能都正常，但至少服务可达。

## 13. 常见错误一：Unknown database

### 13.1 错误表现

```text
Unknown database 'iims'
```

### 13.2 含义

后端连接 MySQL 时指定：

```text
database: iims
```

但是 MySQL 中没有这个数据库。

### 13.3 排查

进入 MySQL：

```sql
SHOW DATABASES;
```

如果没有 `iims`，创建：

```sql
CREATE DATABASE IF NOT EXISTS iims
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

然后导入 SQL。

## 14. 常见错误二：Table doesn't exist

### 14.1 错误表现

```text
Table 'iims.iims_integral_user' doesn't exist
```

### 14.2 含义

数据库 `iims` 存在，但表 `iims_integral_user` 不存在。

### 14.3 可能原因

1. 没导入 SQL。
2. SQL 导入失败。
3. SQL 导入到别的数据库。
4. SQL 只执行了一部分。
5. SQL 文件中表名写错。

### 14.4 排查命令

```sql
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
```

如果没有结果，重新导入 SQL。

### 14.5 本项目特殊提醒

这个项目早期 SQL 曾出现过表名相关问题。

你要确认 SQL 中有：

```text
CREATE TABLE `iims_integral_user`
INSERT INTO `iims_integral_user`
```

而不是把用户数据错误写到其他表。

当前课程使用的脚本中，应该能搜索到：

```text
iims_integral_user
```

## 15. 常见错误三：Access denied

### 15.1 错误表现

```text
Access denied for user 'root'@'localhost'
```

### 15.2 含义

用户名或密码错误，或者 MySQL 权限不允许该用户从当前主机连接。

### 15.3 排查

确认配置：

```yaml
iims:
  datasource:
    username: root
    password: root
```

手动测试：

```powershell
mysql -h 127.0.0.1 -P 3306 -u root -p
```

如果手动都登录不了，后端也不可能登录。

### 15.4 处理方向

1. 改 `application-dev.yml` 密码为实际密码。
2. 或修改 MySQL root 密码。
3. 或创建专用用户。

示例：

```sql
CREATE USER 'iims_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON iims.* TO 'iims_user'@'%';
FLUSH PRIVILEGES;
```

然后配置：

```yaml
username: iims_user
password: your_password
```

## 16. 常见错误四：Communications link failure

### 16.1 错误表现

```text
Communications link failure
```

或：

```text
Connection refused
```

### 16.2 含义

后端无法连接到 MySQL 服务。

### 16.3 排查

检查 MySQL 是否启动：

```powershell
netstat -ano | findstr :3306
```

如果 Docker：

```powershell
docker ps
docker logs iims-mysql --tail=100
```

手动连接：

```powershell
mysql -h 127.0.0.1 -P 3306 -u root -p
```

### 16.4 常见原因

| 原因 | 处理 |
|---|---|
| MySQL 没启动 | 启动 MySQL |
| 端口不是 3306 | 修改配置或端口映射 |
| host 配错 | 本地用 127.0.0.1，容器网络按实际配置 |
| 防火墙阻断 | 开放端口 |
| Docker 容器没映射端口 | 检查 compose 配置 |

## 17. 常见错误五：Public Key Retrieval is not allowed

### 17.1 错误表现

```text
Public Key Retrieval is not allowed
```

### 17.2 含义

MySQL 8 用户认证相关问题。

### 17.3 本项目配置

项目 JDBC URL 已经包含：

```text
allowPublicKeyRetrieval=true
```

所以一般不会遇到。

如果遇到，检查后端实际使用的 JDBC URL 是否被改掉。

## 18. 常见错误六：时区问题

### 18.1 错误表现

```text
The server time zone value is unrecognized
```

### 18.2 本项目配置

项目 JDBC URL 包含：

```text
serverTimezone=Asia/Shanghai
```

所以一般也不会遇到。

如果遇到，检查 URL 是否完整。

## 19. 常见错误七：中文乱码

### 19.1 表现

数据库中中文显示为乱码，或前端菜单中文异常。

### 19.2 排查

查看数据库字符集：

```sql
SELECT DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'iims';
```

查看表字符集：

```sql
SHOW FULL COLUMNS FROM iims_integral_user;
```

### 19.3 处理原则

建库使用：

```sql
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci
```

JDBC URL 包含：

```text
useUnicode=true&characterEncoding=UTF-8
```

## 20. 常见错误八：Druid 解密导致连接失败

### 20.1 表现

日志里可能出现 Druid 配置解密相关异常，或数据库密码明明正确但后端连接失败。

### 20.2 原因

主配置里开启了：

```yaml
config.decrypt=true
```

但本地写的是明文密码。

### 20.3 处理

本地开发配置中覆盖：

```yaml
spring:
  datasource:
    druid:
      filter:
        config:
          enabled: false
      connection-properties: config.decrypt=false
```

### 20.4 教学强调

这不是 MySQL 密码一定错，也不是代码错。

这是：

```text
配置策略与本地密码形式不匹配。
```

## 21. 使用 Docker MySQL 的推荐配置

如果使用 Docker，可以参考：

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
    ports:
      - "3306:3306"
    volumes:
      - iims_mysql_data:/var/lib/mysql

volumes:
  iims_mysql_data:
```

启动：

```powershell
docker compose up -d mysql
```

验证：

```powershell
docker ps
docker exec -it iims-mysql mysql -uroot -p
```

## 22. 重置数据库的安全流程

### 22.1 什么时候需要重置

可以重置的场景：

- 本地开发环境导入错了。
- 表结构不完整。
- 测试数据乱了。
- 课程练习需要重新开始。

不要重置的场景：

- 生产环境。
- 有重要数据但未备份。
- 不确定数据库是不是别人正在用。

### 22.2 本地重置流程

进入 MySQL：

```sql
DROP DATABASE IF EXISTS iims;
CREATE DATABASE iims
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

重新导入：

```powershell
mysql -u root -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

验证：

```sql
USE iims;
SHOW TABLES;
SELECT COUNT(*) FROM iims_integral_user;
```

### 22.3 备份命令

重置前可以备份：

```powershell
mysqldump -u root -p iims > C:\Users\MoLin\Desktop\IIMS\iims-backup.sql
```

恢复：

```powershell
mysql -u root -p iims < C:\Users\MoLin\Desktop\IIMS\iims-backup.sql
```

## 23. 本课最小验证链路

完成下面步骤，第三课就算通过。

### 23.1 MySQL 层

```sql
SHOW DATABASES LIKE 'iims';
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
SELECT COUNT(*) FROM iims_integral_user;
SELECT id, username, role FROM iims_integral_user;
```

### 23.2 配置层

确认：

```yaml
iims.datasource.host: 127.0.0.1
iims.datasource.port: 3306
iims.datasource.database: iims
iims.datasource.username: root
iims.datasource.password: root
```

如果你的 MySQL 密码不是 `root`，这里必须改成实际密码。

### 23.3 后端接口层

启动后端后执行：

```powershell
Invoke-WebRequest http://127.0.0.1:8090/iims/user/login/key
```

如果返回 200，继续登录测试。

## 24. 课堂演示脚本

### 24.1 开场

可以这样讲：

> 上一课我们解决了 Java、Maven、Node、npm 环境。今天进入后端项目最容易出错的地方：数据库初始化。后台系统不是只要服务启动就能用，它必须有正确的库、表和初始数据。

### 24.2 演示配置文件

命令：

```powershell
Get-Content C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application.yml
Get-Content C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\resources\application-dev.yml
```

讲解：

> 主配置里 JDBC URL 用占位符，开发环境配置提供具体值。看到 `database: iims`，就知道 MySQL 里必须有一个叫 `iims` 的数据库。

### 24.3 演示创建数据库

```sql
CREATE DATABASE IF NOT EXISTS iims
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

讲解：

> 建库时就指定字符集，避免后面中文菜单和业务数据乱码。

### 24.4 演示导入 SQL

```powershell
mysql -u root -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

讲解：

> SQL 导入成功不一定有明显输出，所以导入后必须查表验证。

### 24.5 演示验证用户表

```sql
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
SELECT id, username, role FROM iims_integral_user;
```

讲解：

> 如果这张表不存在，登录一定失败。这个错误不应该去前端页面里找原因。

### 24.6 演示错误归类

拿这个错误讲：

```text
Table 'iims.iims_integral_user' doesn't exist
```

拆解：

```text
iims：数据库名
iims_integral_user：表名
doesn't exist：表不存在
```

结论：

```text
连接到了 iims 库，但表没导入。
```

### 24.7 课堂收束

讲：

> 今天只要能保证数据库、核心表、初始数据、后端连接配置都正确，后续登录、菜单、AI 模型配置才有基础。下一课我们会把 MySQL、Redis、MinIO 这些中间件用 Docker 统一启动。

## 25. 学生课堂练习

### 25.1 练习 1：配置读取

填写：

| 配置项 | 当前值 |
|---|---|
| 数据库 host |  |
| 数据库 port |  |
| 数据库名 |  |
| 用户名 |  |
| 密码 |  |
| 是否关闭 Druid 解密 |  |

### 25.2 练习 2：建库

执行：

```sql
CREATE DATABASE IF NOT EXISTS iims
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
SHOW DATABASES LIKE 'iims';
```

记录结果。

### 25.3 练习 3：导入 SQL

执行：

```powershell
mysql -u root -p iims < C:\Users\MoLin\Desktop\IIMS\resources\sql\init-data.sql
```

如果失败，记录第一条错误。

### 25.4 练习 4：核心表验证

执行：

```sql
USE iims;
SHOW TABLES LIKE 'iims_integral_user';
SHOW TABLES LIKE 'iims_integral_role';
SHOW TABLES LIKE 'iims_integral_menu';
SHOW TABLES LIKE 'iims_ai_chat_models';
SELECT COUNT(*) FROM iims_integral_user;
SELECT COUNT(*) FROM iims_integral_role;
```

记录结果。

### 25.5 练习 5：错误归类

填写：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `Unknown database 'iims'` |  |  |
| `Table 'iims.iims_integral_user' doesn't exist` |  |  |
| `Access denied for user 'root'` |  |  |
| `Communications link failure` |  |  |
| `Public Key Retrieval is not allowed` |  |  |

参考答案：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `Unknown database 'iims'` | 数据库不存在 | 创建 `iims` 库 |
| `Table 'iims.iims_integral_user' doesn't exist` | 表未导入 | 导入或修复 SQL |
| `Access denied for user 'root'` | 账号密码/权限 | 检查 MySQL 用户和密码 |
| `Communications link failure` | MySQL 连接失败 | 检查 MySQL 是否启动、端口是否正确 |
| `Public Key Retrieval is not allowed` | MySQL 8 认证参数 | 检查 JDBC URL 是否有 `allowPublicKeyRetrieval=true` |

## 26. 本课验收标准

### 26.1 数据库验收

必须能执行并得到正确结果：

```sql
SHOW DATABASES LIKE 'iims';
SHOW TABLES LIKE 'iims_integral_user';
SELECT COUNT(*) FROM iims_integral_user;
SELECT COUNT(*) FROM iims_integral_role;
SELECT COUNT(*) FROM iims_integral_menu;
```

### 26.2 配置验收

能解释：

```text
后端连接哪个数据库？
配置值来自哪个文件？
为什么本地要关闭 Druid 解密？
如果 MySQL 密码不是 root，要改哪里？
```

### 26.3 排错验收

看到：

```text
Table 'iims.iims_integral_user' doesn't exist
```

必须能说：

```text
这是 MySQL 表缺失，先查 iims 库中是否有 iims_integral_user 表，再检查 init-data.sql 是否完整导入。
```

### 26.4 接口验收

后端启动后：

```powershell
Invoke-WebRequest http://127.0.0.1:8090/iims/user/login/key
```

能返回响应。

## 27. 本课作业

### 作业 1：数据库初始化记录

提交：

```text
MySQL 版本：
数据库名：
建库 SQL：
导入命令：
核心表检查结果：
```

### 作业 2：核心表说明

写出下面表的作用：

```text
iims_integral_user
iims_integral_role
iims_integral_menu
iims_ai_chat_models
iims_ai_chat_settings
iims_integral_wiki
iims_integral_warehouse
iims_archive_metadata
```

每张表至少一句话。

### 作业 3：错误复盘

写一段 200 字：

```text
如果登录时报 Table 'iims.iims_integral_user' doesn't exist，我会如何排查？
```

必须包含：

- 检查数据库名。
- 检查表是否存在。
- 检查 SQL 是否导入。
- 检查后端配置是否连对库。

### 作业 4：面试表达

准备 1 分钟口述：

```text
我如何完成 IIMS 项目的数据库初始化？
```

## 28. 面试表达

### 28.1 初级表达

> 我给 IIMS 项目创建了 MySQL 的 `iims` 数据库，并导入了项目提供的 `init-data.sql`。导入后检查了用户表、角色表、菜单表和模型配置表，解决了登录时报 `iims_integral_user` 表不存在的问题。

### 28.2 更好的表达

> IIMS 的登录、菜单、权限、档案和模型配置都依赖 MySQL 初始化数据。我接手项目后先从 `application.yml` 和 `application-dev.yml` 确认后端连接的是 `127.0.0.1:3306/iims`，然后创建 `utf8mb4` 字符集的 `iims` 数据库，导入 `resources/sql/init-data.sql`。导入后我没有只看执行是否报错，而是验证了 `iims_integral_user`、`iims_integral_role`、`iims_integral_menu`、`iims_ai_chat_models` 等核心表和初始数据。之前登录报表不存在，本质就是 SQL 没有完整导入或连错库。

### 28.3 面试官可能追问

#### 问：为什么登录会依赖菜单表？

答：

> 后台系统登录后通常不只是返回 Token，还要加载当前用户的角色、权限和菜单。IIMS 中用户表保存角色 ID，角色表保存菜单 ID，后端再根据这些数据返回前端动态路由，所以菜单表缺失会影响登录后的页面展示。

#### 问：`Unknown database` 和 `Table doesn't exist` 有什么区别？

答：

> `Unknown database` 是数据库本身不存在，说明还没创建 `iims` 库或连接配置写错。`Table doesn't exist` 是已经连到了某个库，但库里缺少目标表，通常是 SQL 没导入、导入不完整或导入到了别的库。

#### 问：为什么本地要关闭 Druid 解密？

答：

> 主配置默认开启了 Druid 的密码解密，适合生产环境使用密文密码。但本地开发通常直接写明文密码，如果不关闭解密，Druid 可能会把明文当密文处理导致连接失败。所以在 `application-dev.yml` 中覆盖为 `config.decrypt=false`。

#### 问：如何判断 SQL 是否导入完整？

答：

> 不能只看导入命令是否结束，要查 `information_schema.tables` 或直接 `SHOW TABLES`，再检查核心表和关键数据，例如用户、角色、菜单、模型配置。对于这个项目，我会重点检查 `iims_integral_user`、`iims_integral_role`、`iims_integral_menu`、`iims_ai_chat_models`。

## 29. 本课最终交付物

本课结束后，学生应提交：

1. 数据库创建和导入记录。
2. 核心表验证截图或 SQL 输出。
3. 数据库配置说明表。
4. 一份常见数据库错误归类表。
5. 一段 1 分钟数据库初始化面试表达。

完成这些，第三课才算真正过关。

