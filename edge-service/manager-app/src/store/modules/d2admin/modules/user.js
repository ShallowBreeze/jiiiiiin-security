// 设置文件
import setting from '@/setting.js'
// import { uuidv4 } from 'node-uuid';
const uuidv4 = require('uuid/v4')

export default {
  namespaced: true,
  state: {
    // 用户信息
    info: setting.user.info,
    // 用户设备信息
    // https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
    // https://www.zhihu.com/question/51410927
    // TODO 这里之后建议改为后台统一进行唯一id分配，并存储于一个id分配服务
    deviceId: ''
  },
  mutations: {
    setDeviceId(state, deviceId) {
      state.deviceId = deviceId
    }
  },
  actions: {
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
          path: 'user.info',
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
    load ({ state, dispatch }) {
      return new Promise(async resolve => {
        // store 赋值
        state.info = await dispatch('d2admin/db/get', {
          dbName: 'sys',
          path: 'user.info',
          defaultValue: setting.user.info,
          user: true
        }, { root: true })
        state.deviceId = window.$vp.cacheLoadFromSessionStore('DEVICE_ID', uuidv4())
        console.log('load deviceId' + state.deviceId)
        // end
        resolve()
      })
    }
  }
}
