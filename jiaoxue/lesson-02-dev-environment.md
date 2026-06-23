# 第 2 课：本地开发环境与依赖修复

> 课程定位：这一课解决“项目在我电脑上为什么跑不起来”。目标不是机械安装软件，而是建立本地开发环境的检查顺序、错误分类能力和最小可运行验证流程。学完后，学生应该能独立判断 Java、Maven、Node、npm、前端依赖、后端依赖、端口占用这些问题分别属于哪一层。

## 1. 本课目标

### 1.1 教学目标

学完本课后，学生应该能做到：

1. 检查本机 Java 版本是否满足项目要求。
2. 检查 Maven 是否安装、是否加入 PATH。
3. 检查 Node 和 npm 是否可用。
4. 解决 Windows PowerShell 禁止运行 `npm.ps1` 的问题。
5. 理解前端依赖安装与 `node_modules` 的作用。
6. 理解后端 Maven 编译、依赖下载和本地仓库的作用。
7. 能启动前端 Vite 开发服务器。
8. 能编译后端 Maven 多模块项目。
9. 能根据错误信息判断问题属于前端、后端、环境还是依赖。
10. 形成一套“本地环境验收清单”。

### 1.2 就业目标

真实开发中，环境问题非常常见。面试官不会因为你会执行 `npm install` 就认为你有工程能力，但如果你能讲清楚：

- 为什么 Java 版本必须匹配。
- Maven 为什么找不到。
- npm 为什么被 PowerShell 拦截。
- `node_modules` 为什么不能手动乱删乱拷。
- `Cannot find package` 属于什么问题。
- Maven 多模块应该在哪个目录执行编译。

这就说明你不是只会“照教程敲命令”，而是能独立接手项目。

## 2. 本课涉及的项目文件

本课重点关注：

```text
C:\Users\MoLin\Desktop\IIMS\iims-client\package.json
C:\Users\MoLin\Desktop\IIMS\iims-client\package-lock.json
C:\Users\MoLin\Desktop\IIMS\iims-client\vite.config.ts
C:\Users\MoLin\Desktop\IIMS\iims-server\pom.xml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\pom.xml
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\java\cn\aitenry\iims\IimsStarterApplication.java
```

本课涉及的工具：

```text
Java 17
Maven
Node.js
npm
PowerShell
IDEA
VS Code 或 WebStorm
```

## 3. 课前概念

### 3.1 JDK 和 JRE

Java 项目开发需要 JDK，不只是 JRE。

简单理解：

| 名称 | 作用 |
|---|---|
| JRE | 只负责运行 Java 程序 |
| JDK | 既能运行 Java，也能编译 Java |

本项目是后端源码项目，要编译、打包、运行，所以需要 JDK。

### 3.2 Maven 是什么

Maven 可以理解为 Java 项目的“依赖管理 + 编译打包工具”。

它负责：

- 下载项目依赖。
- 管理依赖版本。
- 编译 Java 代码。
- 执行测试。
- 打包 Jar。
- 管理多模块项目。

本项目后端是 Maven 多模块，所以后端不能只点开一个 Java 文件运行，要先理解 Maven 工程结构。

### 3.3 Node 和 npm 是什么

前端项目不是直接双击 HTML 文件运行。

本项目使用 Vue 3 + Vite，需要 Node.js 环境。

| 工具 | 作用 |
|---|---|
| Node.js | 运行前端构建工具 |
| npm | 安装前端依赖、执行脚本 |
| Vite | 启动前端开发服务器、打包生产文件 |

### 3.4 IDEA 和 VS Code 分工

推荐：

| 工具 | 用途 |
|---|---|
| IDEA | 后端 Java / Spring Boot 开发 |
| VS Code | 前端 Vue / TypeScript 开发 |
| WebStorm | 如果有，可替代 VS Code 做前端 |

不要误解：

> Vite 不是 IDE。Vite 是前端构建和开发服务器。

前端开发需要编辑器或 IDE，例如 VS Code/WebStorm；Vite 负责把 Vue 项目跑起来。

## 4. 本项目环境版本要求

### 4.1 后端版本

从 `iims-server/pom.xml` 可以看到：

```xml
<java.version>17</java.version>
<maven.compiler.source>${java.version}</maven.compiler.source>
<maven.compiler.target>${java.version}</maven.compiler.target>
```

说明：

```text
后端要求 Java 17。
```

后端框架：

```xml
<artifactId>spring-boot-starter-parent</artifactId>
<version>3.5.0</version>
```

说明：

```text
后端使用 Spring Boot 3.5.0。
```

Spring Boot 3 系列要求 Java 17 起步，所以 Java 8 不行，Java 11 也不合适。

### 4.2 前端版本

从 `iims-client/package.json` 可以看到：

```json
"engines": {
  "node": "^20.19.0 || >=22.12.0"
}
```

说明：

```text
前端推荐 Node 20.19 以上，或者 Node 22.12 以上。
```

当前项目使用：

```json
"vite": "7.1.11"
```

Vite 7 对 Node 版本要求较新，所以不要用 Node 16、Node 18 这类老版本。

## 5. 环境检查总流程

建议每次换电脑、换服务器、重装系统后，都按这个顺序检查：

```text
第 1 步：检查 Java
第 2 步：检查 Maven
第 3 步：检查 Node
第 4 步：检查 npm
第 5 步：检查 PowerShell 执行策略
第 6 步：安装前端依赖
第 7 步：启动前端
第 8 步：编译后端
第 9 步：检查端口和接口
第 10 步：记录问题和解决方案
```

不要一开始就同时启动所有东西，否则报错会混在一起，定位困难。

## 6. 第一部分：检查 Java

### 6.1 命令

打开 PowerShell：

```powershell
java -version
```

### 6.2 正确结果

应该看到类似：

```text
openjdk version "17.0.x"
OpenJDK Runtime Environment
OpenJDK 64-Bit Server VM
```

只要主版本是 17，就满足本项目要求。

### 6.3 错误情况

#### 情况 1：找不到 java

错误类似：

```text
java : 无法将“java”项识别为 cmdlet
```

说明：

```text
Java 没装，或者 Java 没加入 PATH。
```

处理方向：

1. 安装 JDK 17。
2. 设置 `JAVA_HOME`。
3. 把 `%JAVA_HOME%\bin` 加入 PATH。
4. 重新打开 PowerShell。

#### 情况 2：版本是 Java 8 或 Java 11

如果看到：

```text
java version "1.8.0_xxx"
```

或者：

```text
openjdk version "11.x"
```

说明版本不满足。

处理方向：

```text
安装 JDK 17，并确保 PATH 中 JDK 17 排在旧版本前面。
```

### 6.4 教学解释

为什么 Java 17 必须满足？

因为：

1. Spring Boot 3 需要 Java 17。
2. 项目 POM 明确指定编译版本 17。
3. 如果使用旧版本，可能编译失败或启动失败。

### 6.5 验收标准

学生能截图或记录：

```text
java -version 输出 Java 17
```

并能解释：

```text
JDK 版本不对属于后端运行环境问题，不属于代码问题。
```

## 7. 第二部分：检查 Maven

### 7.1 命令

```powershell
mvn -version
```

### 7.2 正确结果

应该看到类似：

```text
Apache Maven 3.9.x
Java version: 17.x
```

这里要看两个点：

1. Maven 能被识别。
2. Maven 使用的 Java 是 17。

### 7.3 常见错误

错误：

```text
mvn : 无法将“mvn”项识别为 cmdlet、函数、脚本文件或可运行程序的名称
```

说明：

```text
Maven 没安装，或者 Maven 的 bin 目录没有加入 PATH。
```

### 7.4 安装 Maven 的推荐方式

推荐手动安装 Apache Maven：

```text
1. 下载 apache-maven-3.9.x-bin.zip
2. 解压到 C:\Tools\apache-maven-3.9.x
3. 配置环境变量 MAVEN_HOME
4. PATH 加入 %MAVEN_HOME%\bin
5. 重新打开 PowerShell
6. 执行 mvn -version
```

示例路径：

```text
C:\Tools\apache-maven-3.9.11
```

PATH 应该包含：

```text
C:\Tools\apache-maven-3.9.11\bin
```

### 7.5 为什么 winget 可能找不到 Maven

你之前遇到：

```text
winget install Apache.Maven
找不到与输入条件匹配的程序包
```

这说明 winget 源里没有匹配到这个包名，或者源索引不可用。

这不是项目问题，也不是 Maven 官方不可用。

处理方式：

```text
换手动下载安装，最稳定。
```

### 7.6 Maven 本地仓库

Maven 下载的依赖通常在：

```text
C:\Users\你的用户名\.m2\repository
```

这个目录叫 Maven 本地仓库。

作用：

```text
第一次编译时从远程仓库下载依赖。
以后再编译时优先使用本地缓存。
```

如果某个依赖下载坏了，可能需要删除对应依赖目录后重新下载。

不要随便删除整个 `.m2`，因为重新下载会很慢。

### 7.7 验收标准

学生能执行：

```powershell
mvn -version
```

并看到：

```text
Maven 版本
Java 17
```

## 8. 第三部分：检查 Node 和 npm

### 8.1 检查 Node

命令：

```powershell
node -v
```

正确结果：

```text
v20.19.x
```

或者：

```text
v22.12.x 以上
```

你的环境里曾经显示：

```text
v24.15.0
```

这满足项目的 engines 要求。

### 8.2 检查 npm

命令：

```powershell
npm -v
```

可能遇到错误：

```text
npm : 无法加载文件 C:\Program Files\nodejs\npm.ps1，因为在此系统上禁止运行脚本。
```

这不是 npm 没安装。

这是 PowerShell 的脚本执行策略阻止了 `npm.ps1`。

### 8.3 解决 PowerShell 执行策略问题

推荐方式：

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

然后输入：

```text
Y
```

重新打开 PowerShell，再执行：

```powershell
npm -v
```

### 8.4 临时绕过方式

如果不想改执行策略，也可以使用：

```powershell
npm.cmd -v
```

安装依赖时：

```powershell
npm.cmd install
```

启动前端时：

```powershell
npm.cmd run dev
```

### 8.5 教学解释

为什么 `node -v` 可以，`npm -v` 不可以？

因为：

```text
node.exe 是可执行文件。
npm 在 Windows PowerShell 中可能会优先匹配 npm.ps1 脚本。
PowerShell 默认策略可能禁止执行 ps1 脚本。
```

所以这个错误属于：

```text
Windows PowerShell 执行策略问题。
```

不是：

```text
前端代码错误。
npm 没安装。
后端错误。
```

### 8.6 验收标准

学生能执行：

```powershell
node -v
npm -v
```

并看到正常版本。

## 9. 第四部分：安装前端依赖

### 9.1 进入前端目录

命令：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
```

一定要确认当前目录是：

```text
C:\Users\MoLin\Desktop\IIMS\iims-client
```

可以执行：

```powershell
Get-Location
```

### 9.2 安装依赖

推荐命令：

```powershell
npm install --legacy-peer-deps
```

如果 npm 被 PowerShell 拦截：

```powershell
npm.cmd install --legacy-peer-deps
```

### 9.3 为什么加 `--legacy-peer-deps`

因为前端项目依赖比较多，且部分依赖之间可能有 peer dependency 版本提示。

`--legacy-peer-deps` 的作用：

```text
让 npm 使用更宽松的 peer dependency 解析方式，减少因依赖声明不完全匹配导致的安装失败。
```

这在接手旧项目或复杂前端项目时很常见。

### 9.4 安装后会出现什么

安装完成后，会生成或更新：

```text
node_modules
package-lock.json
```

解释：

| 文件/目录 | 作用 |
|---|---|
| `node_modules` | 实际下载到本地的依赖包 |
| `package-lock.json` | 锁定依赖版本，保证多人安装结果尽量一致 |
| `package.json` | 项目直接声明的依赖和脚本 |

### 9.5 不要做什么

不要：

```text
手动复制别人电脑上的 node_modules
随便删除 package-lock.json
在根目录 C:\Users\MoLin\Desktop\IIMS 执行前端 npm install
在 iims-server 目录执行 npm install
```

正确位置是：

```text
C:\Users\MoLin\Desktop\IIMS\iims-client
```

### 9.6 常见错误：Cannot find package 'prettier'

你之前遇到：

```text
failed to load config from ...\vite.config.ts
Error [ERR_MODULE_NOT_FOUND]: Cannot find package 'prettier' imported from ...\vite-plugin-sass-dts\dist\index.js
```

翻译成人话：

```text
Vite 读取配置时加载 vite-plugin-sass-dts。
这个插件内部需要 prettier。
但是当前 node_modules 里没有 prettier。
```

处理方式：

```powershell
npm install prettier --save-dev --legacy-peer-deps
```

或者如果你还没完整安装依赖，先执行：

```powershell
npm install --legacy-peer-deps
```

### 9.7 这个错误属于哪一层

它属于：

```text
前端依赖缺失。
```

不属于：

```text
Java 后端问题。
MySQL 问题。
Redis 问题。
模型配置问题。
```

### 9.8 验收标准

前端目录下存在：

```text
node_modules
```

并且执行：

```powershell
npm run dev
```

不再因为缺少 `prettier` 直接失败。

## 10. 第五部分：启动前端 Vite

### 10.1 命令

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm run dev
```

如果 npm 被拦截：

```powershell
npm.cmd run dev
```

### 10.2 正确结果

应该看到类似：

```text
> iims-client@1.0.012 dev
> vite

VITE v7.1.11  ready in xxx ms

Local:   http://localhost:xxxx/
Network: use --host to expose
```

端口可能是：

```text
5173
```

也可能因为被占用变成其他端口。

### 10.3 访问页面

复制终端里的 Local 地址，用浏览器打开。

例如：

```text
http://localhost:5173/
```

或者：

```text
http://127.0.0.1:5173/
```

### 10.4 前端启动成功不等于项目完整成功

前端成功只代表：

```text
Vue 页面能被 Vite 编译并加载。
```

不代表：

```text
后端可用。
数据库可用。
登录可用。
AI 可用。
```

如果页面打开后登录失败，要继续检查后端和数据库。

### 10.5 常见错误分类

#### 错误 1：端口被占用

表现：

```text
Port 5173 is already in use
```

处理：

```text
Vite 通常会自动换端口。
如果没有换，可以关闭占用进程，或者按提示使用新端口。
```

检查端口：

```powershell
netstat -ano | findstr :5173
```

#### 错误 2：页面白屏

处理顺序：

1. 打开浏览器 DevTools。
2. 看 Console。
3. 看 Network。
4. 看是否有资源 404。
5. 看是否有 JS 报错。

#### 错误 3：接口请求失败

前端页面打开后，如果接口失败，要看请求地址。

常见原因：

```text
VITE_APP_API_URL 配错
后端没启动
后端端口不是 8090
后端接口前缀 /iims 不匹配
```

### 10.6 验收标准

学生能做到：

```text
npm run dev 成功
浏览器能打开前端页面
知道前端端口是多少
知道前端失败时去 Console 和 Network 看错误
```

## 11. 第六部分：编译后端 Maven 项目

### 11.1 进入后端根目录

命令：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
```

确认目录：

```powershell
Get-Location
```

必须是：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server
```

### 11.2 为什么要在 iims-server 执行

因为：

```text
iims-server/pom.xml 是父工程。
它包含 modules。
从这里执行 mvn clean install，Maven 会按模块顺序编译整个后端。
```

不要一开始就随便进某个子模块执行。

### 11.3 编译命令

推荐：

```powershell
mvn clean install -DskipTests
```

含义：

| 片段 | 含义 |
|---|---|
| `mvn` | 调用 Maven |
| `clean` | 清理旧编译产物 |
| `install` | 编译并安装到本地 Maven 仓库 |
| `-DskipTests` | 跳过测试执行，加快初次编译 |

### 11.4 为什么用 `install` 而不是只用 `package`

多模块项目里，模块之间互相依赖。

`install` 会把编译后的模块安装到本地 Maven 仓库，方便其他模块引用。

对于本项目这种多模块后端，初次完整编译推荐：

```powershell
mvn clean install -DskipTests
```

### 11.5 正确结果

看到：

```text
BUILD SUCCESS
```

并且最后生成后端 Jar：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\target\iims-starter-1.0.0.jar
```

### 11.6 常见错误

#### 错误 1：Maven 找不到

```text
mvn : 无法将“mvn”项识别
```

属于：

```text
Maven 环境变量问题。
```

#### 错误 2：Java 版本错误

可能表现：

```text
release version 17 not supported
```

说明：

```text
Maven 当前使用的 JDK 不是 17。
```

处理：

```powershell
mvn -version
```

看 Maven 使用的 Java 版本。

#### 错误 3：依赖下载失败

可能表现：

```text
Could not resolve dependencies
```

处理方向：

1. 检查网络。
2. 检查 Maven 镜像。
3. 等待重试。
4. 删除损坏的单个依赖目录后重试。

#### 错误 4：编译错误

可能表现：

```text
Compilation failure
```

这才可能是代码问题。

要看：

```text
哪一个模块失败
哪一个类失败
哪一行失败
```

不要只看最后一行 `BUILD FAILURE`。

### 11.7 验收标准

学生能执行：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn clean install -DskipTests
```

并得到：

```text
BUILD SUCCESS
```

或者能清楚说明失败属于哪一类。

## 12. 第七部分：用 IDEA 打开后端

### 12.1 打开哪个目录

用 IDEA 打开：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server
```

不要只打开：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter
```

原因：

```text
只打开 starter 可能导致其他模块识别不完整。
```

### 12.2 IDEA 要检查什么

打开后检查：

1. Maven 面板是否能看到所有模块。
2. Project SDK 是否是 Java 17。
3. Language level 是否是 17。
4. Maven importer 使用的 JDK 是否是 17。
5. Lombok 插件是否可用。
6. 依赖是否下载完成。

### 12.3 找启动类

启动类：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\src\main\java\cn\aitenry\iims\IimsStarterApplication.java
```

IDEA 中右键：

```text
Run 'IimsStarterApplication'
```

### 12.4 注意：编译成功不等于启动成功

编译成功只说明：

```text
Java 代码语法和依赖基本没问题。
```

启动还需要：

```text
MySQL 可用
Redis 可用
MinIO 配置正确
application-dev.yml 正确
端口 8090 未占用
```

所以本课重点是“编译通过”，不是完整运行所有业务。

## 13. 第八部分：用 VS Code 打开前端

### 13.1 打开哪个目录

用 VS Code 打开：

```text
C:\Users\MoLin\Desktop\IIMS\iims-client
```

### 13.2 推荐插件

建议安装：

```text
Vue - Official
TypeScript Vue Plugin
ESLint
Prettier
```

如果不想复杂，至少装：

```text
Vue - Official
```

### 13.3 VS Code 终端启动

在 VS Code 内打开终端：

```powershell
npm run dev
```

如果不行：

```powershell
npm.cmd run dev
```

### 13.4 前端开发基本流程

```text
打开 VS Code
修改 Vue 页面或 api 文件
Vite 自动热更新
浏览器刷新或自动更新
Network 查看请求
Console 查看前端错误
```

### 13.5 前端 IDE 和 Vite 的关系

一定要讲清楚：

```text
VS Code / WebStorm 是写代码的工具。
Vite 是运行前端项目的工具。
npm 是执行 Vite 命令的工具。
Node 是支撑 npm 和 Vite 运行的环境。
```

## 14. 第九部分：环境变量与 API 地址

### 14.1 前端为什么需要 API 地址

前端页面在浏览器里运行，它需要知道后端在哪里。

例如本地后端：

```text
http://127.0.0.1:8090/iims
```

服务器后端：

```text
http://47.93.158.196:8090/iims
```

### 14.2 VITE_APP_API_URL

项目里前端会读取：

```text
VITE_APP_API_URL
```

例如 AI SSE 请求里使用：

```ts
const VITE_APP_API_URL = import.meta.env.VITE_APP_API_URL
```

开发时可以通过 `.env` 或 PowerShell 环境变量设置。

### 14.3 PowerShell 临时设置

本次终端有效：

```powershell
$env:VITE_APP_API_URL='http://127.0.0.1:8090/iims'
npm run dev
```

如果要指向公网后端：

```powershell
$env:VITE_APP_API_URL='http://47.93.158.196:8090/iims'
npm run dev
```

### 14.4 常见错误

如果前端请求：

```text
http://127.0.0.1:8090/iims
```

但你的后端没启动，就会失败。

如果前端请求：

```text
http://47.93.158.196:8090/iims
```

但服务器安全组没开 8090，也会失败。

如果少了 `/iims`，可能接口 404。

## 15. 第十部分：最小验证链路

本课最后要跑通的是“环境最小闭环”，不是完整业务。

### 15.1 前端最小验证

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm run dev
```

验收：

```text
浏览器能打开 Vite 地址。
```

### 15.2 后端最小验证

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn clean install -DskipTests
```

验收：

```text
BUILD SUCCESS。
```

### 15.3 接口最小验证

如果后端已经启动：

```powershell
Invoke-WebRequest http://127.0.0.1:8090/iims/user/login/key
```

如果返回 200，说明：

```text
后端服务在 8090 工作，登录密钥接口可达。
```

如果失败，先不要慌，根据错误分类：

| 表现 | 可能原因 |
|---|---|
| 连接失败 | 后端没启动或端口不对 |
| 404 | 路径错、接口前缀错 |
| 500 | 后端内部异常，看日志 |
| 超时 | 服务卡住、防火墙、端口阻断 |

## 16. 第十一部分：错误分类训练

### 16.1 错误分类表

| 错误 | 分类 | 第一处理动作 |
|---|---|---|
| `java` 无法识别 | Java 环境 | 检查 JDK 和 PATH |
| `mvn` 无法识别 | Maven 环境 | 安装 Maven，配置 PATH |
| `npm.ps1` 禁止运行 | PowerShell 策略 | 设置执行策略或用 `npm.cmd` |
| `Cannot find package 'prettier'` | 前端依赖 | 安装依赖或补装 prettier |
| `vite config load failed` | 前端配置/依赖 | 看缺哪个包或配置语法 |
| `BUILD FAILURE` | Maven 构建 | 往上找第一个真正错误 |
| `release version 17 not supported` | JDK 版本 | 确认 Maven 使用 Java 17 |
| `Connection refused` | 服务未启动 | 检查对应端口服务 |
| `Table doesn't exist` | 数据库初始化 | 检查 SQL 是否导入 |
| 页面请求 404 | 请求路径 | 检查 baseURL 和接口前缀 |

### 16.2 训练方法

看到错误后先问 5 个问题：

1. 错误发生在前端终端、后端终端、浏览器，还是数据库工具？
2. 错误出现于启动前、启动中、请求时，还是构建时？
3. 错误里有没有文件路径？
4. 错误里有没有端口号？
5. 错误里有没有包名、表名、类名？

然后归类。

## 17. 第十二部分：课堂演示脚本

### 17.1 开场

可以这样讲：

> 上一课我们知道了 IIMS 的整体结构。今天开始解决本地环境。环境问题不是低级问题，它是每个真实项目都会遇到的第一关。我们要做的不是背命令，而是知道每个命令在验证哪一层。

### 17.2 演示 Java

```powershell
java -version
```

讲解：

> 后端是 Spring Boot 3.5，必须使用 Java 17。这里看主版本，不是看后面的小版本。

### 17.3 演示 Maven

```powershell
mvn -version
```

讲解：

> Maven 输出里不只看 Maven 版本，还要看它当前用的 Java 是不是 17。

### 17.4 演示 Node/npm

```powershell
node -v
npm -v
```

讲解：

> 前端 package.json 写了 Node 版本要求，Vite 7 要求较新的 Node。npm 如果被 PowerShell 拦截，不是项目坏了，是脚本执行策略问题。

### 17.5 演示前端依赖

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm install --legacy-peer-deps
```

讲解：

> `node_modules` 是前端依赖实际存放位置。`package-lock.json` 锁版本。安装依赖必须在 `iims-client` 下执行。

### 17.6 演示前端启动

```powershell
npm run dev
```

讲解：

> 看到 Vite Local 地址，说明前端开发服务器起来了。但这不代表后端和数据库可用。

### 17.7 演示后端编译

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn clean install -DskipTests
```

讲解：

> 多模块项目要从父工程编译。第一次下载依赖会慢，看到 BUILD SUCCESS 才说明后端代码和依赖基本 OK。

### 17.8 课堂收束

讲：

> 今天我们完成的是开发环境可用性验证。下一课会进入数据库初始化，因为项目想真正登录，必须先有 MySQL 表结构和初始用户、菜单数据。

## 18. 第十三部分：学生课堂练习

### 18.1 练习 1：环境版本表

填写：

| 工具 | 命令 | 我的输出 | 是否满足 |
|---|---|---|---|
| Java | `java -version` |  |  |
| Maven | `mvn -version` |  |  |
| Node | `node -v` |  |  |
| npm | `npm -v` |  |  |

要求：

```text
必须把实际输出写下来，不要只写“正常”。
```

### 18.2 练习 2：解释错误

解释下面错误：

```text
npm : 无法加载文件 C:\Program Files\nodejs\npm.ps1，因为在此系统上禁止运行脚本。
```

要求回答：

1. 这是什么层的问题？
2. 为什么 `node -v` 可能正常但 `npm -v` 不正常？
3. 两种解决方式是什么？

参考答案：

```text
这是 PowerShell 执行策略问题。node.exe 是可执行文件，npm 在 PowerShell 下可能匹配到 npm.ps1 脚本，而系统策略禁止执行 ps1。可以执行 Set-ExecutionPolicy -Scope CurrentUser RemoteSigned，也可以临时使用 npm.cmd。
```

### 18.3 练习 3：前端启动

要求：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm install --legacy-peer-deps
npm run dev
```

记录：

```text
Vite 启动端口：
浏览器访问地址：
是否有 Console 报错：
```

### 18.4 练习 4：后端编译

要求：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn clean install -DskipTests
```

记录：

```text
是否 BUILD SUCCESS：
如果失败，第一个真正错误是什么：
失败属于环境、依赖、编译还是测试：
```

### 18.5 练习 5：错误归类

填写：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `mvn 无法识别` |  |  |
| `Cannot find package prettier` |  |  |
| `release version 17 not supported` |  |  |
| `Port 5173 is already in use` |  |  |
| `BUILD FAILURE` |  |  |

参考答案：

| 错误 | 分类 | 下一步 |
|---|---|---|
| `mvn 无法识别` | Maven 环境变量 | 配置 Maven PATH |
| `Cannot find package prettier` | 前端依赖 | 安装依赖或补装 prettier |
| `release version 17 not supported` | JDK 版本 | 让 Maven 使用 JDK 17 |
| `Port 5173 is already in use` | 端口占用 | 换端口或关闭占用进程 |
| `BUILD FAILURE` | Maven 构建结果 | 往上找第一个具体错误 |

## 19. 第十四部分：本课验收标准

### 19.1 工具验收

必须能正常执行：

```powershell
java -version
mvn -version
node -v
npm -v
```

并且知道每个命令验证什么。

### 19.2 前端验收

必须能完成：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-client
npm install --legacy-peer-deps
npm run dev
```

验收结果：

```text
Vite 正常启动。
浏览器能打开前端页面。
```

### 19.3 后端验收

必须能完成：

```powershell
cd C:\Users\MoLin\Desktop\IIMS\iims-server
mvn clean install -DskipTests
```

验收结果：

```text
BUILD SUCCESS。
```

### 19.4 认知验收

能解释：

1. Vite 不是 IDE。
2. IDEA 更适合后端，VS Code/WebStorm 更适合前端。
3. `npm.ps1` 报错不是 npm 没装。
4. Maven 多模块要从父工程编译。
5. 前端启动成功不等于后端成功。
6. 后端编译成功不等于登录成功。

## 20. 第十五部分：本课作业

### 作业 1：环境截图和说明

提交 4 个命令的输出：

```powershell
java -version
mvn -version
node -v
npm -v
```

并写一句判断：

```text
我的环境是否满足 IIMS 项目要求，理由是什么？
```

### 作业 2：前端启动记录

记录：

```text
执行目录：
安装依赖命令：
启动命令：
启动端口：
访问地址：
遇到的错误：
解决方式：
```

### 作业 3：后端编译记录

记录：

```text
执行目录：
编译命令：
是否 BUILD SUCCESS：
Jar 生成路径：
遇到的错误：
解决方式：
```

Jar 参考路径：

```text
C:\Users\MoLin\Desktop\IIMS\iims-server\iims-starter\target\iims-starter-1.0.0.jar
```

### 作业 4：写一段环境排错总结

要求 200 字左右：

```text
我在配置 IIMS 本地环境时，如何区分 Java、Maven、Node、npm、前端依赖、后端依赖的问题？
```

## 21. 第十六部分：面试表达

### 21.1 初级表达

> 我在本地搭建了 IIMS 的开发环境。后端使用 Java 17 和 Maven 编译，前端使用 Node 和 npm 启动 Vite。过程中处理了 Maven 未配置 PATH、PowerShell 禁止运行 npm.ps1、前端缺少 prettier 依赖等问题。

### 21.2 更好的表达

> 我接手项目后先做了本地环境标准化。后端是 Spring Boot 3.5，多模块 Maven 工程，所以我确认了 JDK 17、Maven 版本以及 Maven 实际使用的 Java 版本，并从父工程执行 `mvn clean install -DskipTests` 完整编译。前端是 Vue 3 + Vite 7，要求 Node 20.19 或 22.12 以上，我处理了 Windows PowerShell 对 npm.ps1 的执行策略限制，并补齐前端依赖，最终前端 Vite 和后端 Maven 构建都能正常通过。

### 21.3 面试官可能追问

#### 问：为什么 Spring Boot 3 项目不能用 Java 8？

答：

> Spring Boot 3 基于 Jakarta EE 新体系，最低要求 Java 17。本项目 POM 里也明确指定了 `java.version=17`，所以 Java 8 或 Java 11 都不适合作为编译运行环境。

#### 问：你为什么从 `iims-server` 执行 Maven，而不是从 `iims-starter`？

答：

> 因为 `iims-server` 是父工程，里面声明了所有子模块。这个项目模块之间有依赖关系，从父工程执行 `mvn clean install` 可以按正确顺序编译并安装各模块，避免子模块单独编译时找不到内部依赖。

#### 问：`npm.ps1` 报错是什么原因？

答：

> 这是 Windows PowerShell 的脚本执行策略问题。npm 在 PowerShell 中可能会匹配到 `npm.ps1`，而系统默认策略不允许执行脚本。解决方式是给当前用户设置 `RemoteSigned`，或者临时使用 `npm.cmd`。

#### 问：前端启动成功后，为什么登录仍然可能失败？

答：

> 前端启动成功只代表 Vue/Vite 编译运行正常。登录还依赖后端服务、API 地址、MySQL 表结构、Redis 登录状态等。要继续看 Network 请求和后端日志。

## 22. 本课最终交付物

本课结束后，学生应提交：

1. 环境版本检查表。
2. 前端依赖安装和启动记录。
3. 后端 Maven 编译记录。
4. 至少 5 条错误归类笔记。
5. 一段 1 分钟环境搭建面试表达。

完成这些，第二课才算真正过关。

