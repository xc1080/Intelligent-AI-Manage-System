import request from '@/utils/request.ts'

/**
 * 查询菜单树形结构
 * @returns
 */
export function getMenuTree(companyId: string | null) {
    return request({
        url: `/organization/menuTree?companyId=${companyId ? companyId : ''}`,
        method: 'get'
    }) as any
}

/**
 * 查询菜单树形结构
 * @returns
 */
export function getAllMenuTree() {
    return request({
        url: `/organization/allMenuTree`,
        method: 'get'
    }) as any
}

/**
 * 更新菜单
 * @param {*} data
 * @returns
 */
export function updateOrganization(data: any) {
    return request({
        url: '/organization',
        method: 'put',
        data
    }) as any
}

/**
 * 新增菜单
 * @param {*} data
 * @returns
 */
export function addOrganization(data: any) {
    return request({
        url: '/organization',
        method: 'post',
        data
    }) as any
}

/**
 * 删除菜单
 * @param {*} id
 * @returns
 */
export function deleteOrganization(id: string) {
    return request({
        url: `/organization/${id}`,
        method: 'delete'
    }) as any
}