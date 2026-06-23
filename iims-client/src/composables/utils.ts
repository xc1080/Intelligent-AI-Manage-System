import nprogress from "nprogress"
import {ElMessage, ElMessageBox} from 'element-plus'

// 消息提示
export function showMessage(message = '提示内容', type: string = 'success', customClass = '') {
    return ElMessage({
        type: type as any,
        message,
        customClass,
    } as any)
}

// 弹出确认框
export function showModel(content = '提示内容', type: string = 'warning', title = '') {
    return ElMessageBox.confirm(
        content,
        title,
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: type as any,
        }
    )
}

// 显示页面加载 Loading
export function showPageLoading() {
    nprogress.start()
}

// 隐藏页面加载 Loading
export function hidePageLoading() {
    nprogress.done()
}