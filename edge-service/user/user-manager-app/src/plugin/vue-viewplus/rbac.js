/**
 * RBAC权限控制模块
 */
import _ from 'lodash';
// _onAjaxReqCheckFail
let _debug, _errorHandler, _installed, _onPathCheckFail, _modifyLoginState, _isRESTfulInterfaces
let _publicPaths = []
let _authorizedPaths = []
let _authorizeInterfaces = []
let _authorizeResourceAlias = []

/**
 * 是否是【超级管理员】
 * 如果登录用户是这个`角色`，那么就无需进行各种授权控制检测
 * @type {boolean}
 * @private
 */
let _superAdminStatus = false

const _compare = function(rule, path) {
  let temp = false
  if (_.isRegExp(rule)) {
    temp = rule.test(path)
  } else {
    temp = _.isEqual(path, rule)
  }
  return temp
}

/**
 * 检测登录用户是否具有访问对应页面的权限
 * 1.校验是否登录
 * 2.校验带访问的页面是否在`loginStateCheck#authorizedPaths`授权`paths`集合中
 * @param to
 * @param from
 * @param next
 * @private
 */
const _rbacPathCheck = function(to, from, next) {
  if (_superAdminStatus) {
    next();
    return;
  }
  try {
    // 默认认为所有资源都需要进行权限控制
    let isAllow = false
    const path = to.path;
    // 先检测公共页面集合
    const publicPathsLength = _publicPaths.length
    for (let i = publicPathsLength; i--;) {
      const rule = _publicPaths[i];
      isAllow = _compare(rule, path)
      if (isAllow) {
        break;
      }
    }
    // 非公共页面 && 已经登录
    if (!isAllow && this.isLogin()) {
      // 检测已授权页面集合
      const authorizedPathsLength = _authorizedPaths.length;
      for (let i = authorizedPathsLength; i--;) {
        const rule = _authorizedPaths[i];
        isAllow = _compare(rule, path);
        if (isAllow) {
          break;
        }
      }
    }

    if (isAllow) {
      next();
    } else {
      if (_.isFunction(_onPathCheckFail)) {
        if (_debug) {
          console.error(`[v+] RBAC模块检测：用户无权访问【${path}】，回调onPathCheckFail钩子`);
        }
        this::_onPathCheckFail(to, from, next);
      } else {
        next(new Error('check_authorize_paths_fail'));
      }
    }
  } catch (e) {
    if (_debug) {
      console.error(`[v+] RBAC模块检测出错: ${e.message}`);
    }
    if (_.isFunction(_errorHandler)) {
      this::_errorHandler(e)
    }
  }
};

const _restoreState = function() {
  _publicPaths = this.cacheLoadFromSessionStore('PUBLIC_PATHS', [])
  _authorizedPaths = this.cacheLoadFromSessionStore('AUTHORIZED_PATHS', [])
  _authorizeInterfaces = this.cacheLoadFromSessionStore('AUTHORIZED_INTERFACES', [])
  _authorizeResourceAlias = this.cacheLoadFromSessionStore('AUTHORIZED_RESOURCE_ALIAS', [])
  _superAdminStatus = this.cacheLoadFromSessionStore('AUTHORIZED_SUPER_ADMIN_STATUS', false)
}

/**
 * 校验给定指令显示声明所需列表是否包含于身份认证用户所具有的权限集合中，如果是则返回`true`标识权限校验通过
 * @param statementAuth
 * @param authorizeCollection
 * @returns {boolean}
 * @private
 */
const _checkPermission = function(statementAuth, authorizeCollection) {
  let voter = []
  statementAuth.forEach(url => {
    voter.push(authorizeCollection.includes(url))
  })
  return !voter.includes(false)
}

/**
 * {@link _checkPermission} 附加了对接口类型的校验
 * @param statementAuth
 * @param authorizeCollection
 * @returns {boolean}
 * @private
 */
const _checkPermissionRESTful = function(statementAuth, authorizeCollection) {
  let voter = []
  const expectedSize = statementAuth.length
  const size = authorizeCollection.length
  for (let i = 0; i < size; i++) {
    const itf = authorizeCollection[i]
    if (_.find(statementAuth, itf)) {
      voter.push(true)
      // 移除判断成功的声明权限对象
      statementAuth.splice(i, 1)
    }
  }
  // 如果投票得到的true含量和需要判断的声明权限长度一致，则标识校验通过
  return voter.length === expectedSize
}

const _parseAccessDirectiveValue2Arr = function(value) {
  let params = []
  if (_.isString(value) || _.isPlainObject(value)) {
    params.push(value)
  } else if (_.isArray(value)) {
    params = value
  } else {
    throw new Error('access 配置的授权标识符不正确，请检查')
  }
  return params
}

/**
 * 推荐使用资源标识配置：`v-access:alias[.disable]="'LOGIN'"` 前提需要注入身份认证用户所拥有的**授权资源标识集合**，因为这种方式可以较少比较的次数
 * 传统使用接口配置：`v-access:[url][.disable]="'admin'"` 前提需要注入身份认证用户所拥有的**授权接口集合**
 * 两种都支持数组配置
 * v-access:alias[.disable]="['LOGIN', 'WELCOME']"
 * v-access:[url][.disable]="['admin', 'admin/*']"
 * 针对于RESTful类型接口：
 * v-access="[{url: 'admin/search/*', method: 'POST'}]"
 * 默认使用url模式，因为这种方式比较通用
 * v-access="['admin', 'admin/*']"
 * <p>
 *   其中`[.disbale]`用来标明在检测用户不具有对当前声明的权限时，将会把当前声明指令的`el`元素添加`el.disabled = true`，默认则是影藏元素：`el.style.display = 'none'`
 * <p>
 *   举例：`<el-form v-access="['admin/search']" slot="search-inner-box" :inline="true" :model="searchForm" :rules="searchRules" ref="ruleSearchForm" class="demo-form-inline">...</el-form>`
 *   上面这个检索表单需要登录用户具有访问`'admin/search'`接口的权限，才会显示
 * @param Vue
 * @private
 */
const _createRBACDirective = function(Vue) {
  Vue.directive('access', {
    bind: function(el, { value, arg, modifiers }) {
      if (_superAdminStatus) {
        return;
      }
      let isAllow = false
      const statementAuth = _parseAccessDirectiveValue2Arr(value)
      switch (arg) {
        case 'alias':
          isAllow = _checkPermission(statementAuth, _authorizeResourceAlias)
          break
        // 默认使用url模式
        case 'url':
        default:
          if (_isRESTfulInterfaces) {
            isAllow = _checkPermissionRESTful(statementAuth, _authorizeInterfaces)
          } else {
            isAllow = _checkPermission(statementAuth, _authorizeInterfaces)
          }
      }

      if (!isAllow) {
        if (_debug) {
          console.warn(`[v+] RBAC access权限检测不通过：用户无权访问【${_.isObject(value) ? JSON.stringify(value) : value}】`);
        }
        if (_.has(modifiers, 'disable')) {
          el.disabled = true;
          el.style.opacity = '0.5'
        } else {
          el.style.display = 'none';
        }
      }
    }
  })
}

/**
 * 用于在发送ajax请求之前，对待请求的接口和当前集合进行匹配，如果匹配失败说明用户就没有请求权限，则直接不发送后台请求，减少后端不必要的资源浪费
 * @private
 */
// const _rbacAjaxCheck = function() {
//   this.getAjaxInstance().interceptors.request.use(
//     (config) => {
//       console.log('req', config)
//       const { url, method } = config
//       const statementAuth = []
//       let isAllow
//       if (_isRESTfulInterfaces) {
//         const _method = _.toUpper(method)
//         statementAuth.push({ url, method: _method });
//         isAllow = _checkPermissionRESTful(statementAuth, _authorizeInterfaces)
//         // TODO 因为拦截到的请求`{url: "admin/0/1/10", method: "GET"}` 没有找到类似java中org.springframework.util.AntPathMatcher;
//         // 那样能匹配`{url: "admin/*/*/*", method: "GET"}`，的方法`temp = antPathMatcher.match(anInterface.getUrl(), reqURI)`
//         // 故这个需求暂时没法实现 ：）
//         console.log('statementAuth', isAllow, statementAuth, _authorizeInterfaces)
//       } else {
//         isAllow = _checkPermission(statementAuth, _authorizeInterfaces)
//       }
//       if (isAllow) {
//         return config;
//       } else {
//         if (_debug) {
//           console.warn(`[v+] RBAC ajax权限检测不通过：用户无权发送请求【${method}-${url}】`);
//         }
//         if (_.isFunction(_onAjaxReqCheckFail)) {
//           this::_onAjaxReqCheckFail(config);
//         } else {
//           throw new Error('check_authorize_ajax_req_fail');
//         }
//       }
//     },
//     error => {
//       return Promise.reject(error)
//     }
//   )
// }

const rbacModel = {
  /**
   * 代理`$vp#login-state-check`模块的同名方法，以实现在登出、会话超时踢出的时候清理本模块维护的登录之后设置的状态
   * @param status
   */
  modifyLoginState(status = false) {
    this::_modifyLoginState(status)
    if (!status) {
      this::rbacModel.rabcUpdateSuperAdminStatus(false)
      this::rbacModel.rabcUpdateAuthorizedPaths([])
      this::rbacModel.rabcUpdateAuthorizeInterfaces([])
    }
  },
  /**
   * 【可选】有些系统存在一个超级用户角色，其可以访问任何资源、页面，故如果设置，针对这个登录用户将不会做任何权限校验，以便节省前端资源
   * @param status
   */
  rabcUpdateSuperAdminStatus(status) {
    _superAdminStatus = status;
    this.cacheSaveToSessionStore('AUTHORIZED_SUPER_ADMIN_STATUS', _superAdminStatus)
  },
  /**
   * 添加授权路径集合
   * 如：登录完成之后，将用户被授权可以访问的页面`paths`添加到`LoginStateCheck#authorizedPaths`中
   * @param paths
   */
  rabcAddAuthorizedPaths(paths) {
    this::rbacModel.rabcUpdateAuthorizedPaths(_.concat(_authorizedPaths, paths))
  },
  /**
   * 更新授权路径集合
   * @param paths
   */
  rabcUpdateAuthorizedPaths(paths) {
    _authorizedPaths = [...new Set(paths)]
    this.cacheSaveToSessionStore('AUTHORIZED_PATHS', _authorizedPaths)
  },
  /**
   * 更新授权接口集合
   * @param interfaces
   */
  rabcUpdateAuthorizeInterfaces(interfaces) {
    _authorizeInterfaces = [...new Set(interfaces)]
    this.cacheSaveToSessionStore('AUTHORIZED_INTERFACES', _authorizeInterfaces)
  },
  /**
   * 添加授权接口集合
   * @param interfaces
   */
  rabcAddAuthorizeInterfaces(interfaces) {
    this::rbacModel.rabcUpdateAuthorizeInterfaces(_.concat(_authorizeInterfaces, interfaces))
  },
  /**
   * 更新资源别名集合
   * @param alias
   */
  rabcUpdateAuthorizeResourceAlias(alias) {
    _authorizeResourceAlias = [...new Set(alias)]
    this.cacheSaveToSessionStore('AUTHORIZED_RESOURCE_ALIAS', _authorizeResourceAlias)
  },
  /**
   * 添加资源别名集合
   * @param alias
   */
  rabcAddAuthorizeResourceAlias(alias) {
    this::rbacModel.rabcUpdateAuthorizeResourceAlias(_.concat(_authorizeResourceAlias, alias))
  },
  /**
   * 更新公共路径集合
   * @param paths
   */
  rabcUpdatePublicPaths(paths) {
    _publicPaths = [...new Set(paths)];
    this.cacheSaveToSessionStore('PUBLIC_PATHS', _publicPaths)
  },
  /**
   * 添加公共路径集合
   * @param paths
   */
  rabcAddPublicPaths(paths) {
    this::rbacModel.rabcUpdatePublicPaths(_.concat(_publicPaths, paths))
  },
  install(Vue, {
    /**
     * 参考：http://jiiiiiin.cn/vue-viewplus/#/global_configuration?id=debug-
     */
    debug = false,
    /**
     * 参考：http://jiiiiiin.cn/vue-viewplus/#/global_configuration?id=errorhandler-
     */
    errorHandler = null,
    /**
     * 参考：http://jiiiiiin.cn/vue-viewplus/#/global_configuration?id=router-
     */
    router = null,
    /**
     * 参考：http://jiiiiiin.cn/vue-viewplus/#/global_configuration?id=installed
     */
    installed = null,
    /**
     * [*] 系统公共路由path路径集合，即可以让任何人访问的页面路径
     * {Array<Object>}
     * <p>
     *   比如登录页面的path，因为登录之前我们是无法判断用户是否可以访问某个页面的，故需要这个配置，当然如果需要这个配置也可以在初始化插件之前从服务器端获取，这样前后端动态性就更高，但是一般没有这种需求：）
     * <p>
     * 数组中的item，可以是一个**正则表达式字面量**，如`[/^((\/Interbus)(?!\/SubMenu)\/.+)$/]`，也可以是一个字符串
     * <p>
     * 匹配规则：如果在`LoginStateCheck#publicPaths`**系统公共路由path路径集合**中，那么就直接跳过权限校验
     */
    publicPaths = [],
    /**
     * [*] 登录用户拥有访问权限的路由path路径集合
     * {Array<Object>}
     * <p>
     * 数组中的item，可以是一个**正则表达式字面量**，如`[/^((\/Interbus)(?!\/SubMenu)\/.+)$/]`，也可以是一个字符串
     * <p>
     * 匹配规则：如果在`LoginStateCheck#authorizedPaths`**需要身份认证规则集**中，那么就需要查看用户是否登录，如果没有登录就拒绝访问
     */
    authorizedPaths = [],
    /**
     * [可选] 登录用户拥有访问权限的资源别名集合
     * {Array<Object>}
     * <p>
     * 数组中的item，可以是一个**正则表达式字面量**，如`[/^((\/Interbus)(?!\/SubMenu)\/.+)$/]`，也可以是一个字符串
     * <p>
     * 匹配规则：因为如果都用`LoginStateCheck#authorizeInterfaces`接口进行匹配，可能有一种情况，访问一个资源，其需要n个接口，那么我们在配置配置权限指令：v-access="[n, n....]"的时候就需要声明所有需要的接口，就会需要对比多次，
     * 当我们系统的接口集合很大的时候，势必会成为一个瓶颈，故我们可以为资源声明一个别名，这个别名则可以代表这n个接口，这样的话就从n+减少到n次匹配；
     */
    authorizeResourceAlias = [],
    /**
     * [*] 登录用户拥有访问权限的后台接口集合
     * {Array<Object>}
     * <p>
     *   1.在`v-access`指令配置为url（默认）校验格式时，将会使用该集合和指令声明的待审查授权接口列表进行匹配，如果匹配成功，则指令校验通过，否则校验不通过，会将对应dom元素进行处理
     *   2.TODO 将会用于在发送ajax请求之前，对待请求的接口和当前集合进行匹配，如果匹配失败说明用户就没有请求权限，则直接不发送后台请求，减少后端不必要的资源浪费
     * <p>
     * 数组中的item，可以是一个**正则表达式字面量**，如`[/^((\/Interbus)(?!\/SubMenu)\/.+)$/]`，也可以是一个字符串
     * <p>
     * 匹配规则：将会用于在发送ajax请求之前，对待请求的接口和当前集合进行匹配，如果匹配失败说明用户就没有请求权限，则直接不发送后台请求，减少后端不必要的资源浪费
     * <p>
     *   注意需要根据`isRESTfulInterfaces`属性的值，来判断当前集合的数据类型：
     *
     * 如果`isRESTfulInterfaces`设置为`false`，则使用下面的格式：
     * ```json
     * ["admin/dels/*", ...]
     * ```
     * 如果`isRESTfulInterfaces`设置为`true`，**注意这是默认设置**，则使用下面的格式：
     * ```json
     * [[{url: "admin/dels/*", method: "DELETE"}, ...]]
     * ```
     */
    authorizeInterfaces = [],
    /**
     * [*] 声明`authorizeInterfaces`集合存储的是RESTful类型的接口还是常规接口
     * 1. 如果是（true），则`authorizeInterfaces`集合需要存储的结构就是:
     * [{url: 'admin/dels/*', method: 'DELETE'}]
     * 即进行接口匹配的时候会校验类型
     * 2. 如果不是（false），则`authorizeInterfaces`集合需要存储的结构就是，即不区分接口类型:
     * ['admin/dels/*']
     */
    isRESTfulInterfaces = true,
    /**
     * [*] `$vp::onPathCheckFail(to, from, next)`
     * <p>
     * 访问前端页面时权限检查失败时被回调
     */
    onPathCheckFail = null,
    /**
     * [*] `$vp::onPathCheckFail(to, from, next)`
     * <p>
     * 发送ajax请求时权限检查失败时被回调
     */
    onAjaxReqCheckFail = null
  } = {}) {
    _debug = debug
    _errorHandler = errorHandler
    _installed = installed
    _modifyLoginState = this.modifyLoginState
    _isRESTfulInterfaces = isRESTfulInterfaces
    // 恢复模块状态
    this:: _restoreState()
    this::rbacModel.rabcAddPublicPaths(publicPaths)
    this::rbacModel.rabcAddAuthorizedPaths(authorizedPaths)
    this::rbacModel.rabcAddAuthorizeInterfaces(authorizeInterfaces)
    this::rbacModel.rabcAddAuthorizeResourceAlias(authorizeResourceAlias)
    _onPathCheckFail = onPathCheckFail;
    router.beforeEach((to, from, next) => {
      this::_rbacPathCheck(to, from, next);
    });
    this::_createRBACDirective(Vue)
    // TODO 因为路径匹配的原因占时不支持
    // _onAjaxReqCheckFail = onAjaxReqCheckFail;
    // this::_rbacAjaxCheck()
  },
  installed() {
    if (_.isFunction(_installed)) {
      this::_installed()
    }
  }
};

export default rbacModel;
