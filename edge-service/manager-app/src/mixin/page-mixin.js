import { mapState } from 'vuex'

export default {
  data () {
    return {
      page: {
        records: [],
        total: 0,
        size: this.$store.state.d2admin.page.defSize,
        current: this.$store.state.d2admin.page.defCurrent
      }
    }
  },
  computed: {
    ...mapState('d2admin', {
      pageSizes: state => state.page.pageSizes
    })
  },
  methods: {
    handleSizeChange(val) {
      this.page.size = val
      // 混合组件实现
      this.qryData()
    },
    handleCurrentChange(val) {
      this.page.current = val
      // 混合组件实现
      this.qryData()
    }
  }
}
