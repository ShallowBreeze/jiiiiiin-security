<template>
  <div class="page-login">
    <div
            class="page-login--layer page-login--layer-time"
            flex="main:center cross:center">
      {{time}}
    </div>
    <div class="page-login--layer">
      <div
              class="page-login--content"
              flex="dir:top main:justify cross:center box:justify">
        <div class="page-login--content-header">
          <p class="page-login--content-header-motto">
            时间是一切财富中最宝贵的财富。 <span>—— 德奥弗拉斯多</span>
          </p>
        </div>
        <div
                class="page-login--content-main"
                flex="dir:top main:center cross:center">
          <!-- logo -->
          <img class="page-login--logo" src="./image/logo@2x.png">
          <!-- 表单 -->
          <div class="page-login--form">
            <el-card shadow="never">
              <div slot="header" class="clearfix">
                <span>用户注册</span>
              </div>
              <el-form ref="loginForm" label-position="top" :rules="rules" :model="formLogin" size="default" @submit.native.prevent>
                <el-form-item  prop="username">
                  <el-input type="text" v-model="formLogin.username" placeholder="用户名">
                    <i slot="prepend" class="fa fa-user-circle-o"></i>
                  </el-input>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input type="password" v-model="formLogin.password" placeholder="密码">
                    <i slot="prepend" class="fa fa-keyboard-o"></i>
                  </el-input>
                </el-form-item>
                <el-form-item prop="code">
                  <el-input type="text" v-model="formLogin.code" placeholder="- - -">
                    <template slot="prepend">验证码</template>
                    <template slot="append">
                      <img ref="codeImageDom" class="login-code" :src="validateImgCodeUri" @click="onChangeValidateImgCode">
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item>
                  <el-checkbox v-model="formLogin.rememberMe" name="remember-me" type="checkbox" value="true">记住我</el-checkbox>
                </el-form-item>
                <el-button size="default" native-type="submit" @click="submit" :disabled="submitBtnDisabled" type="primary" class="button-login">登录</el-button>
              </el-form>
            </el-card>
            <p
                    class="page-login--options"
                    flex="main:justify cross:center">
              <span @click="onForgetPwd"><d2-icon name="question-circle"/> 忘记密码</span>
              <span @click="onQQSignUp">QQ登录</span>
              <span @click="onSignUp">注册用户</span>
            </p>
          </div>
        </div>
        <div class="page-login--content-footer">
          <p class="page-login--content-footer-options">
            <a href="#">帮助</a>
            <a href="#">隐私</a>
            <a href="#">条款</a>
          </p>
          <p class="page-login--content-footer-copyright">
            Copyright <d2-icon name="copyright"/> 2018 D2 Projects 开源组织出品 <a href="https://github.com/FairyEver">@FairyEver</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { mapActions } from 'vuex'
import NProgress from 'nprogress'
import { delEmptyChildren, parseAuthorizePaths, parseAuthorizeInterfaces, parseUserRoleIsSuperAdminStatus } from './util.login.js'

export default {
  data() {
    return {
      timeInterval: null,
      time: dayjs().format('HH:mm:ss'),
      submitBtnDisabled: false,
      validateImgCodeUri: `${this.$vp.options.serverUrl}/code/image`,
      // 表单
      formLogin: {
        username: 'admin',
        password: 'admin',
        rememberMe: true,
        code: ''
      },
      // 校验
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur' }
        ],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.timeInterval = setInterval(() => {
      this.refreshTime()
    }, 1000)
  },
  beforeDestroy() {
    clearInterval(this.timeInterval)
  },
  methods: {
    ...mapActions('d2admin/account', ['login']),
    onQQSignUp() {
      console.log(this.$vp)
      // this.$vp.pageHref(`${this.$vp.options.serverUrl}/auth/qq`)
      window.location.href = `${this.$vp.options.serverUrl}/qqLogin/callback.do`
    },
    onForgetPwd() {
      this.$vp.toast('暂未实现', { type: 'warning' });
    },
    onSignUp() {
      this.$vp.toast('暂未实现', { type: 'warning' });
    },
    refreshTime () {
      this.time = dayjs().format('HH:mm:ss')
    },
    onChangeValidateImgCode: function(e) {
      this.formLogin.code = ''
      console.log('code::', `${this.validateImgCodeUri}?${new Date().getTime()}`)
      e.target.src = `${this.validateImgCodeUri}?${new Date().getTime()}`
    },
    /**
     * @description 接收选择一个用户快速登录的事件
     * @param {Object} user 用户信息
     */
    handleUserBtnClick(user) {
      this.formLogin.username = user.username
      this.formLogin.password = user.password
      this.submit()
    },
    /**
     * @description 提交登录信息
     */
    submit() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          // 登录
          this.login({
            vm: this,
            username: this.formLogin.username,
            password: this.formLogin.password,
            imageCode: this.formLogin.code,
            rememberMe: this.formLogin.rememberMe
          }).then((res) => {
            // 修改用户登录状态
            this.$vp.modifyLoginState(true);
            // 解析服务端返回的登录用户数据，得到菜单、权限相关数据
            const menus = delEmptyChildren(res.principal.admin.menus);
            const authorizeResources = parseAuthorizePaths(res.principal.admin.authorizeResources);
            // console.log('resources', res.principal.admin.authorizeResources)
            // console.log('authorizeResources', authorizeResources)
            this.$vp.rabcUpdateAuthorizedPaths(authorizeResources.paths);
            this.$vp.rabcUpdateAuthorizeResourceAlias(authorizeResources.alias);
            const authorizeInterfaces = parseAuthorizeInterfaces(res.principal.admin.authorizeInterfaces);
            this.$vp.rabcUpdateAuthorizeInterfaces(authorizeInterfaces);
            const isSuperAdminStatus = parseUserRoleIsSuperAdminStatus(res.principal.admin.roles);
            this.$vp.rabcUpdateSuperAdminStatus(isSuperAdminStatus);
            // 设置顶栏菜单
            // this.$store.commit('d2admin/menu/headerSet', menus, { root: true });
            // 设置侧边栏菜单
            this.$store.commit('d2admin/menu/asideSet', menus, { root: true });
            // 初始化菜单搜索功能
            this.$store.commit('d2admin/search/init', menus, { root: true });
            this.$vp.cacheSaveToSessionStore('menus', menus);
            this.$vp.toast('登录成功', {
              type: 'success'
            });
            // 重定向对象不存在则返回顶层路径
            this.$router.replace(this.$route.query.redirect || '/')
          })
          this.$refs.codeImageDom.click()
        } else {
          // 登录表单校验失败
          this.$message.error('表单校验失败')
        }
      })
    }
  },
  created() {
    // 防止多次请求导致的重复调用会话超时函数，重复请求当前页面，导致进度条不会消失的bug
    NProgress.done()
  }
}
</script>

<style lang="scss">
  .page-login {
    @extend %unable-select;
    $backgroundColor: #F0F2F5;
    // ---
    background-color: $backgroundColor;
    height: 100%;
    position: relative;
    // 层
    .page-login--layer {
      @extend %full;
      overflow: auto;
    }
    .page-login--layer-area {
      overflow: hidden;
    }
    // 时间
    .page-login--layer-time {
      font-size: 24em;
      font-weight: bold;
      color: rgba(0, 0, 0, 0.03);
      overflow: hidden;
    }
    // 登陆页面控件的容器
    .page-login--content {
      height: 100%;
      min-height: 500px;
    }
    // header
    .page-login--content-header {
      padding: 1em 0;
      .page-login--content-header-motto {
        margin: 0px;
        padding: 0px;
        color: $color-text-normal;
        text-align: center;
        font-size: 12px;
        span {
          color: $color-text-sub;
        }
      }
    }
    // main
    .page-login--logo {
      width: 240px;
      margin-bottom: 2em;
      margin-top: -2em;
    }
    // 登录表单
    .page-login--form {
      width: 280px;
      // 卡片
      .el-card {
        margin-bottom: 15px;
      }
      // 登录按钮
      .button-login {
        width: 100%;
      }
      // 输入框左边的图表区域缩窄
      .el-input-group__prepend {
        padding: 0px 14px;
      }
      .login-code {
        height: 40px - 2px;
        display: block;
        margin: 0px -20px;
        border-top-right-radius: 2px;
        border-bottom-right-radius: 2px;
      }
      // 登陆选项
      .page-login--options {
        margin: 0px;
        padding: 0px;
        font-size: 14px;
        color: $color-primary;
        margin-bottom: 15px;
        font-weight: bold;
      }
      .page-login--quick {
        width: 100%;
      }
    }
    // 快速选择用户面板
    .page-login--quick-user {
      @extend %flex-center-col;
      padding: 10px 0px;
      border-radius: 4px;
      &:hover {
        background-color: $color-bg;
        i {
          color: $color-text-normal;
        }
        span {
          color: $color-text-normal;
        }
      }
      i {
        font-size: 36px;
        color: $color-text-sub;
      }
      span {
        font-size: 12px;
        margin-top: 10px;
        color: $color-text-sub;
      }
    }
    // footer
    .page-login--content-footer {
      padding: 1em 0;
      .page-login--content-footer-options {
        padding: 0px;
        margin: 0px;
        margin-bottom: 10px;
        font-size: 14px;
        text-align: center;
        a {
          color: $color-text-normal;
          margin: 0 1em;
        }
      }
      .page-login--content-footer-copyright {
        padding: 0px;
        margin: 0px;
        font-size: 12px;
        color: $color-text-normal;
        a {
          color: $color-text-normal;
        }
      }
    }
    // 背景
    .circles {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      overflow: hidden;
      li {
        position: absolute;
        display: block;
        list-style: none;
        width: 20px;
        height: 20px;
        background: #FFF;
        animation: animate 25s linear infinite;
        bottom: -200px;
        @keyframes animate {
          0%{
            transform: translateY(0) rotate(0deg);
            opacity: 1;
            border-radius: 0;
          }
          100%{
            transform: translateY(-1000px) rotate(720deg);
            opacity: 0;
            border-radius: 50%;
          }
        }
        &:nth-child(1) {
          left: 15%;
          width: 80px;
          height: 80px;
          animation-delay: 0s;
        }
        &:nth-child(2) {
          left: 5%;
          width: 20px;
          height: 20px;
          animation-delay: 2s;
          animation-duration: 12s;
        }
        &:nth-child(3) {
          left: 70%;
          width: 20px;
          height: 20px;
          animation-delay: 4s;
        }
        &:nth-child(4) {
          left: 40%;
          width: 60px;
          height: 60px;
          animation-delay: 0s;
          animation-duration: 18s;
        }
        &:nth-child(5) {
          left: 65%;
          width: 20px;
          height: 20px;
          animation-delay: 0s;
        }
        &:nth-child(6) {
          left: 75%;
          width: 150px;
          height: 150px;
          animation-delay: 3s;
        }
        &:nth-child(7) {
          left: 35%;
          width: 200px;
          height: 200px;
          animation-delay: 7s;
        }
        &:nth-child(8) {
          left: 50%;
          width: 25px;
          height: 25px;
          animation-delay: 15s;
          animation-duration: 45s;
        }
        &:nth-child(9) {
          left: 20%;
          width: 15px;
          height: 15px;
          animation-delay: 2s;
          animation-duration: 35s;
        }
        &:nth-child(10) {
          left: 85%;
          width: 150px;
          height: 150px;
          animation-delay: 0s;
          animation-duration: 11s;
        }
      }
    }
  }
</style>
