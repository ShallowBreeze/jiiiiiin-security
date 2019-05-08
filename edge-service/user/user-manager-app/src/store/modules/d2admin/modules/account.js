import util from '@/libs/util.js';
import { AccountLogin } from '@api/sys.login'
import _ from 'lodash';

export default {
  namespaced: true,
  actions: {
    /**
     * @description 登录
     * @param {Object} param context
     * @param {Object} param vm {Object} vue 实例
     * @param {Object} param username {String} 用户账号
     * @param {Object} param password {String} 密码
     * @param {Object} param route {Object} 登录成功后定向的路由对象
     */
    login({ commit, dispatch }, {
      vm,
      username,
      password,
      imageCode,
      rememberMe = false,
      route = {
        name: 'index'
      }
    }) {
      return new Promise((resolve, reject) => {
        // 开始请求登录接口
        AccountLogin(vm.$vp, {
          username,
          password,
          imageCode,
          'remember-me': rememberMe
        })
          .then(async res => {
            // 设置 cookie 一定要存 uuid
            // 整个系统依赖这两个数据进行校验和存储
            // uuid 是用户身份唯一标识 用户注册的时候确定 并且不可改变 不可重复
            util.cookies.set('uuid', `${res.principal.admin.username}-uuid`);
            // 设置 vuex 用户信息
            await dispatch('d2admin/user/set', {
              name: res.name
            }, { root: true });
            // 用户登录后从持久化数据加载一系列的设置
            await dispatch('load');
            commit('d2admin/gray/set', false, { root: true });
            resolve(res)
          })
          .catch(err => {
            console.log('err: ', err);
            reject(err);
          });
      });
    },
    /**
     * @description 注销用户并返回登录页面
     * @param {Object} param context
     * @param {Object} param vm {Object} vue 实例
     * @param {Object} param confirm {Boolean} 是否需要确认
     */
    logout({ commit, dispatch }, { vm, confirm = false }) {
      /**
       * @description 注销
       */
      function logout() {
        // 删除cookie
        util.cookies.remove('uuid');
        // 跳转路由
        this.psPageReplace('/login');
        // 修改用户登录状态
        this.modifyLoginState(false)
        dispatch('d2admin/page/closeAll', null, { root: true });
      }

      // 判断是否需要确认
      if (confirm) {
        commit('d2admin/gray/set', true, { root: true });
        vm.$confirm('注销当前账户吗?  打开的标签页和用户设置将会被保存。', '确认操作', {
          confirmButtonText: '确定注销',
          cancelButtonText: '放弃',
          type: 'warning'
        })
          .then(() => {
            commit('d2admin/gray/set', false, { root: true })
            vm.$vp.ajaxGet('/signOut')
              .finally(() => {
                vm.$vp::logout();
              });
          })
          .catch(() => {
            commit('d2admin/gray/set', false, { root: true });
            vm.$message('放弃注销用户');
          });
      } else {
        if (_.has(vm, '$vp')) {
          vm.$vp::logout();
        } else {
          vm::logout();
        }
      }
    },
    /**
     * @description 用户登录后从持久化数据加载一系列的设置
     * @param {Object} state vuex state
     */
    load({ commit, dispatch }) {
      return new Promise(async resolve => {
        // DB -> store 加载用户名
        await dispatch('d2admin/user/load', null, { root: true });
        // DB -> store 加载主题
        await dispatch('d2admin/theme/load', null, { root: true });
        // DB -> store 加载页面过渡效果设置
        await dispatch('d2admin/transition/load', null, { root: true });
        // DB -> store 持久化数据加载上次退出时的多页列表
        // 因为内管项目权限可能随时在变，所以上一次可以打开的页面，下一次不一定有权限能打开，故不需要这个功能
        // await dispatch('d2admin/page/openedLoad', null, { root: true });
        // DB -> store 持久化数据加载侧边栏折叠状态
        await dispatch('d2admin/menu/asideCollapseLoad', null, { root: true });
        // DB -> store 持久化数据加载全局尺寸
        await dispatch('d2admin/size/load', null, { root: true });
        // end
        resolve();
      });
    }
  }
};
