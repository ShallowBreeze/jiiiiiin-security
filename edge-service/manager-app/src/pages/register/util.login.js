import _ from 'lodash'
/**
 * 删除空`children`节点
 * @param menus
 * @returns {Array}
 * @private
 */
export const delEmptyChildren = function(menus) {
  const res = [];
  menus.forEach((item, idx) => {
    if (_.isEmpty(item.children)) {
      delete item.children;
    } else {
      // 递归删除
      item.children = delEmptyChildren(item.children);
    }
    // 删除多余的字段
    delete item.num
    res.push(item);
  });
  return res;
}

export const parseAuthorizePaths = function(resources) {
  const res = {};
  const paths = [];
  const aliasArr = [];
  resources.forEach(resource => {
    const path = resource.path;
    const alias = resource.alias;
    if (!_.isEmpty(path)) {
      paths.push(path);
    }
    if (!_.isEmpty(alias)) {
      aliasArr.push(alias);
    }
  });
  res.paths = paths
  res.alias = aliasArr
  return res;
}

export const parseAuthorizeInterfaces = function(interfaes) {
  const res = [];
  interfaes.forEach(({ url, method } = {}) => {
    if (!_.isEmpty(url) && !_.isEmpty(method)) {
      res.push({
        url, method
      });
    }
  });
  return res;
}

export const parseUserRoleIsSuperAdminStatus = function(roles) {
  let res = false;
  for (let i = 0; i < roles.length; i++) {
    const role = roles[i];
    res = role.authorityName === 'ADMIN';
    if (res) {
      break;
    }
  }
  return res;
}
