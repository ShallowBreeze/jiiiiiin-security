module.exports = {
  proxy: {
    // https://segmentfault.com/a/1190000016314976
    '/': {
      ws: false,
      target: 'http://jiiiiiin-user-manager:9090',
      pathRewrite: {
        '^/': ''
      },
      secure: false,
      changeOrigin: true
    }
  },
  open: false,
  quiet: false,
  disableHostCheck: true
}
