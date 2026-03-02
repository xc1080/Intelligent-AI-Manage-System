<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button v-if="isGoOrganization" @click="getList" style="display: flex;" type="primary" plain>
        <i class="ri-arrow-left-line"></i>
      </el-button>
      <el-button
          type="primary"
          plain
          @click="handleAdd()"
          v-if="!isGoOrganization"
      >
        <i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增公司
      </el-button>
    </div>

    <el-table
        v-loading="loading"
        max-height="calc(100vh - 200px)"
        :data="menuList"
        border
        stripe
        row-key="id"
        :default-expand-all="true"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        empty-text="暂无数据"
        style="width: calc(100% - 40px); margin: auto"
    >
      <el-table-column
          prop="name"
          label="名称"
          :show-overflow-tooltip="true"
          min-width="130"
      />
      <el-table-column
          prop="type"
          label="组织类型"
          min-width="80"
          align="center"
      >
        <template #default="scope">
          <el-tag
              :type="menuTypeTag(scope.row.type)"
              class="mb10"
          >
            {{ formatMenuType(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="code"
          label="全宗"
          :show-overflow-tooltip="true"
          min-width="100"
      />
      <el-table-column
          prop="description"
          label="描述"
          :show-overflow-tooltip="true"
          min-width="200"
      />
      <el-table-column
          fixed="right"
          label="操作"
          class-name="small-padding fixed-width"
          min-width="150"
      >
        <template #default="scope">
          <el-tooltip
              class="item"
              effect="dark"
              content="查看公司架构"
              placement="bottom"
              v-if="!isGoOrganization"
          >
            <el-button
                plain
                type="primary"
                size="small"
                icon="right"
                @click="handleGoOrganization(scope.row.id)"
            />
          </el-tooltip>
          <el-tooltip
              class="item"
              effect="dark"
              content="添加"
              placement="bottom"
              v-if="canAddChild(scope.row)"
          >
            <el-button
                plain
                type="success"
                size="small"
                icon="plus"
                @click="handleAdd(scope.row)"
            />
          </el-tooltip>
          <el-tooltip
              class="item"
              effect="dark"
              content="编辑"
              placement="bottom"
          >
            <el-button
                plain
                type="warning"
                size="small"
                icon="edit"
                @click="handleUpdate(scope.row)"
            />
          </el-tooltip>
          <el-tooltip
              class="item"
              effect="dark"
              content="删除"
              placement="bottom"
              v-if="scope.row.children.length === 0"
          >
            <el-button
                plain
                type="danger"
                size="small"
                icon="delete"
                @click="handleDelete(scope.row)"
            />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改组织对话框 -->
    <el-dialog
        :title="title"
        v-model="open"
        width="670px"
        append-to-body
        :close-on-click-modal="false"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级">
              <el-tree-select
                  v-model="form.parentId"
                  :data="menuOptions"
                  :render-after-expand="false"
                  :check-strictly="true"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input
                  v-model="form.name"
                  placeholder="请输入名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="全宗">
              <el-input
                  v-model="form.code"
                  placeholder="请输入全宗"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入描述"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button plain @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, addOrganization, deleteOrganization, updateOrganization } from '@/api/organization'
import type { FormInstance } from 'element-plus'

interface OrgData {
  id: string | null
  parentId: string
  name: string
  type: number // 0 公司、1 部门、2 职位
  code: string
  description: string
  children?: OrgData[]
  hasChildren?: boolean
}

interface OrgForm extends OrgData {
  [key: string]: any
}

// 数据模型
const loading = ref(true)
const isGoOrganization = ref(false)
const menuList = ref<OrgData[]>([])
const menuOptions = ref<any[]>([])
const open = ref(false)
const title = ref('')
const organizationId = ref<string | null>(null)
const formRef = ref<FormInstance>()
const currentParentType = ref<number | null>(null)

const form = reactive<OrgForm>({
  id: null,
  parentId: '0',
  name: '',
  type: 0,
  code: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '组织名称不能为空', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getMenuTree(null)
    menuList.value = res.data
    isGoOrganization.value = false
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
    organizationId.value = null
  }
}

const canAddChild = (row: OrgData): boolean => {
  if (!isGoOrganization.value) {
    // 在公司页面，只允许添加公司
    return true
  } else {
    // 在具体组织下，根据当前层级决定可以添加什么
    return row.type === 0 || row.type === 1 // 公司下可以添加部门，部门下可以添加职位
  }
}

const normalizer = (node: any) => {
  if (!node.children) {
    delete node.children
  }
  if (node.type === 2) return {}
  return {
    value: node.id,
    label: node.name ?? node.label,
    children: node.children ?? []
  }
}

const normalizeTree = (tree: any): any => {
  const normalizedNode = normalizer(tree)

  if (Object.keys(normalizedNode).length === 0) return {}

  if (Array.isArray(normalizedNode.children)) {
    normalizedNode.children = normalizedNode.children
        .map((child: any) => normalizeTree(child))
        .filter((child: any) => Object.keys(child).length > 0)
  }

  return normalizedNode
}

const getTreeSelect = async () => {
  try {
    menuOptions.value = []
    let menu: any
    if (!isGoOrganization.value) {
      menu = { id: '0', label: '主类目', children: [] }
      menu.children = menuList.value
    } else if (menuList.value[0]?.children?.length === 0) {
      menu = menuList.value[0]
      menuOptions.value.push({
        value: menu.id,
        label: menu.name ?? menu.label,
        children: []
      })
      return
    } else {
      menu = menuList.value[0]
    }
    console.log(menuList.value)
    menuOptions.value.push(normalizeTree(menu))
  } catch (err) {
    console.error(err)
  }
}

const reset = () => {
  Object.assign(form, {
    id: undefined,
    parentId: '0',
    name: '',
    type: 0,
    code: '',
    description: ''
  })
  currentParentType.value = null
}

const cancel = () => {
  open.value = false
  reset()
}

const handleGoOrganization = async (id: string) => {
  loading.value = true
  try {
    const res = await getMenuTree(id)
    menuList.value = res.data
    isGoOrganization.value = true
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
    organizationId.value = id
  }
}

const handleAdd = async (row?: OrgData) => {
  await nextTick()
  reset()
  await getTreeSelect()

  if (row && row.id) {
    // 在现有节点下添加子节点
    form.parentId = row.id
    currentParentType.value = row.type

    // 根据父级类型设置默认的组织类型
    if (isGoOrganization.value && row.type === 0) {
      form.type = 1 // 公司下默认添加部门
      title.value = "新增部门"
    } else if (row.type === 1) {
      form.type = 2 // 部门下默认添加职位
      title.value = "新增职位"
    } else {
      form.type = 0 // 其他情况默认为公司
      title.value = "新增公司"
    }
  } else {
    form.parentId = '0'
    currentParentType.value = null
    title.value = "新增公司"
    form.type = 0
  }
  open.value = true
}

const handleUpdate = async (row: OrgData) => {
  reset()
  await getTreeSelect()
  Object.assign(form, { ...row })

  currentParentType.value = null

  title.value = '修改组织'
  open.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      if (form.id) {
        await updateOrganization(form)
        ElMessage.success({message: '修改成功', duration: 1500})
      } else {
        await addOrganization(form)
        ElMessage.success({message: '新增成功', duration: 1500})
      }
      if (isGoOrganization.value && organizationId.value) {
        await handleGoOrganization(organizationId.value)
      } else {
        await getList()
      }
      open.value = false
    }
  })
}

const handleDelete = (row: OrgData) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.name}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (!row.id) return
    return deleteOrganization(row.id)
  }).then(() => {
    if (isGoOrganization.value && organizationId.value) {
      handleGoOrganization(organizationId.value)
    } else {
      getList()
    }
  })
}

const formatMenuType = (type: number) => {
  const map: Record<number, string> = { 0: '公司', 1: '部门', 2: '职位' }
  return map[type]
}

const menuTypeTag = (type: number) => {
  const map: Record<number, string> = { 0: 'success', 1: 'primary', 2: 'info' }
  return map[type]
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.reset-form-item {
  margin-right: 0;
}

:deep(.el-table) {
  thead tr th:first-child .cell {
    padding-left: 25px;
  }

  tbody tr td:first-child .cell {
    padding-left: 25px;
  }
}

.cus-btn-con {
  margin-bottom: 16px;
}

.mb10 {
  margin-bottom: 4px;
}
</style>