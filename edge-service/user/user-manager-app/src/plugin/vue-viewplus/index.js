import router from '@/router/index';
import store from '@/store/index';

export const mixinConfig = {
  baseUrl: process.env.VUE_APP_SEVER_URL,
  serverUrl: `${process.env.VUE_APP_SEVER_URL}${process.env.VUE_APP_API}`
};

const viewPlusOptions = {
  baseUrl: mixinConfig.baseUrl,
  serverUrl: mixinConfig.serverUrl,
  router,
  store,
  debug: process.env.NODE_ENV !== 'production',
  runNative: false,
  errorHandler(err) {
    if (err.code) {
      switch (err.code) {
        case 'RUN_EVN_NOT_SUPPORT':
          break
        default:
          console.error('收到v+全局错误处理通知', err);
      }
    } else {
      console.error('收到v+全局错误处理通知', err);
    }
  },
  utilHttp: {
    baseURL: mixinConfig.serverUrl,
    headers: {
      Accept: 'application/json'
    },
    // 适配后端`com.baomidou.mybatisplus.extension.api.R`接口
    msgKey: 'msg',
    dataKey: 'data',
    errInfoOutDataObj: true,
    defShowLoading: true,
    onParseServerResp(response) {
      return response.data.code !== 0;
    },
    onReqErrParseMsg(response, errMsg) {
      switch (errMsg) {
        case '未解析到服务端返回的错误消息':
          return errMsg;
        case '参数传递错误':
          let res = `参数传递错误`
          response.data.forEach(err => {
            res += `${err.defaultMessage}\n`
          });
          return res
      }
      return `${errMsg} [服务端]`;
    },
    onReqErrParseHttpStatusCode(status, response) {
      console.log('onReqErrParseHttpStatusCode', status)
      let res = false;
      switch (status) {
        case 403:
          this.toast('权限不足', {
            type: 'error'
          });
          // 要判断是初始化页面发送请求时候出发的`403`很麻烦
          // store.dispatch('d2admin/page/closeCurrent', { vm: this });
          res = true;
          break;
        case 401:
          // TODO 待测试
          console.log('401 111 onSessionTimeOut', viewPlusOptions)
          this::viewPlusOptions.utilHttp.accessRules.onSessionTimeOut(response)
          console.log('401 onSessionTimeOut')
          res = true;
          break;
      }
      return res
    },
    errDialog(content = '错误消息未定义') {
      // 排除一些不需要弹窗（http模块）
      switch (content) {
        case '未授权，请登录':
          return this;
        case 'Bad credentials [服务端]':
          // 临时处理，待解决SpringSecurity这种特殊情况
          // https://www.cnblogs.com/jifeng/archive/2012/06/09/2542928.html
          content = '用户名或密码错误，请重试'
          break
      }
      this.dialog(content, {
        title: '错误消息'
      });
      return this;
    },
    loading(loadingHintText) {
      this.loading(loadingHintText);
    },
    hideLoading() {
      this.hideLoading();
    },
    accessRules: {
      sessionTimeOut: [-2],
      onSessionTimeOut(response) {
        console.log('def onSessionTimeOut')
        this.toast('会话超时，请重新登录', {
          type: 'error'
        });
        store.dispatch('d2admin/account/logout', {
          vm: this
        });
        store.dispatch('d2admin/page/closeAll', {
          vm: router.app
        });
      },
      unauthorized: ['core_error_unauthorized'],
      onUnauthorized(response) {
        console.error(`onUnauthorized ${response}`);
        this.toast('您尚未登录，请先登录', {
          type: 'error'
        });
      }
    }
  }
};

export default viewPlusOptions
