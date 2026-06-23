# 第 17 课：档案管理业务模块

> 课程定位：这一课解决“档案模块为什么比普通 CRUD 复杂、档案树怎么来、档案类型和动态属性怎么组织”。IIMS 的档案管理是业务复杂度较高的模块，适合训练真实后台业务理解。

## 1. 本课目标

学完本课后，学生应该能做到：

1. 找到档案模块 Controller、Service、Mapper、DTO、VO。
2. 理解档案树菜单如何生成。
3. 理解组织、档案类型、档案元数据的关系。
4. 理解档案分页、新增、编辑、删除链路。
5. 能排查档案树为空、档案列表为空、动态属性不显示。

## 2. 源码定位

```text
iims-module-archive/src/main/java/cn/aitenry/iims/archive/controller/CollectController.java
iims-module-archive/src/main/java/cn/aitenry/iims/archive/service/impl/ArchiveBaseModuleServiceImpl.java
iims-module-archive/src/main/java/cn/aitenry/iims/archive/mapper
iims-module-archive/src/main/java/cn/aitenry/iims/archive/model/dto
iims-module-archive/src/main/java/cn/aitenry/iims/archive/model/entity
iims-module-archive/src/main/java/cn/aitenry/iims/archive/model/vo
iims-client/src/views/system/archives
iims-client/src/api/archive
```

数据库：

```text
iims_archive_metadata
iims_archive_type
iims_archive_mapper
```

## 3. 档案接口

`CollectController`：

```text
GET  /iims/archive/collect/menuTree
POST /iims/archive/collect/page
GET  /iims/archive/collect/metadata/{id}
POST /iims/archive/collect/edit/metadata
POST /iims/archive/collect/add/metadata
POST /iims/archive/collect/del/metadata
```

这些接口覆盖：

- 档案树。
- 档案分页。
- 详情。
- 编辑。
- 新增。
- 删除。

## 4. 档案树菜单

`getTreeMenus()` 流程：

```text
获取当前登录用户 ID
查询用户信息
根据用户岗位/组织找到部门
根据部门查 archive mapper
解析组织 IDs 和档案类型 IDs
查询组织列表
查询档案类型列表
组合成树形菜单
递归挂载 children
```

这里涉及：

- 用户。
- 组织。
- 档案映射。
- 档案类型。

不是简单查一张菜单表。

## 5. 档案类型

```text
iims_archive_type
```

包含：

- 档案类型名称。
- 操作组件。
- 详情组件。
- 类型编码。

前端会根据档案类型加载不同表单或详情组件。

## 6. 档案元数据

```text
iims_archive_metadata
```

保存档案基础信息和动态属性。

常见字段：

- 档案编码。
- 档案标题。
- 年度。
- 保管期限。
- 责任人。
- 动态属性 JSON。
- 关联文件 IDs。
- 删除状态。

## 7. 档案分页

Service：

```java
PageHelper.startPage(page, pageSize);
Page<ArchiveBaseMetadata> archiveBaseMetadata = archiveMetadataMapper.pageQuery(dto);
```

然后转换 VO：

```java
ArchiveBaseMetadataVO
```

包括：

- 档案级别枚举转换。
- 保管期限枚举转换。
- 责任人 JSON 转对象。

## 8. 新增档案

```java
ArchiveMetadata metadata = new ArchiveMetadata();
BeanUtils.copyProperties(addArchiveMetadataDto, metadata);
metadata.setIsDeleted(false);
archiveMetadataMapper.insert(metadata);
```

重点：

```text
DTO -> Entity -> Mapper insert。
```

## 9. 编辑档案

```java
BeanUtils.copyProperties(editArchiveMetadataDto, metadata);
archiveMetadataMapper.update(metadata);
```

动态字段通常也会随 DTO 带入。

## 10. 删除档案

```java
DeletedStatus deletedStatus = DeletedStatus.builder()
    .isDeleted(true)
    .ids(ids)
    .build();
archiveMetadataMapper.updateArchiveDeleted(deletedStatus);
```

这是逻辑删除。

## 11. 常见错误

### 11.1 档案树为空

排查：

- 当前用户是否有 organization。
- organization 是否能找到部门。
- `iims_archive_mapper` 是否有部门映射。
- 档案类型是否存在。

### 11.2 档案列表为空

排查：

- 查询条件是否正确。
- 档案类型是否匹配。
- 是否逻辑删除。
- PageHelper 参数是否正确。

### 11.3 前端组件找不到

排查：

- `iims_archive_type` 中组件名。
- 前端 `system/archives` 下是否存在对应组件。

## 12. 实操任务

1. 调用 `/menuTree`。
2. 观察返回树结构。
3. 查询 `iims_archive_type`。
4. 新增档案。
5. 编辑档案。
6. 逻辑删除档案。
7. 查看数据库变化。

## 13. 验收标准

学生必须能回答：

1. 档案树由哪些数据拼出来？
2. 档案类型表有什么作用？
3. 档案元数据和动态属性怎么保存？
4. 删除档案是真删还是逻辑删除？
5. 档案模块为什么比普通 CRUD 复杂？

## 14. 面试表达

> IIMS 档案模块不是简单 CRUD。档案树会根据当前用户所属组织、部门映射和档案类型动态生成；档案类型表定义不同档案的操作组件和详情组件；档案元数据表保存基础字段、动态属性和关联文件。分页查询使用 PageHelper，新增和编辑通过 DTO 转 Entity 写入，删除采用 `is_deleted` 逻辑删除。排查档案树为空时，我会从用户组织、部门映射、档案类型和前端组件路径逐层检查。

