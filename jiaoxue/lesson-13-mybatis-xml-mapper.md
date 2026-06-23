# 第 13 课：MyBatis 与 XML Mapper

> 课程定位：这一课解决“Controller 调用 Service 后，SQL 到底在哪里写、动态查询怎么实现、为什么 Mapper 方法找不到会报错”。IIMS 大量使用 MyBatis XML，读懂 XML 是读懂后端业务的关键。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 找到 Mapper 接口和 XML 文件。
2. 理解 namespace 和 Mapper 接口的对应关系。
3. 读懂 insert、update、select、where、set、if、foreach。
4. 理解 PageHelper 分页和 XML 查询的配合。
5. 理解 JSON_CONTAINS 在角色菜单查询中的作用。
6. 能排查 `Invalid bound statement`。
7. 能新增一个简单查询接口。

## 2. 源码定位

典型文件：

```text
iims-module-integral/src/main/java/cn/aitenry/iims/integral/mapper/UserMapper.java
iims-module-integral/src/main/resources/mapper/UserMapper.xml
iims-module-integral/src/main/resources/mapper/MenuMapper.xml
iims-module-ai/src/main/resources/mapper/AiChatModelsMapper.xml
iims-module-archive/src/main/resources/mapper
```

配置：

```yaml
mybatis:
  mapper-locations: classpath*:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

## 3. Mapper XML 基本结构

```xml
<mapper namespace="cn.aitenry.iims.integral.mapper.UserMapper">
    <select id="pageQuery">
        select * from iims_integral_user
    </select>
</mapper>
```

关键对应：

```text
namespace = Mapper 接口全限定名
id = Mapper 接口方法名
```

如果接口方法叫 `pageQuery`，XML id 也必须叫 `pageQuery`。

## 4. 动态 SQL

用户分页：

```xml
<select id="pageQuery">
    select * from iims_integral_user
    <where>
        and is_deleted = 0
        <if test="name != null and name != ''">
            and name like concat('%', #{name}, '%')
        </if>
        <if test="username != null and username != ''">
            and username like concat('%', #{username}, '%')
        </if>
    </where>
</select>
```

解释：

- `<where>` 会自动处理开头的 `and`。
- `<if>` 根据参数是否为空拼接条件。
- `#{name}` 是预编译参数，防 SQL 注入。

## 5. 动态更新

```xml
<update id="update">
    update iims_integral_user
    <set>
        <if test="name != null">name = #{name},</if>
        <if test="username != null">username = #{username},</if>
        <if test="password != null">password = #{password},</if>
    </set>
    where id = #{id}
</update>
```

`<set>` 会自动去掉最后多余逗号。

适合：

```text
只更新非空字段。
```

## 6. foreach

```xml
<foreach collection="ids" item="id" separator="," open="(" close=")">
    #{id}
</foreach>
```

用于：

```sql
WHERE id IN (...)
```

典型场景：

- 批量删除。
- 批量查询。
- 角色菜单 ID 查询。

## 7. JSON_CONTAINS

用户权限查询：

```sql
SELECT m.perms
FROM iims_integral_user a
JOIN iims_integral_role r
  ON JSON_CONTAINS(a.role, CONCAT('"', CAST(r.id AS CHAR), '"'))
JOIN iims_integral_menu m
  ON JSON_CONTAINS(r.menus, CONCAT('"', CAST(m.id AS CHAR), '"'))
WHERE a.id = #{userId}
```

含义：

```text
用户 role 字段是 JSON 数组字符串。
角色 menus 字段也是 JSON 数组字符串。
用 JSON_CONTAINS 判断数组中是否包含某个 ID。
```

## 8. PageHelper

Service 中：

```java
PageHelper.startPage(page, pageSize);
Page<User> pageResult = userMapper.pageQuery(dto);
```

PageHelper 会拦截后面的 SQL，自动加分页。

顺序很重要：

```text
先 startPage
再执行查询
```

## 9. 常见错误

### 9.1 Invalid bound statement

原因：

- namespace 不等于接口全名。
- XML id 不等于方法名。
- XML 没放在 `resources/mapper`。
- `mapper-locations` 配错。
- 没从父工程完整编译。

### 9.2 字段映射为空

排查：

- 数据库字段是否是下划线。
- Java 字段是否是驼峰。
- `map-underscore-to-camel-case` 是否开启。

### 9.3 SQL 语法错误

排查：

- 打印最终 SQL。
- 检查 `<if>` 拼接。
- 检查逗号和 and。

## 10. 实操任务

1. 找到 `UserMapper.java` 和 `UserMapper.xml`。
2. 追踪 `/iims/user/page` 从 Controller 到 SQL。
3. 修改查询条件，增加手机号模糊查询。
4. 执行分页接口验证。
5. 故意改错 XML id，观察错误，再恢复。

## 11. 验收标准

学生必须能回答：

1. namespace 对应什么？
2. XML id 对应什么？
3. `<where>` 和 `<set>` 的作用是什么？
4. `#{}` 和 `${}` 有什么区别？
5. PageHelper 为什么要写在查询前？
6. JSON_CONTAINS 在本项目哪里用？

## 12. 面试表达

> IIMS 后端大量使用 MyBatis XML 编写 SQL。Mapper XML 的 namespace 对应 Mapper 接口全限定名，SQL id 对应接口方法名。项目中用 `<where>`、`<if>` 实现动态查询，用 `<set>` 实现非空字段更新，用 `<foreach>` 实现 IN 查询。权限查询里因为用户角色和角色菜单存的是 JSON 数组字符串，所以使用 MySQL 的 `JSON_CONTAINS` 联查用户、角色和菜单。分页通过 PageHelper，在 Service 中先调用 `startPage` 再执行 Mapper 查询。

