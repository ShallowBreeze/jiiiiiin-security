/**
 * 解决插件依赖的`vue-navigation`会向url动态添加`VNK`key的问题
 * 1.会导致tab每次都打开新的
 * 2.会导致`menu-side`在监控路径设置激活菜单的时候出问题
 * @param fullPath
 * @returns {*}
 */
export const handlerVueNavigationUrlKey = function(fullPath) {
  // TODO aaa
  return fullPath.replace(/\?VNK=[\w]*/, '')
}
