import Vue from 'vue'
import VueRouter from 'vue-router'

// 进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

import store from '@/store/index'

import util from '@/libs/util.js'

import { handlerVueNavigationUrlKey } from '@/plugin/vue-viewplus/util.adapter';

// 路由数据
import routes from './routes'

Vue.use(VueRouter)

// 导出路由 在 main.js 里使用
const router = new VueRouter({
  routes
})

/**
 * 路由拦截
 * 权限验证
 */
router.beforeEach((to, from, next) => {
  const { path } = to;
  if (path === '/' && !router.app.$vp.isLogin()) {
    next('/login');
  } else {
    // 进度条
    NProgress.start();
    // 关闭搜索面板
    store.commit('d2admin/search/set', false);
    // 权限控制交给`vue-viewplus#rabc`模块
    next();
  }
})

router.afterEach(to => {
  // 进度条
  NProgress.done()
  // 需要的信息
  const app = router.app
  const { name, params, query, fullPath } = to
  // 多页控制 打开新的页面
  app.$store.dispatch('d2admin/page/open', { name, params, query, fullPath: handlerVueNavigationUrlKey(fullPath) })
  // 更改标题
  util.title(to.meta.title)
})

export default router
