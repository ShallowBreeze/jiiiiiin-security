<template>
  <d2-mng-page
          :filename="filename"
          @qry-data="qryData"
          @create="onCreate"
          :create-access-rule="{url: 'interface', method: 'POST'}"
          @update="onUpdate"
          update-access-rule="MNG_INTERFACE_UPDATE"
          @del="onDel"
          :del-access-rule="[{url: 'interface/dels/*', method: 'DELETE'}]"
          :page="page"
          :form-mode.sync="formMode"
          :dialog-form-visible.sync="dialogFormVisible"
          :select-rows="selectRows"
          :show-option-box="true">
    <el-form v-access="{url: 'interface/search/*/*/*', method: 'GET'}" slot="search-inner-box" :inline="true" :model="searchForm" :rules="searchRules" ref="ruleSearchForm" class="demo-form-inline">
      <el-form-item label="渠道" prop="channel">
        <el-select size="small" v-model="searchForm.channel" placeholder="请选择" :required="true" @change="onChangeSearchChannel">
          <el-option
                  v-for="item in channelOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="接口名称">
        <el-input
                size="small"
                placeholder="请输入接口名称"
                v-model="searchForm.name">
        </el-input>
      </el-form-item>
      <d2-el-form-item label="接口地址" >
        <el-input size="small" v-model="searchForm.url" placeholder="请输入接口地址"></el-input>
      </d2-el-form-item>
      <el-form-item label="接口类型" prop="method">
        <el-select size="small" v-model="searchForm.method" placeholder="请选择" :required="true" @change="onChangeSearchChannel">
          <el-option
                  v-for="item in methodOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-switch
                v-model="searchForm.status"
                inactive-value="STOP"
                active-value="ENABLE">
        </el-switch>
      </el-form-item>
      <el-form-item class="search-inner-btn-box">
        <el-button size="small" type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="onCancelSubmit">重置</el-button>
      </el-form-item>
    </el-form>

    <ul slot="hint-msg-box">
      <li>`接口名称+接口类型`必须唯一</li>
      <li>切换表格`状态`开关，可以实时修改记录状态</li>
    </ul>

    <el-table
            ref="table"
            :data="page.records"
            stripe
            :height="listHeight"
            border
            @selection-change="handleSelectionChange"
            style="width: 100%">
      <el-table-column
              type="selection"
              :width="tableSelectionWidth">
      </el-table-column>
      <el-table-column
              prop="name"
              label="接口名称">
      </el-table-column>
      <el-table-column
              prop="url"
              label="接口地址">
      </el-table-column>
      <el-table-column
              label="接口类型"
              align="center"
              :width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.method === 'POST'">新增</el-tag>
          <el-tag v-else-if="scope.row.method === 'GET'" type="success">查询</el-tag>
          <el-tag v-else-if="scope.row.method === 'PUT'" type="warning">修改</el-tag>
          <el-tag v-else-if="scope.row.method === 'DELETE'" type="danger">删除</el-tag>
        </template>
      </el-table-column>
      <el-table-column
              label="接口状态"
              align="center"
              :width="80">
        <template slot-scope="scope">
          <el-switch v-if="scope.row.id !== '0'" v-model="scope.row.status" inactive-value="STOP" active-value="ENABLE" @change="onTableItemStatusChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column
              prop="description"
              label="接口描述">
      </el-table-column>
    </el-table>

    <el-form slot="form" :model="form" :rules="rules" ref="form" @submit.native.prevent>
      <div class="dialog-form-submit-container">
        <d2-el-form-item label="接口名称" :required="true" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </d2-el-form-item>
        <d2-el-form-item label="接口状态" >
          <!--！注意label需要类型匹配-->
          <el-switch v-model="form.status" inactive-value="STOP" active-value="ENABLE"></el-switch>
        </d2-el-form-item>
        <d2-el-form-item label="接口地址" >
          <el-input v-model="form.url" autocomplete="off"></el-input>
        </d2-el-form-item>
        <d2-el-form-item label="接口类型" >
          <el-select v-model="form.method" filterable placeholder="请选择">
            <el-option
                    v-for="item in methodOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
            </el-option>
          </el-select>
        </d2-el-form-item>
        <d2-el-form-item label="接口描述" prop="description">
          <el-input
                  type="textarea"
                  :rows="2"
                  placeholder="请输入内容"
                  v-model="form.description">
          </el-input>
        </d2-el-form-item>

        <div class="dialog-form-submit-inner-container">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" native-type="submit" @click="onSubmitForm">确 定</el-button>
        </div>
      </div>
    </el-form>
  </d2-mng-page>
</template>

<script>
import _ from 'lodash'
import { mapState } from 'vuex'
export default {
  name: 'mngauth-interface',
  data () {
    return {
      filename: __filename,
      methodOptions: [
        {
          value: 'GET',
          label: '查询'
        },
        {
          value: 'POST',
          label: '新增'
        },
        {
          value: 'PUT',
          label: '更新'
        },
        {
          value: 'DELETE',
          label: '删除'
        }
      ],
      rolesOptions: [],
      formMode: 'add',
      dialogFormVisible: false,
      dialogModifyPwdFormVisible: false,
      rules: {
        name: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        description: [
          { min: 5, max: 30, message: '长度在 5 到 30 个字符', trigger: 'blur' }
        ]
      },
      chooseRoles: [],
      form: {
        name: '',
        method: '',
        url: '',
        description: '',
        status: 'ENABLE'
      },
      formTmpl: {},
      selectRows: [],
      page: {
        records: [],
        total: 0,
        size: this.$store.state.d2admin.page.defSize,
        current: this.$store.state.d2admin.page.defCurrent
      },
      channel: this.$store.state.d2admin.page.defChannel,
      channelOptions: this.$store.state.d2admin.page.channelOptions,
      searchRules: {
        channel: [
          { required: true, message: '请选择渠道', trigger: 'change' }
        ]
      },
      searchForm: {
        channel: this.$store.state.d2admin.page.defChannel,
        name: '',
        method: '',
        status: 'ENABLE'
      }
    }
  },
  computed: {
    ...mapState('d2admin', {
      listHeight: state => state.page.listHeight,
      tableSelectionWidth: state => state.page.tableSelectionWidth
    })
  },
  methods: {
    onTableItemStatusChange(item) {
      this.$vp.ajaxPut('interface', {
        params: item
      }).then(res => {
        this.$vp.toast('修改成功', { type: 'success' })
      });
    },
    _loadRecords(item, callback) {
      this.$vp.ajaxGet(`interface/${item.id}`)
        .then(res => {
          this._copy(item, res)
          this.form = _.clone(item)
          callback()
        })
    },
    qryData() {
      this.$vp.ajaxGet(`interface/${this.channel}/${this.page.current}/${this.page.size}`).then(res => { this.page = res })
    },
    // 因为不能将一个普通json对象直接覆盖vue的响应式对象，故做此函数
    _copy(current, orig) {
      current.channel = orig.channel
      current.name = orig.name
      current.url = orig.url
      current.method = orig.method
      current.desc = orig.desc
      current.status = orig.status
    },
    handleSelectionChange(rows) {
      this.selectRows = rows
    },
    onChangeSearchChannel(idx) {
      this.channel = this.channelOptions[idx].value
      this.qryData()
    },
    onSearch() {
      this.$refs.ruleSearchForm.validate((valid) => {
        if (valid) {
          this.$vp.ajaxPostJson(`interface/search/${this.channel}/${this.page.current}/${this.page.size}`, { params: this.searchForm }).then(res => { this.page = res })
        }
      });
    },
    onCancelSubmit() {
      this.searchForm = _.clone({
        channel: this.channel,
        status: 'ENABLE'
      })
      this.qryData()
    },
    onCreate() {
      this.form = _.clone(this.formTmpl)
    },
    onUpdate(item) {
      this._loadRecords(item, () => { this.dialogFormVisible = true })
    },
    onDel() {
      const records = _.clone(this.selectRows)
      const ids = []
      records.forEach(item => {
        ids.push(item.id)
      })
      this.$vp.ajaxDel(`interface/dels/${ids}`).then(res => {
        this.qryData();
        this.selectRows = []
      })
    },
    _submitFinally() {
      this.form = _.clone(this.formTmpl)
      this.dialogFormVisible = false
    },
    onSubmitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const params = _.clone(this.form);
          if (this.formMode === 'add') {
            params.channel = this.channel
            this.$vp.ajaxPostJson('interface', {
              params
            }).then(res => {
              this.qryData()
            }).finally(() => {
              this._submitFinally()
            });
          } else {
            this.$vp.ajaxPut('interface', {
              params
            }).then(res => {
              this.qryData()
            }).finally(() => {
              this._submitFinally()
            });
          }
        }
      })
    }
  },
  created() {
    this.formTmpl = _.clone(this.form)
    this.onCancelSubmit()
  }
}
</script>
