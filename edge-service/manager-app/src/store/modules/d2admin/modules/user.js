// 设置文件
import setting from '@/setting.js'
// import { uuidv4 } from 'node-uuid';
const uuidv4 = require('uuid/v4')

export default {
  namespaced: true,
  state: {
    // 用户信息
    info: setting.mvc.info,
    // 用户设备信息
    // https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
    // https://www.zhihu.com/question/51410927
    // TODO 这里之后建议改为后台统一进行唯一id分配，并存储于一个id分配服务
    deviceId: '',
    // 用户访问令牌相关信息
    oauth2AccessToken: {}
  },
  getters: {
    access_token: state => {
      return state.oauth2AccessToken.access_token
    }
  },
  mutations: {
    /**
     * 修改用户`deviceId`为用户`username`，-MNG标识内管用户
     * @param {Object} state
     * @param {String} deviceId 设备标识
     */
    setDeviceId(state, deviceId) {
      state.deviceId = deviceId
    },
    /**
     * 设置令牌信息对象
     * @param {Object} state
     * @param {Object} tokenInfo 服务端返回的access_token信息对象，包含访问令牌、刷新令牌等
     */
    setOauth2AccessToken(state, tokenInfo) {
      state.oauth2AccessToken = tokenInfo
    }
  },
  actions: {
    async clearTokenInfo({ commit }, tokenInfo) {
      commit('setOauth2AccessToken', {})
      await window.$vp.cacheDeleteToSessionStore('TOKEN_INFO')
      console.log('user clearTokenInfo', window.$vp.cacheLoadFromSessionStore('TOKEN_INFO', {}))
    },
    async updateOauth2AccessToken({ commit }, tokenInfo) {
      commit('setOauth2AccessToken', tokenInfo)
      await window.$vp.cacheSaveToSessionStore('TOKEN_INFO', tokenInfo)
    },
    async updateDeviceId({ commit }, deviceId) {
      commit('setDeviceId', deviceId)
      await window.$vp.cacheSaveToSessionStore('DEVICE_ID', deviceId)
    },
    /**
     * @description 设置用户数据
     * @param {Object} state vuex state
     * @param {*} info info
     */
    set ({ state, dispatch }, info) {
      return new Promise(async resolve => {
        // store 赋值
        state.info = info
        // 持久化
        await dispatch('d2admin/db/set', {
          dbName: 'sys',
          path: 'mvc.info',
          value: info,
          user: true
        }, { root: true })
        // end
        resolve()
      })
    },
    /**
     * @description 从数据库取用户数据
     * @param {Object} state vuex state
     */
    async load ({ state, dispatch }) {
      // store 赋值
      state.info = await dispatch('d2admin/db/get', {
        dbName: 'sys',
        path: 'mvc.info',
        defaultValue: setting.mvc.info,
        user: true
      }, { root: true })
      state.deviceId = await window.$vp.cacheLoadFromSessionStore('DEVICE_ID', uuidv4())
      state.oauth2AccessToken = await window.$vp.cacheLoadFromSessionStore('TOKEN_INFO', {})
    }
  }
}
