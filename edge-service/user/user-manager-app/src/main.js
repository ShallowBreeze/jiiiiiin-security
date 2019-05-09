// polyfill
import 'babel-polyfill'
// Vue
import Vue from 'vue'
import App from './App'
// store
import store from '@/store/index'
// 多国语
import i18n from './i18n'
// 核心插件
import d2Admin from '@/plugin/d2admin'
// 菜单和路由设置
import router from './router'
import { frameInRoutes } from '@/router/routes'
import ViewPlus from 'vue-viewplus'
import viewPlusOptions from '@/plugin/vue-viewplus'
import jsUIModule from '@/plugin/vue-viewplus/js-ui-component.js'
import rbacModule from '@/plugin/vue-viewplus/rbac.js'
import ZkTable from 'vue-table-with-tree-grid'
import '@/assets/style/custom.scss'
import _ from 'lodash'
import NProgress from 'nprogress';
import { handlerVueNavigationUrlKey } from '@/plugin/vue-viewplus/util.adapter';

import { mapState } from 'vuex';

Vue.use(ViewPlus, viewPlusOptions)

const {
  debug,
  errorHandler
} = viewPlusOptions

ViewPlus.mixin(Vue, jsUIModule, {
  debug,
  errorHandler,
  moduleName: '自定义jsComponents'
})

ViewPlus.mixin(Vue, rbacModule, {
  debug,
  errorHandler,
  moduleName: '自定义RBAC',
  router,
  publicPaths: ['/login', '/register', '/404'],
  onPathCheckFail(to, from, next) {
    NProgress.done();
    const { path } = to;
    if ((path === '/' || path === '/index') && !router.app.$vp.isLogin()) {
      this.toast('请先登录', { type: 'warning' })
      next('/login');
    } else {
      const title = to.meta.title;
      this.dialog(`您无权访问【${_.isNil(title) ? to.path : title}】页面`)
        .then(() => {
          // 防止用户被踢出之后，被权限拦截导致访问不了任何页面，故这里进行登录状态监测
          if (this.isLogin()) {
            next(false);
          } else {
            // 没有登录的时候跳转到登录界面
            // 携带上登陆成功之后需要跳转的页面完整路径
            next({
              name: 'login',
              query: {
                redirect: handlerVueNavigationUrlKey(to.fullPath)
              }
            });
          }
        });
    }
  }
})

// 核心插件
Vue.use(d2Admin)

Vue.component(ZkTable.name, ZkTable)

new Vue({
  router,
  store,
  i18n,
  render: h => h(App),
  computed: {
    ...mapState('d2admin/page', [
      'opened',
      'current'
    ])
  },
  created() {
    // 处理路由 得到每一级的路由设置
    this.$store.commit('d2admin/page/init', frameInRoutes);
    const menus = this.$vp.cacheLoadFromSessionStore('menus');
    if (!_.isNil(menus)) {
      // 设置侧边栏菜单
      this.$store.commit('d2admin/menu/asideSet', menus);
      // 初始化菜单搜索功能
      this.$store.commit('d2admin/search/init', menus);
    }
  },
  mounted() {
    // 展示系统信息
    this.$store.commit('d2admin/releases/versionShow')
    // 用户登录后从数据库加载一系列的设置
    this.$store.dispatch('d2admin/account/load')
    // 获取并记录用户 UA
    this.$store.commit('d2admin/ua/get')
    // 初始化全屏监听
    this.$store.dispatch('d2admin/fullscreen/listen')
  }
}).$mount('#app')
