module.exports = {
  proxy: {
    // https://segmentfault.com/a/1190000016314976
    '/mng': {
      target: 'http://jiiiiiin-server-manager:9090',
      pathRewrite: {
        '^/mng': ''
      },
      secure: false,
      changeOrigin: true
    }
  },
  open: false,
  quiet: false,
  disableHostCheck: true
}
