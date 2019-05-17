import setting from '@/setting'
// 基于browser模块session方式的登录
// export function AccountLogin($vp, params) {
//   return $vp.ajaxPost('/authentication/form', {
//     params
//   })
// }

/**
 * 登录接口
 * @param {Object} $vp vp实例
 * @param {JSON Object} params 用户名和密码相关登录变化参数
 */
export function AccountLogin($vp, params) {
  return $vp.ajaxPost('/ac/oauth/token', {
    params: {
      grant_type: 'password',
      scope: setting.user.auth.scope,
      ...params
    },
    axiosOptions: {
      headers: {
        isToken: false,
        Authorization: setting.user.auth.Authorization
      }
    }
  })
}
