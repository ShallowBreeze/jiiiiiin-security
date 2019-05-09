import {
  Loading,
  MessageBox,
  Message
} from 'element-ui'

let /* 计时器 */ _t, _gloabLoadingInstance

/**
 * 常用工具函数模块
 */
export default {
  /**
   * > http://element.eleme.io/#/zh-CN/component/message
   * @param {*} msg
   * @param {*} param1
   */
  toast(msg = '默认消息', {
    time = 2000,
    type = 'success'
  } = {}) {
    Message({
      message: msg,
      duration: time,
      type
    })
    return this
  },
  /**
   * > http://element.eleme.io/#/zh-CN/component/message
   * @param {*} content
   * @param {*} param1
   */
  dialog(content = '默认消息', {
    title = '提示',
    buttonText = '确认',
    hideOnBlur = false
  } = {}) {
    return MessageBox.alert(
      content,
      title, {
        confirmButtonText: buttonText,
        distinguishCancelAndClose: hideOnBlur
      })
  },
  /**
   * > http://element.eleme.io/#/zh-CN/component/message-box
   * @param {*} content
   * @param {*} option
   * @return Promise
   */
  confirm(content = '确认消息!', {
    title = '提示',
    confirmText = '确认',
    cancelText = '取消',
    hideOnBlur = false,
    type = 'warning'
  } = {}) {
    return new Promise((resolve, reject) => {
      MessageBox.confirm(
        title,
        content, {
          confirmButtonText: confirmText,
          cancelButtonText: cancelText,
          distinguishCancelAndClose: hideOnBlur,
          type
        })
        .then(this::resolve())
        .catch(this::reject())
    })
  },
  /**
   * Loading 加载
   * @param text
   * @param time
   * @returns {plugin}
   */
  loading(text = '加载中...', {
    time
  } = {}) {
    _gloabLoadingInstance = Loading.service({
      fullscreen: true,
      text
    })
    if (time) {
      _t = setTimeout(() => {
        _gloabLoadingInstance.close()
        if (_t) {
          clearTimeout(_t)
        }
      }, time)
    }
    return this
  },
  hideLoading() {
    if (_t) {
      clearTimeout(_t)
    }
    if (_gloabLoadingInstance) {
      _gloabLoadingInstance.close()
    }
  }
}
