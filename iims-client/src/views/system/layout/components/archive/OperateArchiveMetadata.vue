<template>
  <div>
    <el-divider content-position="left">基础属性</el-divider>
    <el-descriptions
        class="margin-top"
        :column="3"
        border
    >
      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-user-heart-line"></i>
            责任人
          </span>
        </template>
        <el-select
            v-model="form.archivalResponsible"
            remote
            filterable
            placeholder="请搜索责任人"
            :remote-method="remoteMethod"
            :loading="loadingResponsible"
            style="width: 230px"
            :class="{ 'is-error': errors.archivalResponsible }"
        >
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <div v-if="errors.archivalResponsible" class="error-message">{{ errors.archivalResponsible }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-pages-line"></i>
            档号
          </span>
        </template>
        <el-input
            v-model="form.archivalCode"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalCode }"
        />
        <div v-if="errors.archivalCode" class="error-message">{{ errors.archivalCode }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-draft-line"></i>
            题名
          </span>
        </template>
        <el-input
            v-model="form.archivalTitle"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalTitle }"
        />
        <div v-if="errors.archivalTitle" class="error-message">{{ errors.archivalTitle }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-time-line"></i>
            档案年度
          </span>
        </template>
        <el-date-picker
            v-model="form.archivalYear"
            type="year"
            value-format="YYYY"
            placeholder="选择年度"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalYear }"
        />
        <div v-if="errors.archivalYear" class="error-message">{{ errors.archivalYear }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-time-line"></i>
            档案日期
          </span>
        </template>
        <el-date-picker
            v-model="form.archivalDate"
            type="date"
            placeholder="选择日期"
            :picker-options="pickerOptions"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalDate }"
        />
        <div v-if="errors.archivalDate" class="error-message">{{ errors.archivalDate }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-calendar-schedule-line"></i>
            保管期限
          </span>
        </template>
        <el-select
            v-model="form.archivalDeadline"
            placeholder="选择期限"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalDeadline }"
        >
          <el-option
              v-for="item in deadlines"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <div v-if="errors.archivalDeadline" class="error-message">{{ errors.archivalDeadline }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-information-line"></i>
            档案密级
          </span>
        </template>
        <el-select
            v-model="form.archivalLevel"
            placeholder="选择密级"
            style="width: 230px;"
            :class="{ 'is-error': errors.archivalLevel }"
        >
          <el-option
              v-for="item in levels"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <div v-if="errors.archivalLevel" class="error-message">{{ errors.archivalLevel }}</div>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <span class="required-label">
            <i class="ri-info-card-line"></i>
            档案摘要
          </span>
        </template>
        <el-input
            v-model="form.archivalAbstract"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入档案摘要"
            :class="{ 'is-error': errors.archivalAbstract }"
        />
        <div v-if="errors.archivalAbstract" class="error-message">{{ errors.archivalAbstract }}</div>
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">档案属性</el-divider>
    <component
        :is="componentMap[name]"
        v-if="loading"
        ref="metadata"
        :metadata="metadataProperty"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getArchiveMetadata } from '@/api/archive/collect.js'
import { getBaseAdminPage } from "@/api/user.ts"

// 动态组件注册
import OperateDocArchive from './operate/OperateDocArchive.vue'
import OperateTechArchive from './operate/OperateTechArchive.vue'
import OperatePrsArchive from './operate/OperatePrsArchive.vue'
import OperateAcctArchive from './operate/OperateAcctArchive.vue'
import OperateProjectArchive from './operate/OperateProjectArchive.vue'
import OperateProfArchive from './operate/OperateProfArchive.vue'
import OperateAudioVisArchive from './operate/OperateAudioVisArchive.vue'
import OperateContractArchive from './operate/OperateContractArchive.vue'
import OperateJudicialArchive from './operate/OperateJudicialArchive.vue'
import OperateBuildArchive from './operate/OperateBuildArchive.vue'
import OperateFinancialArchive from './operate/OperateFinancialArchive.vue'
import OperatePhysicalArchive from './operate/OperatePhysicalArchive.vue'
import {ElMessage} from "element-plus";

type ComponentMap = {
  [key: string]: any;
}

const componentMap: ComponentMap = {
  'OperateDocArchive': OperateDocArchive,
  'OperateTechArchive': OperateTechArchive,
  'OperatePrsArchive': OperatePrsArchive,
  'OperateAcctArchive': OperateAcctArchive,
  'OperateProjectArchive': OperateProjectArchive,
  'OperateProfArchive': OperateProfArchive,
  'OperateAudioVisArchive': OperateAudioVisArchive,
  'OperateContractArchive': OperateContractArchive,
  'OperateJudicialArchive': OperateJudicialArchive,
  'OperateBuildArchive': OperateBuildArchive,
  'OperateFinancialArchive': OperateFinancialArchive,
  'OperatePhysicalArchive': OperatePhysicalArchive
}

// Props
const props = defineProps<{
  id: string | null
  name: string
}>()

// Refs
const metadata = ref()

// 数据模型
const loading = ref(false)
const metadataProperty = ref('')
const errors = ref({
  archivalResponsible: '',
  archivalCode: '',
  archivalTitle: '',
  archivalYear: '',
  archivalDate: '',
  archivalDeadline: '',
  archivalLevel: '',
  archivalAbstract: ''
})

const form = reactive({
  id: null as string | null,
  archivalResponsible: {} as { id: string; name: string } | null,
  archivalCode: '',
  archivalTitle: '',
  archivalYear: '',
  archivalDate: '',
  archivalDeadline: '',
  archivalLevel: '',
  archivalAbstract: '',
  metadataProperty: ''
})

const options = ref<{ value: string; label: string }[]>([])
const loadingResponsible = ref(false)
const pickerOptions = {
  disabledDate(time: Date) {
    return time.getTime() > Date.now()
  },
  shortcuts: [{
    text: '今天',
    onClick(picker: any) {
      picker.$emit('pick', new Date())
    }
  }, {
    text: '昨天',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      picker.$emit('pick', date)
    }
  }, {
    text: '一周前',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      picker.$emit('pick', date)
    }
  }]
}

const deadlines = ref([
  { value: '999', label: '永久存储' },
  { value: '10', label: '定期10年' },
  { value: '30', label: '定期30年' }
])

const levels = ref([
  { value: '1', label: '绝密' },
  { value: '2', label: '机密' },
  { value: '3', label: '秘密' }
])

// 验证函数
const validateForm = () => {
  let isValid = true
  // 清空之前的错误信息
  Object.keys(errors.value).forEach(key => {
    errors.value[key as keyof typeof errors.value] = ''
  })

  // 验证责任人
  if (!form.archivalResponsible || Object.keys(form.archivalResponsible).length === 0) {
    errors.value.archivalResponsible = '请选择责任人'
    isValid = false
  }

  // 验证档号
  if (!form.archivalCode || !form.archivalCode.trim()) {
    errors.value.archivalCode = '请输入档号'
    isValid = false
  }

  // 验证题名
  if (!form.archivalTitle || !form.archivalTitle.trim()) {
    errors.value.archivalTitle = '请输入题名'
    isValid = false
  }

  // 验证档案年度
  if (!form.archivalYear) {
    errors.value.archivalYear = '请选择档案年度'
    isValid = false
  }

  // 验证档案日期
  if (!form.archivalDate) {
    errors.value.archivalDate = '请选择档案日期'
    isValid = false
  }

  // 验证保管期限
  if (!form.archivalDeadline) {
    errors.value.archivalDeadline = '请选择保管期限'
    isValid = false
  }

  // 验证档案密级
  if (!form.archivalLevel) {
    errors.value.archivalLevel = '请选择档案密级'
    isValid = false
  }

  // 验证档案摘要
  if (!form.archivalAbstract || !form.archivalAbstract.trim()) {
    errors.value.archivalAbstract = '请输入档案摘要'
    isValid = false
  }

  return isValid
}

// 初始化档案元数据
const initArchiveMetadata = async () => {
  if (props.id) {
    try {
      const res = await getArchiveMetadata(props.id)
      const data = res.data
      metadataProperty.value = data.metadataProperty
      console.log(data)
      if (data.archivalResponsible && Object.keys(data.archivalResponsible).length > 0) {
        const info = JSON.parse(data.archivalResponsible)
        options.value = [{
          value: data.archivalResponsible,
          label: info.name
        }]
      }
      form.archivalResponsible = data.archivalResponsible
      form.archivalCode = data.archivalCode
      form.archivalTitle = data.archivalTitle
      form.archivalYear = data.archivalYear
      form.archivalDate = data.archivalDate
      form.archivalDeadline = data.archivalDeadline
      form.archivalLevel = data.archivalLevel
      form.archivalAbstract = data.archivalAbstract
      loading.value = true
    } catch (error) {
      console.log(error)
    }
  } else {
    loading.value = true
  }
}

// 远程搜索方法
const remoteMethod = (name: string) => {
  if (name) {
    loadingResponsible.value = true
    setTimeout(async () => {
      loadingResponsible.value = false
      const res = await getBaseAdminPage({ name })
      const data = res.data
      const info: { value: string; label: string }[] = []
      data.forEach((item: any) => {
        info.push({
          value: JSON.stringify({ id: item.id, name: item.name }),
          label: item.name
        })
      })
      options.value = info.filter((item) => {
        return item.label.toLowerCase().includes(name.toLowerCase())
      })
    }, 300)
  } else {
    options.value = []
  }
}

// 获取操作表单数据 - 带验证
const getOperateFormData = () => {
  if (!validateForm()) {
    return null
  }

  const data = { ...form }
  if (props.id) data.id = props.id
  data.metadataProperty = JSON.stringify(metadata.value?.form || {})
  return data
}

// 组件挂载后初始化
onMounted(() => {
  initArchiveMetadata()
})

// 暴露给父组件的方法
defineExpose({
  getOperateFormData,
  validateForm
})
</script>

<style scoped>
.required-label::before {
  content: '*';
  color: #f56c6c;
  margin-right: 2px;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 2px;
  display: block;
}

.is-error .el-input__inner,
.is-error .el-select .el-input__inner {
  border-color: #f56c6c;
}
</style>