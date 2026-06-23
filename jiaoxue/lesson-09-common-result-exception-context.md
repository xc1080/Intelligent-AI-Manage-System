# 第 9 课：统一返回、异常与上下文

> 课程定位：这一课解决“后端接口为什么都返回同一种结构、异常为什么能统一变成前端提示、当前用户 ID 怎么在服务层拿到”。这些不是业务功能，但它们决定了后台系统是否可维护。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 读懂 `Result<T>` 的统一返回结构。
2. 理解前端为什么根据 `code`、`errCode` 判断成功失败。
3. 读懂 `GlobalExceptionHandler` 如何统一处理异常。
4. 理解业务异常、SQL 异常、Sa-Token 异常的返回方式。
5. 理解 `BaseContext` 的 ThreadLocal 当前用户上下文。
6. 知道异步和 SSE 场景下 ThreadLocal 的风险。
7. 能设计一个新接口时保持统一返回风格。

## 2. 源码定位

```text
iims-module-common/src/main/java/cn/aitenry/iims/common/result/Result.java
iims-module-common/src/main/java/cn/aitenry/iims/common/context/BaseContext.java
iims-module-common/src/main/java/cn/aitenry/iims/common/exception
iims-module-integral/src/main/java/cn/aitenry/iims/integral/handler/GlobalExceptionHandler.java
iims-client/src/utils/request.ts
```

## 3. 统一返回 Result

`Result<T>` 字段：

```java
private Integer code;
private String msg;
private Boolean success;
private Integer errCode;
private T data;
```

含义：

| 字段 | 作用 |
|---|---|
| `code` | 业务成功失败，项目中 `1` 成功，`0` 失败 |
| `msg` | 错误或提示信息 |
| `success` | 布尔成功标记 |
| `errCode` | 更具体的错误码 |
| `data` | 真正返回的数据 |

成功：

```java
return Result.success(data);
```

失败：

```java
return Result.error("操作失败", 500);
```

## 4. 前端如何理解 Result

前端 `request.ts`：

```ts
const code = response.data.code
const errCode = response.data.errCode

if (code === 0) {
  if (errCode === 10007) {
    // 登录过期
  } else {
    // 普通错误
  }
} else {
  return response.data
}
```

所以后端返回结构必须稳定，否则前端无法统一处理。

特别注意：

```text
IIMS 中 code=1 是成功，code=0 是失败。
```

## 5. 为什么需要统一返回

没有统一返回时，接口可能这样混乱：

```text
有的返回 true
有的返回字符串
有的返回对象
有的直接抛异常
有的 HTTP 200 但业务失败
```

统一后：

```text
前端只需要看 code、msg、errCode、data。
```

这对后台管理系统非常重要。

## 6. 全局异常处理

文件：

```text
GlobalExceptionHandler.java
```

注解：

```java
@RestControllerAdvice
```

作用：

```text
捕获 Controller 层抛出的异常，并转换成统一 JSON 返回。
```

### 6.1 BaseException

```java
@ExceptionHandler
public Result<T> exceptionHandler(BaseException ex) {
    return Result.error(ex.getMessage(), ErrorCodeConstant.UNKNOWN_ERROR);
}
```

业务层可以抛：

```java
throw new LoginFailedException("登录失败");
```

最终前端收到统一结构。

### 6.2 SQLIntegrityConstraintViolationException

处理：

- Duplicate entry。
- cannot be null。

作用：

```text
把数据库唯一约束、非空约束错误变成用户能理解的提示。
```

### 6.3 NotPermissionException

来自 Sa-Token：

```java
@ExceptionHandler
public Result<T> exceptionHandler(NotPermissionException ex)
```

作用：

```text
用户没有权限时返回统一错误。
```

### 6.4 NotLoginException

未登录或 token 无效时触发。

前端根据返回结果跳转登录页。

## 7. BaseContext 当前用户上下文

源码：

```java
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
```

作用：

```text
在当前请求线程中保存当前用户 ID。
```

业务层可以：

```java
Long userId = BaseContext.getCurrentId();
```

用于：

- 创建人。
- 修改人。
- 当前用户默认模型。
- 当前用户权限。

## 8. ThreadLocal 的风险

### 8.1 请求结束要清理

如果不清理：

```text
线程池复用线程时，旧用户 ID 可能污染新请求。
```

所以拦截器通常应在请求结束后：

```java
BaseContext.removeCurrentId();
```

### 8.2 异步线程无法自动传递

AI 对话中有异步：

```java
CompletableFuture.runAsync(...)
```

`ThreadLocal` 默认不会自动传到新线程。

项目中 `ChatServiceImpl` 会先保存：

```java
Long userId = BaseContext.getCurrentId();
```

异步线程里再：

```java
BaseContext.setCurrentId(userId);
```

这是非常值得面试讲的点。

## 9. 新接口应该怎么写

Controller 示例：

```java
@PostMapping("/demo")
public Result<DemoVO> demo(@RequestBody DemoDTO dto) {
    DemoVO vo = demoService.handle(dto);
    return Result.success(vo);
}
```

不要：

```java
return vo;
return true;
return "ok";
```

异常不要到处 try-catch 后乱返回，优先抛业务异常交给全局异常处理。

## 10. 常见错误

### 10.1 前端明明 HTTP 200 但提示失败

原因：

```text
HTTP 200 只是网络成功，业务 code=0 表示业务失败。
```

### 10.2 前端拿不到 data

排查：

- 后端是否 `Result.success(data)`。
- 前端是否解包 `response.data`。
- 字段名是否和前端预期一致。

### 10.3 异步中当前用户为空

原因：

```text
ThreadLocal 没传到异步线程。
```

处理：

```text
进入异步前保存 userId，异步内部重新 set。
```

## 11. 实操任务

1. 找到 `Result.java`，记录所有字段含义。
2. 找到 `request.ts`，说明前端如何判断成功失败。
3. 找到 `GlobalExceptionHandler`，列出它处理的异常。
4. 找到 `BaseContext`，说明 ThreadLocal 用法。
5. 在 AI 对话代码中找异步线程如何处理当前用户。

## 12. 验收标准

学生必须能回答：

1. IIMS 成功返回的 `code` 是多少？
2. `errCode` 有什么用？
3. `@RestControllerAdvice` 做什么？
4. SQL 唯一约束异常如何变成前端提示？
5. `BaseContext` 为什么用 ThreadLocal？
6. ThreadLocal 在异步线程中有什么风险？

## 13. 面试表达

> IIMS 后端使用 `Result<T>` 统一接口返回，字段包括 `code`、`msg`、`success`、`errCode`、`data`，前端 Axios 拦截器统一根据 `code` 和 `errCode` 处理业务成功、失败和登录过期。异常处理通过 `@RestControllerAdvice` 统一捕获业务异常、SQL 异常和 Sa-Token 异常，避免 Controller 到处写重复 try-catch。当前用户 ID 通过 `BaseContext` 的 ThreadLocal 在线程内传递，在 AI SSE 异步场景中需要手动把 userId 带到新线程，避免上下文丢失。

