import layoutHeaderAside from '@/layout/header-aside'

const meta = {}

export default {
  path: '/mngauth',
  name: 'mngauth',
  meta,
  component: layoutHeaderAside,
  children: (pre => [
    { path: 'admin', name: `${pre}admin`, component: () => import('@/pages/mngauth/admin'), meta: { meta, title: '用户管理' } },
    { path: 'role', name: `${pre}role`, component: () => import('@/pages/mngauth/role'), meta: { meta, title: '角色管理' } },
    { path: 'resource', name: `${pre}resource`, component: () => import('@/pages/mngauth/resource'), meta: { meta, title: '资源管理' } },
    { path: 'interface', name: `${pre}interface`, component: () => import('@/pages/mngauth/interface'), meta: { meta, title: '接口管理' } }
  ])('mngauth-')
}
