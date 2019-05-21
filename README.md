# jiiiiiin-security

一个SpringCloud微服务学习项目 && 一个前后端分离的权限管理基础项目

+ [MyBatis-Plus优秀案例之一](https://mp.baomidou.com/guide/#%E4%BC%98%E7%A7%80%E6%A1%88%E4%BE%8B)
+ [d2-admin优秀案例之一](https://github.com/d2-projects/d2-admin#open-source-backend-implementation)

# 原则

+ 以最少的表结构字段完成一个基础应用，以便在以此完成实际项目时有更多的扩充自由

> **如果你仅仅需要一个简单的内管系统，请切换到[master](https://github.com/Jiiiiiin/jiiiiiin-security/tree/master)分支，其仅仅是一个spring-boot前后端分离基础应用，当前分支，是用来学习和实践spring-cloud微服务框架所开**


# 功能截图

以下是部分功能截图

|  |  |
| ------ | ------ |
| ![](https://ws4.sinaimg.cn/large/006tNc79gy1fzpv0weyyhj31c00u0mz6.jpg) | ![](https://ws4.sinaimg.cn/large/006tNbRwgy1fxy9om3ct3j31hc0u0dpu.jpg) |
| ![](https://ws3.sinaimg.cn/large/006tNbRwgy1fxw90anl1yj31c00u0taw.jpg) | ![](https://ws3.sinaimg.cn/large/006tNbRwgy1fyazwdryopj31hc0u0qa5.jpg) |
| ![](https://ws2.sinaimg.cn/large/006tNbRwgy1fy2tx2neg1j31hc0u0q9b.jpg) | ![](https://ws1.sinaimg.cn/large/006tNc79gy1fzm4vm6f3pj31c00u0q5t.jpg) |
| ![](https://ws4.sinaimg.cn/large/006tKfTcgy1g063daokoij31cy0u0jx2.jpg) | ![](https://ws4.sinaimg.cn/large/006tKfTcgy1g0daq2y8qwj31d30u0du9.jpg) |
| ![](https://ws2.sinaimg.cn/large/006tKfTcgy1g0ds20z3muj31d30u0dlw.jpg) | ![](https://ws2.sinaimg.cn/large/006tKfTcgy1g0jmwac98nj31d30u0jww.jpg) |
| ![](https://ws3.sinaimg.cn/large/006tKfTcgy1g11m4k4sgtj31d30u076s.jpg) | ![](https://ws4.sinaimg.cn/large/006tKfTcgy1g14ok7i3n4j31d30u07g5.jpg) |

# 项目结构说明

```bash
.
├── pom.xml 公共pom
├── apollo-cache-dir (apollo本地缓存目录，见配置)
├── config 各个边界服务、后端服务的apollo配置目录
├── db 数据库初始化脚本
├── jiiiiiin-lib 自定义库
│   ├── jiiiiiin-data 数据层模块，目前主要针对Mybatis-Plus、redis（lib）
│   ├── jiiiiiin-security-app 针对SpringSecurity OAuth2 + Token(default/JWT)的安全模块(lib)
│   ├── jiiiiiin-security-authorize 后端RBAC抽象模块(lib)
│   ├── jiiiiiin-security-browser 针对Session的安全层模块(lib)
│   ├── jiiiiiin-security-core 安全层基础模块，处理Spring-Security相关基础配置（lib）
│   ├── jiiiiiin-mvc 应用开发通用模块(lib)
├── jiiiiiin-eureka-server 注册中心服务端(通用服务)
├── jiiiiiin-gateway 网关(通用服务)
├── jiiiiiin-hystrix
│   ├── jiiiiiin-hystrix-dashboard Hystrix熔断监服务(通用服务)
│   ├── jiiiiiin-hystrix-tuibine Hystrix Turbine聚合服务(通用服务)
├── edge-service 边界服务(pom)
│   ├── auth-center-server 统一用户认证中心应用
│   ├── manager-app 内管前端应用（vue项目，依赖d2-admin模块（1.6.9最新））
├── middle-tier-service 后端服务(pom)
│   └── user 用户服务(pom)
│       ├── user-common 用户公共代码(lib) 
│       ├── user-client 用户Feign客户端(lib) 
│       ├── user-server 用户应用
│   ├── order 订单服务(pom)
│   │   ├── order-server 后端应用
│   ├── product 商品服务(pom)
│   │   ├── product-client 商品Feign客户端(lib)
│   │   ├── product-server 后端应用
```

> 微服务代码划分，以便更好的管理项目，另外模块约定也可以为做代码生成做准备

- `middle-tier-service`为【原子服务】
  - 一个原子服务，有划分为：
    - 业务标识-client Feign客户端（提供给调用方使用）
    - 业务标识-server 服务本身
    - 业务标识-common 当前原子服务中client和server共同依赖的代码，如实体等
- `edge-service`为【边界服务服务】
  - 边界服务即提供给外部客户端（如：App）来调用，组织比较自由，但有一个原则及其只会依赖【原子服务】，一般不作为内部服务提供者

即下图中的`Edge Service`和`Middle Tier Service`：

![](https://ws2.sinaimg.cn/large/006tKfTcly1g0kscyq1s0j317m0j00ue.jpg)

> 关于边界服务和原子服务的理解可以参考，[【微服务架构~BFF和网关是如何演化出来的】](https://mp.weixin.qq.com/s/F6WalEvx69_BCJ0z1dW1cg)



# 快速开始

+ 视频演示

> 注意：目前该视频是针对[master](https://github.com/Jiiiiiin/jiiiiiin-security/tree/master)分支录制，等当前分支对spring-cloud实践有一个基础眉目我会在重新录制响应视频，但是这个视频对于`user-server & manager-app`也具有参考价值

[![Watch the video](https://ws2.sinaimg.cn/large/006tNc79gy1fzqotcb0i4j31410u07dl.jpg)](https://www.youtube.com/embed/eemHJEvsTog)

[下载高清视频](https://pan.baidu.com/s/1ZZmw7idemDWD0-tnmb1GHA)

+ 提示步骤：

    + 修改本地hosts

        + [win配置方法](https://www.jb51.net/os/win10/395409.html) | [mac配置方法](https://www.jianshu.com/p/752211238c1b) | 建议使用 switchhost，开源群下载,对自己的网络环境自信的朋友，也可以直接[官网](https://github.com/oldj/SwitchHosts/releases)下载

            ```bash
            # 本地测试环境
            127.0.0.1   jiiiiiin-redis
            127.0.0.1   jiiiiiin-mysql
            127.0.0.1   jiiiiiin-eureka
            127.0.0.1   jiiiiiin-hystrix-dashboard
            127.0.0.1   jiiiiiin-hystrix-tuibine
            127.0.0.1   jiiiiiin-springboot-admin
            127.0.0.1   user-server
            127.0.0.1   auth-center-server
            127.0.0.1   jiiiiiin-gateway
            127.0.0.1   manager-app
            ```
  + [导入数据脚本](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/db/sql-mysql.sql)

  + 启动jiiiiiin-mysql & jiiiiiin-redis

  + 启动[apollo](https://github.com/ctripcorp/apollo/wiki/Quick-Start)，并参考`config`目录中的`xxx.properties`创建各个应用的apollo对应项目，并自行配置所需配置信息

  > 注意这里可以自行控制apollo的连接环境，可以使用`apollo-Quick-Start`快速上手实践

  + 启动eureka::DiscoveryServerApplication
  + 启动监控服务
  + hystrix-dashboard::HystrixDashboardApplication
  + hystrix-tuibine::HystrixTuibineApplication
  + springboot-admin::SpringBootAdminApplication
  + zipkin::[集成方式参考](https://windmt.com/2018/04/24/spring-cloud-12-sleuth-zipkin/)，建议使用docker直接部署服务端
  + 启动统一用户认证中心应用auth-center-server::AuthCenterApplication
  + 启动用户服务 user-server::UserApplication::9999
  + 启动网关 gateway::ZuulGatewayApplication::8861
  + 启动前端内管应用 manager-app::`j manager-app && npm run serve`

  > 一切ok，就可以直接访问manager-app 查看管理控制台了 ：）




# 自定义配置

| 配置内容                                | 位置                                                         |
| --------------------------------------- | ------------------------------------------------------------ |
| 自定义properties配置`jiiiiiin.security` | [SecurityProperties.java](https://github.com/Jiiiiiin/jiiiiiin-security/blob/feature/springcloud/jiiiiiin-lib/jiiiiiin-security-core/src/main/java/cn/jiiiiiin/security/core/properties/SecurityProperties.java#L26) 目前提供了针对公共接口、浏览器安全、验证码、OAuth2等相关配置 |
| 可覆盖的Bean配置                        | 待整理~~                                                     |

# 设计

### 安全设计

![js-oauth](https://ws1.sinaimg.cn/large/006tNc79gy1g38p0fwv1cj30tr0kp76m.jpg)

上图简要描述了整个系统的安全相关设计，我们可以从上往下看：

+ 客户端应用发送身份认证请求

+ 请求通过网关直接发送到身份认证中心(OAUTH-CENTER-SERVER)

+ 身份认证中心通过FeignClient传递用户名或手机号到用户服务

+ 用户服务到数据库查询当前认证用户信息(基础信息、角色信息、资源信息、可访问接口信息)，查询到之后返回给身份认证中心

+ 身份认证中心得到用户信息组装成一个认证通过的Authentication标识认证完成，**并在此阶段将认证用户对象缓存到redis数据库**，**这样的好处是，分离的资源和认证服务器后续都通过它来共享当前认证用户信息**，**避免SpringSecurity OAuth的[性能问题**](https://juejin.im/post/5c9191785188252d7941f87c)

+ 并返回给客户端应用两个重要信息：TOKEN信息对象(包含访问令牌、刷新令牌和自建的身份认证对象存储在缓存数据库中的key `cache_principal`)和用户数据：

  类似：

  ```json
  {
      "code": 0,
      "data": {
          "admin": {
              "id": "1",
              "channel": "MNG",
              "username": "admin",
              "phone": "15399999999",
              "email": "15399999999@163.com",
              "roles": [
                  {
                      "id": "1061277220292595713",
                      "name": "系统管理员",
                      "authorityName": "ADMIN"
                  }
              ],
              "authorizeInterfaces": [
                  {
                      "name": "批量删除用户记录",
                      "url": "admin/dels/*",
                      "method": "DELETE"
                  }
                  //...
              ],
              "menus": [
                  {
                      "path": "/index",
                      "title": "首页",
                      "icon": "home",
                      "num": 1
                  },
                  {
                      "path": "/mngauth",
                      "title": "权限管理",
                      "icon": "cog",
                      "num": 2,
                      "children": [
                          {
                              "path": "/mngauth/admin",
                              "title": "用户管理",
                              "icon": "users",
                              "num": 1
                          }
                          //...
                      ]
                  }
              ]
          },
          "oauth2AccessToken": {
              "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTg0MTA3MzksInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiI3NjA5YzU0Yi04NjJkLTRjNGMtYTVhNi0zNTBkYmJmZTgxN2MiLCJjbGllbnRfaWQiOiJwd2ViIiwic2NvcGUiOlsiYWxsIl19.1yKeKlHKzIo4cv1HY8ZqLbLP8LCeCJ5xkdFCL2MGSuE",
              "token_type": "bearer",
              "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI3NjA5YzU0Yi04NjJkLTRjNGMtYTVhNi0zNTBkYmJmZTgxN2MiLCJleHAiOjE1NjA5OTU1MzksImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiMTU5OGJlZDYtMGZhMi00MDMwLWFhOWMtOGQxZWUzZDIwNzRkIiwiY2xpZW50X2lkIjoicHdlYiJ9.Zg39bkwSjT78HN3YN7_8F92URRicHp7D8oRVDnf5yX8",
              "expires_in": 7199,
              "scope": "all",
              "cache_principal": "tokenenhancer-userdetails-1"
          }
      },
      "msg": "执行成功"
  }
  
  ```

  

+ 客户端应用，如前端内管应用 manager-app就会通过用户权限信息初始化[前端rbac模块]、缓存用户基础信息、缓存token信息（特别注意`cache_principal`）

+ 到此认证完成，客户端应用在请求其他服务，如用户服务的修改当前用户密码接口，那么就需要在请求头携带两个重要信息：`Authorization`和`cache_principal`

+ 网关内部的资源服务器通过`Authorization`验证token有效性，针对鉴权，我们有两种思路：

  + 第一种情况，针对内管这类权限灵活运用，如需要RBAC权限处理，我们直接走`.access("@rbacService.hasPermission(request, authentication)")`自定义权限校验器来进行投票
  + 第二种情况，如果针对一般应用，如用户直接使用的商城这类应用，一般对于权限这块只会要求验证用户是否登录、或者是否是某个角色，那么我们就不会走)`自定义权限校验器
  + **这两种情况，就会影响到我们资源服务器在进行`Authentication`对象组装的时候，我们是否还需要去读用户缓存认证信息，即如果是第一种情况，因为权限要素过多我们必须要去缓存数据库重新加载一次，但是如果是第二种情况，我们直接通过JWT 访问令牌解析到的数据已经能够满足认证和鉴权需求就没必要再去加载一次，那反过来想想，我们在认证的时候也可以通过客户端应用渠道标识，来判断是否需要将认证信息缓存到redis**



# 计划

+ SpringCloud

**近期维护：新增[feature/springcloud](https://github.com/Jiiiiiin/jiiiiiin-security/tree/feature/springcloud)分支，将会在该分支尝试spring-cloud探索，目前基于第一代spring-cloud基础组件，核心为Netfix套件，进行一个初探**

![](https://ws3.sinaimg.cn/large/006tKfTcgy1g0dsh2fztyj318q0lkaek.jpg)

预期实践架构(来自[微服务架构实战160讲](https://time.geekbang.org/course/intro/84))

| 功能 | 完成状态 | 简介 |
| ------ | ------ | ------ |
| 实践Eureka 服务注册发现 | 90% | 集成[Service Discovery (Eureka)](https://spring.io/projects/spring-cloud-netflix)服务注册中心 |
| 实践Zuul 服务网关 | 90% | 集成[Intelligent Routing (Zuul)](https://spring.io/projects/spring-cloud-netflix)服务网关 |
| 实践Feign/RestTemplate 服务间通讯和负载均衡 | 90% | 实践服务间的调用 |
| 实践HYSTRIX/Turbine 服务的容错 | 90% | 实现通过Turbine聚合各个服务的Hystrix监控信息，通过`jiiiiiin-hystrix-dashboard`项目完成统一聚合监控，需要在dashboard中键入`jiiiiiin-hystrix-tuibine`的集群监控url，如`http://localhost:8962/turbine.stream` |
| 实践Zipkin 服务链路追踪 | 90% | [集成方式参考](https://windmt.com/2018/04/24/spring-cloud-12-sleuth-zipkin/)，建议使用docker直接部署服务端 |
| 集成Spring Boot Admin | 90% | [集成方式参考](https://codecentric.github.io/spring-boot-admin/2.1.3/#spring-cloud-discovery-support)|
| 实践apollo 服务配置管理| 90% | 实践[Apollo（阿波罗）](https://mp.weixin.qq.com/s/iDmYJre_ULEIxuliu1EbIQ?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=weixin) |
| 实践OAuth2授权认证中心 服务安全 | 90% | 通过Spring Security Oauth2 + JWT来实现，身份认证通过网关走`jiiiiiin-auth-center`，鉴权直接在网关的SpringSecurity Resource服务器完成，并解决[SpringSecurity OAuth2默认的性能问题](https://juejin.im/post/5c9191785188252d7941f87c)|
| 服务实时日志 | 0% |  |
| 代码自动生成 | 0% | [服务端3层代码自动生成](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-module-common/src/main/java/cn/jiiiiiin/module/common/generator/CodeGenerator.java)，待适配目前的目录结构 |
| docker化部署实践 | 0% |  |

+ 业务

> 将会通过一个电商模块来实践服务划分

| 模块名称 | 完成状态 | 简介 |
| ------ | ------ | ------ |
| 用户服务 | **90%** | 开发进行中，将用户管理相关业务抽成一个`user`原子服务，提供给网关、统一权限认证中心、内管等应用使用 |
| 商品服务 | 0% | 商家用来管理自己的商品 |
| 订单服务 | 0% | 商家用来管理自己的订单 |

+ 已经实践的基础功能

| 功能 | 完成状态 | 简介 |
| ------ | ------ | ------ |
| RBAC后端权限控制 | 100% | [基于Spring Security的后端RBAC权限控制](https://github.com/Jiiiiiin/jiiiiiin-security/tree/master/jiiiiiin-security-authorize) |
| RBAC前端权限控制 | 100% | [1.基于vue-viewplus，实现了一个自定义模块](http://jiiiiiin.cn/vue-viewplus/#/global_api?id=mixin-) <br> [2.实现前端页面可访问性控制，通过路由拦截，判断用户待访问页面是否已经授权](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-client-manager/src/plugin/vue-viewplus/rbac.js#L250) <br> [3.实现可见页面的局部UI组件的**可使用性或可见性**控制，基于自定义`v-access`指令，对比声明的接口或资源别是否已经授权](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-client-manager/src/plugin/vue-viewplus/rbac.js#L124)|
| 集成vue-viewplus | 100% | [vue-viewplus一个简化Vue应用开发的工具库](https://github.com/Jiiiiiin/vue-viewplus) |
| 会话并发控制 | 100% | [使用SpringSecurity#concurrency-control实现应用中同一用户在同时只能有一个是终端（渠道）成功登录应用，后登录终端会导致前一个会话失效](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-security-browser/src/main/java/cn/jiiiiiin/security/browser/config/BrowserSpringSecurityBaseConfig.java#L123) |
| 会话集群共享 | 100% | [使用Spring Session与Redis实现会话的共享存储和集群部署](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-server-manager/src/main/resources/application.yml#L20) |
| 记住我控制 | 50% | [使用spring-security实现remember-me功能](https://www.baeldung.com/spring-security-remember-me) |
| d2-mng-page | 100% | [自定义管理页面组件（统一管理：分页、检索、table、编辑）,为了统一审美](https://github.com/Jiiiiiin/jiiiiiin-security/blob/master/jiiiiiin-client-manager/src/components/d2-mng-page/index.vue) |
| Swagger集成 | 100% | [用来构建服务端的RESTFull Api接口](https://swagger.io) |
| spring-mobile | 100% | [用来进行渠道判断](https://projects.spring.io/spring-mobile/)，使得应用能根据请求的设备响应不同的数据格式 |
| Gif验证码 | 100% | [EasyCaptcha](https://github.com/whvcse/EasyCaptcha)和[kaptcha](https://github.com/penggle/kaptcha)两种验证码库的支持 |
| 集成druid监控 | 100% | [druid](https://github.com/alibaba/druid) |
| 自定义短信登录集成 | 100% | 自定义SpringSecurity短信登录 |


# 表结构和权限说明

| 表名称 | 简介 |
| ------ | ------ |
| mng_admin |【用户表】，使用`channel`字段可以区分不同业务系统的用户，如这里`0`标识内管 |
| mng_role |【角色表】，使用`channel`字段可以区分不同业务系统的角色，如这里`0`标识内管 |
| mng_role_admin |【角色用户关联表】，这套系统中，角色和用户是可以多对多配置的 |
| mng_resource |【权限资源表】，使用`channel`字段可以区分不同业务系统的资源，如这里`0`标识内管，另外`type`用来标识资源的类型，在这里只有`类型: 1:菜单(默认) 0:按钮`两种类型，并且没有直接定义一个类`url`字段取标识资源记录对应的后端某一个接口，是因为如一个菜单点击之后到达的页面可能要发多个后台交易，故一个字段来标识这多个交易容易导致混乱，所以在传统的RBAC表结构之下新增了`mng_interface`【系统接口表】来定义，而资源则是给业务人员配置角色或菜单时使用；这张表还有一个特点是将前端Vue Router的页面路径以`path`字段标识，以方便前后端的权限管理 |
| mng_role_resource |【角色资源关联表】，用户、角色、资源都可以由业务人员进行关联操作 |
| mng_interface |【系统接口表】，使用`channel`字段可以区分不同业务系统的接口，如这里`0`标识内管，使用`url+method`来区分后台的某一个接口 |
| mng_resource_interface | 【资源接口关联表】，`mng_resource`和`mng_interface`是多对多关系，因为某一个接口，比如`查询资源树`接口在角色管理列表和资源管理列表两个页面都会被调用，且存在一个资源记录会调用多个接口，故我觉得这样来设计表机构，多加这一张表，才能更清晰的将意思表达到位，且方便维护 |
| persistent_logins | [spring security 记住用户所涉及表](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/remember-me.html)|
| springsocial_UserConnection | [spring social 第三方授权信息关联表](https://docs.spring.io/spring-social/docs/2.0.0.M4/reference/htmlsingle/#section_jdbcConnectionFactory)|

+ 关于前端`rbac`权限控制，详见下面链接：

    - [前端如何配合后端完成RBAC权限控制](https://juejin.im/post/5c1f8d6c6fb9a049e06353aa)
    
    - [Vue 前端应用实现RBAC权限控制的一种方式](https://juejin.im/post/5c19a282f265da61137f372c)

    - 详细配置和api可以点击：[vue-viewplus-自定义RBAC权限控制模块](http://jiiiiiin.cn/vue-viewplus/#/rbac)
    
# 所用技术栈

### 后台

+ [spring-boot](https://github.com/spring-projects/spring-boot)
+ [spring-security](https://github.com/spring-projects/spring-security)
+ [spring-session](https://github.com/spring-projects/spring-session/projects)
+ [mybatis-plus](https://github.com/baomidou/mybatis-plus)
+ [rzwitserloot/lombok](https://github.com/rzwitserloot/lombok)
+ [ctripcorp/apollo](https://github.com/ctripcorp/apollo)
+ [spring-cloud/spring-cloud-netflix](https://github.com/spring-cloud/spring-cloud-netflix)

### 前端    

+ [vue](https://github.com/vuejs/vue)
+ [ElemeFE/element](https://github.com/ElemeFE/element)
+ [vue-viewplus](https://github.com/Jiiiiiin/vue-viewplus)
+ [d2-admin](https://github.com/d2-projects/d2-admin)

<a href="https://github.com/d2-projects/d2-admin" target="_blank"><img src="https://raw.githubusercontent.com/FairyEver/d2-admin/master/doc/image/d2-admin@2x.png" width="200"></a>

# 参考

+ [Spring Security开发安全的REST服务](https://coding.imooc.com/class/134.html)
+ [OAuth2.0最简向导](http://static.iocoder.cn/OAuth2.0%E6%9C%80%E7%AE%80%E5%90%91%E5%AF%BC.pdf)
+ [Spring Security 实现原理与源码解析系统 —— 精品合集](http://www.iocoder.cn/Spring-Security/good-collection/)
+ [微服务的4大设计原则和19个解决方案](https://juejin.im/entry/59bf30876fb9a00a583176a1)
+ [GitHub 9K Star！Apollo作者手把手教你微服务配置中心之道](https://mp.weixin.qq.com/s/iDmYJre_ULEIxuliu1EbIQ?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=weixin)
+ [pig](https://gitee.com/log4j/pig) 目前我们做的事情和pig很像，但是我们有更大的幻想~~



