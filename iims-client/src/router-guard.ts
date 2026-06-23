import router from './router/index.js'
import store from './store/index.js'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getStorage } from '@/utils/auth.js' // get token from cookie
import getPageTitle from '@/utils/get-page-title.js'
import { generateWatermark, removeWatermark } from '@/utils/common.js'
import type {RouteRecordRaw} from "vue-router";

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

router.beforeEach(async(to, _from, next) => {
  NProgress.start()
  document.title = getPageTitle(to.meta.title as string)
  console.log(`[RouterGuard] → to: ${to.path} | from: ${_from.path} | matched: ${to.matched.length} routes`)
  if (to.matched.length > 0) {
    to.matched.forEach((r, i) => {
      console.log(`  matched[${i}]: path="${r.path}" name="${String(r.name)}" component=${r.components?.default ? '✓' : '✗'}`)
    })
  } else {
    console.warn(`[RouterGuard] ⚠ No routes matched for path: ${to.path}`)
    console.log(`[RouterGuard] Available routes:`, router.getRoutes().map(r => ({ path: r.path, name: r.name })).filter(r => !r.path.includes(':pathMatch')))
  }
  const hasToken = getStorage('token')
  if (hasToken) {
    if (to.path === '/login' || to.path === '/') {
      next({ path: '/home' })
      NProgress.done()
    } else {
      const hasGetUserInfo = store.getters.name
      if (hasGetUserInfo) {
        generateWatermark(getStorage('name') || '')
        console.log(`[RouterGuard] ✓ User info exists, proceeding to ${to.path}`)
        next()
      } else {
        try {
          console.log(`[RouterGuard] Generating routes for ${to.path}...`)
          const accessRoutes = await store.dispatch('permission/generateRoutes')
          console.log(`[RouterGuard] Generated ${accessRoutes.length} routes`)
          accessRoutes.forEach((route: RouteRecordRaw) => {
            router.addRoute(route)
          })
          console.log(`[RouterGuard] Routes added, retrying navigation to ${to.path}`)
          next({ ...to, replace: true })
        } catch (error) {
          console.error(`[RouterGuard] Error:`, error)
          await store.dispatch('user/resetStorage')
          ElMessage.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
      removeWatermark()
    } else {
      if (to.path === '/') {
        next('/login')
      } else {
        next(`/login?redirect=${to.path}`)
      }
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
