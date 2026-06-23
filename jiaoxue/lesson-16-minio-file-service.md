# 第 16 课：MinIO 文件服务与智能云库

> 课程定位：这一课解决“文件到底存在数据库还是 MinIO、上传接口怎么工作、为什么 bucket 不存在会失败”。IIMS 的头像、档案附件、知识库文件、智能云库都依赖 MinIO。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 理解对象存储和数据库元数据的分工。
2. 读懂 MinIO 配置。
3. 读懂 `/iims/common/upload` 文件上传接口。
4. 理解 MD5 秒传、对象路径、bucket、预签名 URL。
5. 能创建 `iims-bucket`。
6. 能排查上传失败、文件打不开、短链失败。

## 2. 源码定位

```text
iims-module-common/src/main/java/cn/aitenry/iims/common/controller/CommonController.java
iims-module-common/src/main/java/cn/aitenry/iims/common/service/impl/MinioServiceImpl.java
iims-module-common/src/main/java/cn/aitenry/iims/common/config/MinioConfiguration.java
iims-module-common/src/main/java/cn/aitenry/iims/common/properties/MinioProperties.java
iims-module-common/src/main/java/cn/aitenry/iims/common/mapper/FileStorageMapper.java
iims-module-common/src/main/resources/mapper/FileStorageMapper.xml
```

配置：

```yaml
iims:
  minio:
    endpoint: http://127.0.0.1:9000
    accessKey: minioadmin
    secretKey: minioadmin
    bucketName: iims-bucket
```

## 3. 数据库和 MinIO 分工

数据库保存：

```text
文件 ID
原始文件名
重命名文件名
对象路径
文件大小
Content-Type
Bucket
MD5
上传时间
```

MinIO 保存：

```text
真实文件二进制内容
```

所以文件功能需要：

```text
MySQL + MinIO
```

## 4. 上传接口

`CommonController`：

```java
@PostMapping("/upload")
public Result<FileUpload> upload(
    @RequestParam("file") MultipartFile file,
    @RequestParam("itemType") Integer itemType)
```

接口路径：

```text
POST /iims/common/upload
```

请求类型：

```text
multipart/form-data
```

参数：

- `file`
- `itemType`

## 5. MinioServiceImpl 上传流程

流程：

```text
检查文件非空
计算文件 MD5
根据 MD5 查数据库是否已存在
存在则返回预签名 URL，实现秒传
不存在则生成 UUID 文件名
按 用户ID/日期/文件名 生成对象路径
上传到 MinIO bucket
写入 iims_integral_warehouse 文件元数据
返回 fileId 和访问 URL
```

## 6. 秒传逻辑

```java
String fileKey = DigestUtils.md5Hex(file.getInputStream());
FileWarehouse fileInfoByFileKey = fileStorageMapper.getFileInfoByKey(fileKey);
```

含义：

```text
同一个文件内容 MD5 相同，如果数据库已有记录，就不重复上传。
```

优点：

- 节省存储。
- 提升重复上传速度。

注意：

```text
MD5 用于去重，不是安全校验万能方案。
```

## 7. 对象路径

```java
String objectName = String.format("%s/%s/%s", StpUtil.getLoginIdAsLong(), datePath, fileRename);
```

格式：

```text
用户ID/年月日/随机文件名.后缀
```

示例：

```text
17/2026/06/10/abcd1234.png
```

## 8. 预签名 URL

```java
minioClient.getPresignedObjectUrl(...)
```

作用：

```text
生成临时可访问的文件 URL。
```

项目也有短链：

```java
return shortLink + fileId;
```

配置：

```yaml
iims.short-link: http://localhost:8090/iims/common/file/
```

短链接口：

```text
GET /iims/common/file/{fileId}
```

## 9. MinIO 端口

| 端口 | 用途 |
|---:|---|
| 9000 | API，后端 Java 使用 |
| 9001 | 控制台，浏览器管理 |

后端 endpoint 必须是：

```text
http://127.0.0.1:9000
```

不要写 9001。

## 10. 常见错误

### 10.1 bucket 不存在

表现：

```text
上传失败
NoSuchBucket
```

处理：

```text
登录 http://127.0.0.1:9001 创建 iims-bucket。
```

### 10.2 endpoint 写错

表现：

```text
连接 MinIO 失败
```

检查：

```yaml
endpoint: http://127.0.0.1:9000
```

### 10.3 文件有数据库记录但打不开

排查：

- `file_path` 是否为空。
- MinIO 对象是否存在。
- bucket 是否正确。
- 短链地址是否还是 localhost。

### 10.4 上传大文件失败

检查：

- Spring multipart `max-file-size`。
- Nginx `client_max_body_size`。
- MinIO 是否正常。

## 11. 实操任务

1. 登录 MinIO 控制台。
2. 创建 `iims-bucket`。
3. 前端上传一个文件。
4. 查数据库文件记录。
5. 在 MinIO 控制台找到对象。
6. 访问 `/iims/common/file/{fileId}`。

## 12. 验收标准

学生必须能回答：

1. 文件为什么不直接存 MySQL？
2. MinIO 9000 和 9001 的区别？
3. `iims-bucket` 从哪里配置？
4. 文件 MD5 秒传如何实现？
5. 数据库文件元数据和 MinIO 对象如何关联？

## 13. 面试表达

> IIMS 文件服务采用 MySQL 保存元数据、MinIO 保存真实文件的设计。上传接口 `/iims/common/upload` 接收 multipart 文件，服务层先计算 MD5 做重复文件检测，如果已存在就返回预签名 URL；否则生成 UUID 文件名，按用户 ID 和日期组织对象路径，上传到 MinIO 的 `iims-bucket`，再把文件名、路径、大小、类型、bucket、MD5 等元数据写入数据库。文件访问支持 MinIO 预签名 URL 和项目短链 `/iims/common/file/{fileId}`。

