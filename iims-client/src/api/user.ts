import request from '@/utils/request.ts'

/**
 * 管理员分页查询
 * @param {*} data
 * @returns
 */
export function getAdminList(data: any) {
  return request({
    url: '/user/page',
    method: 'post',
    data
  }) as any
}

/**
 * 启用禁用管理员账号
 * @param {*} data
 * @returns
 */
export function startOrStop(data: any) {
  return request({
    url: `/user/status/${data.isDisable}?id=${data.id}`,
    method: 'post'
  }) as any
}

/**
 * 管理员分页查询
 * @param {*} data
 * @returns
 */
export function updateAdmin(data: any) {
  return request({
    url: '/user',
    method: 'put',
    data
  }) as any
}

/**
 * 修改密码
 * @param {*} data
 * @returns
 */
export function updatePassword(data: any) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  }) as any
}

export function addAdmin(data: any) {
  return request({
    url: '/user',
    method: 'post',
    data
  }) as any
}

// 部门导出
export function excelUser() {
  return request({
    url: '/user/export',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 删除管理员
 * @param {*} id
 * @returns
 */
export function deleteAdmin(id: string) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  }) as any
}

export function getAdmin(id: string) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  }) as any
}

export function getBaseAdminPage(data: any) {
  return request({
    url: `/user/base/page`,
    method: 'post',
    data
  }) as any
}


export function getBaseAdminInfo(id: string) {
  return request({
    url: `/user/base/info/${id}`,
    method: 'get'
  }) as any
}

export function resetPassword(id: string) {
  return request({
    url: `/user/reset/${id}`,
    method: 'put'
  }) as any
}