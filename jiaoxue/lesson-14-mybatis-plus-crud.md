# 第 14 课：MyBatis Plus 与基础 CRUD

> 课程定位：这一课解决“项目里既有 MyBatis XML，又引入 MyBatis Plus，到底怎么理解”。IIMS 主要业务 SQL 仍偏 XML，但配置中引入了 MyBatis Plus，部分实体、枚举、通用字段和 CRUD 思路都可以结合 MyBatis Plus 理解。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 理解 MyBatis 和 MyBatis Plus 的关系。
2. 读懂项目中的 MyBatis Plus 配置。
3. 理解枚举 ordinal 存储的风险。
4. 理解 BaseTable、逻辑删除、创建/更新时间字段。
5. 能设计一个标准 CRUD 接口结构。
6. 能判断什么时候用 XML，什么时候用通用 CRUD。

## 2. 源码定位

配置：

```text
iims-starter/src/main/resources/application.yml
```

相关配置：

```yaml
mybatis-plus:
  configuration:
    default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler
  global-config:
    banner: off
```

实体：

```text
iims-module-common/src/main/java/cn/aitenry/iims/common/model/entity/base/BaseTable.java
iims-module-common/src/main/java/cn/aitenry/iims/common/model/entity/status
iims-module-common/src/main/java/cn/aitenry/iims/common/enums
```

## 3. MyBatis Plus 是什么

可以理解为：

```text
MyBatis Plus 是 MyBatis 的增强工具。
```

它通常提供：

- BaseMapper。
- 通用 CRUD。
- 条件构造器 Wrapper。
- 分页插件。
- 逻辑删除。
- 自动填充。
- 枚举处理。

但本项目仍保留大量 XML，因为业务 SQL 比较复杂。

## 4. 为什么项目仍用 XML

原因：

1. RBAC 查询涉及 JSON_CONTAINS。
2. 档案查询涉及动态条件和复杂 VO。
3. AI 模型、知识库、文章查询有定制字段。
4. XML 更直观地控制 SQL。

所以不能看到 MyBatis Plus 依赖就以为所有 SQL 都是自动生成。

## 5. 枚举处理

配置：

```yaml
default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler
```

含义：

```text
枚举按 ordinal 存数据库。
```

例如：

```java
public enum AiApiType {
    AGENT,
    OPENAI,
    OLLAMA,
    DEEPSEEK;
}
```

对应：

```text
AGENT = 0
OPENAI = 1
OLLAMA = 2
DEEPSEEK = 3
```

风险：

```text
如果后续调整枚举顺序，数据库含义会变。
```

面试要能说：

> ordinal 存储节省空间，但可维护性不如显式 code。

## 6. BaseTable 通用字段

项目实体继承基础表字段。

常见字段：

```text
createBy
createTime
updateBy
updateTime
```

作用：

- 记录创建人。
- 记录创建时间。
- 记录修改人。
- 记录修改时间。

这些字段结合自动填充切面或业务代码写入。

## 7. 逻辑删除

项目大量使用：

```text
is_deleted
```

逻辑删除不是物理删除。

物理删除：

```sql
DELETE FROM table WHERE id = ?
```

逻辑删除：

```sql
UPDATE table SET is_deleted = 1 WHERE id = ?
```

好处：

- 可恢复。
- 保留历史。
- 避免误删。

坏处：

- 所有查询都要加 `is_deleted = 0`。
- 数据量会越来越大。

## 8. 标准 CRUD 链路

```text
前端页面
-> api/*.ts
-> Controller
-> Service
-> Mapper
-> Mapper XML / BaseMapper
-> MySQL
```

新增：

```text
DTO -> Entity -> insert -> Result.success
```

修改：

```text
DTO -> Entity -> update -> Result.success
```

删除：

```text
ids -> is_deleted=true -> Result.success
```

查询：

```text
PageQueryDTO -> PageHelper -> Mapper XML -> PageResult
```

## 9. 新增业务表设计模板

实体：

```java
public class Demo extends BaseTable {
    private Long id;
    private String name;
    private Boolean isDeleted;
}
```

Controller：

```java
@PostMapping
public Result<String> add(@RequestBody DemoDTO dto)
```

Service：

```java
void add(DemoDTO dto);
PageResult page(DemoPageQueryDTO dto);
```

Mapper：

```java
int insert(Demo demo);
Page<Demo> pageQuery(DemoPageQueryDTO dto);
```

XML：

```xml
<insert id="insert">...</insert>
<select id="pageQuery">...</select>
```

## 10. 常见错误

### 10.1 枚举值错乱

原因：

```text
ordinal 存储，枚举顺序被改。
```

处理：

```text
不要随便调整已有枚举顺序。
```

### 10.2 逻辑删除数据仍显示

原因：

```text
查询忘记加 is_deleted = 0。
```

### 10.3 创建人为空

原因：

```text
BaseContext 当前用户为空，或自动填充没生效。
```

## 11. 实操任务

1. 找一个带 `is_deleted` 的表。
2. 找对应 XML 查询是否过滤删除数据。
3. 找一个枚举字段，看数据库存的是数字还是字符串。
4. 设计一个简单 Demo 表 CRUD 草图。

## 12. 验收标准

学生必须能回答：

1. MyBatis Plus 和 MyBatis 是什么关系？
2. 本项目为什么仍使用大量 XML？
3. ordinal 枚举存储有什么风险？
4. 逻辑删除有什么优缺点？
5. 一个标准 CRUD 要有哪些层？

## 13. 面试表达

> IIMS 引入了 MyBatis Plus，但复杂业务 SQL 仍主要通过 MyBatis XML 控制，例如 RBAC 的 JSON_CONTAINS 查询、档案动态查询和模型配置查询。MyBatis Plus 配置里枚举采用 ordinal 方式存储，这要求不能随意调整枚举顺序。项目通用实体继承基础字段，并通过 `is_deleted` 做逻辑删除。新增业务时我会按 DTO、Controller、Service、Mapper、XML、PageResult 的标准链路实现。

