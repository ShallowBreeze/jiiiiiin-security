import { mapMutations } from 'vuex'

// TODO 待梳理关联页面通用模板代码
export default {
  data () {
    return {
    }
  },
  methods: {
    ...mapMutations('d2admin', {
      setInitAjaxNum: 'page/setInitAjaxNum'
    })
  }
}
