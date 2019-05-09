import layoutHeaderAside from '@/layout/header-aside'

const meta = { requiresAuth: true }

export default {
  path: '/develop',
  name: 'develop',
  meta,
  component: layoutHeaderAside,
  children: (pre => [
    { path: 'swagger-ui', name: `${pre}swagger-ui`, component: () => import('@/pages/develop/swagger-ui'), meta: { meta, title: '接口文档' } },
    { path: 'eureka', name: `${pre}eureka`, component: () => import('@/pages/develop/eureka'), meta: { meta, title: '服务注册中心' } },
    { path: 'druid', name: `${pre}druid`, component: () => import('@/pages/develop/druid'), meta: { meta, title: '数据库监控中心' } },
    { path: 'hystrix', name: `${pre}hystrix`, component: () => import('@/pages/develop/hystrix'), meta: { meta, title: '断路器监控中心' } },
    { path: 'apollo', name: `${pre}apollo`, component: () => import('@/pages/develop/apollo'), meta: { meta, title: '配置中心' } },
    { path: 'zipkin', name: `${pre}zipkin`, component: () => import('@/pages/develop/zipkin'), meta: { meta, title: '服务链路追踪' } },
    { path: 'admin-server', name: `${pre}admin-server`, component: () => import('@/pages/develop/admin-server'), meta: { meta, title: '应用监控中心' } }
  ])('develop-')
}
