<template>
  <d2-mng-page
          :filename="filename"
          @qry-data="qryData"
          @create="onCreate"
          :create-access-rule="{url: 'admin', method: 'POST'}"
          @update="onUpdate"
          update-access-rule="MNG_ADMIN_UPDATE"
          @del="onDel"
          :del-access-rule="[{url: 'admin/dels/*', method: 'DELETE'}]"
          :page="page"
          :form-mode.sync="formMode"
          :dialog-form-visible.sync="dialogFormVisible"
          :select-rows="selectRows"
          :show-option-box="true">
    <el-form v-access:alias="'MNG_USERMNG'" slot="search-inner-box" :inline="true" :model="searchForm" :rules="searchRules" ref="ruleSearchForm" class="demo-form-inline">
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
      <el-form-item label="用户名">
        <el-input
                size="small"
                placeholder="请输入用户名"
                v-model="searchForm.username">
        </el-input>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input
                size="small"
                placeholder="请输入手机号"
                v-model="searchForm.phone">
        </el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input
                size="small"
                placeholder="请输入邮箱"
                v-model="searchForm.email">
        </el-input>
      </el-form-item>
      <el-form-item class="search-inner-btn-box">
        <el-button size="small" type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="onCancelSubmit">重置</el-button>
      </el-form-item>
    </el-form>

    <ul slot="hint-msg-box">
      <li>`用户名`必须唯一</li>
      <li>不能删除登录用户记录</li>
    </ul>
    <el-button v-access="{url: 'admin/pwd', method: 'UPDATE'}" slot="option-box-attch-but" size="small" @click="onClickModifyPwd">修改用户密码</el-button>

    <el-table
            ref="table"
            :data="page.records"
            stripe
            :height="listHeight"
            border
            @selection-change="handleSelectionChange"
            @expand-change="handleExpandChangge"
            style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="top" inline class="demo-table-expand">
            <el-form-item label="授权角色">
              <el-tag v-for="role in props.row.roles" v-bind:key="role.id" style="margin-right: 10px">{{role.name}}</el-tag>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
              type="selection"
              :width="tableSelectionWidth">
      </el-table-column>
      <el-table-column
              prop="username"
              label="用户名">
      </el-table-column>
      <el-table-column
              prop="phone"
              label="手机号">
      </el-table-column>
      <el-table-column
              prop="email"
              label="邮箱">
      </el-table-column>
      <el-table-column
              prop="createTimeStr"
              label="创建时间">
      </el-table-column>
    </el-table>

    <el-form slot="form" :model="form" :rules="rules" ref="form" @submit.native.prevent>
      <div class="dialog-form-submit-container">
        <d2-el-form-item label="用户名" :required="true" prop="username">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </d2-el-form-item>
        <d2-el-form-item label="密码" :required="true" prop="password" v-if="formMode === 'add'">
          <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
        </d2-el-form-item>
        <d2-el-form-item label="资源授权" :required="true" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择">
            <el-option
                    v-for="item in rolesOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
            </el-option>
          </el-select>
        </d2-el-form-item>
        <d2-el-form-item label="手机号">
          <el-input v-model="form.phone" autocomplete="off" type="tel"></el-input>
        </d2-el-form-item>

        <d2-el-form-item label="邮箱">
          <el-input v-model="form.email" autocomplete="off" type="email"></el-input>
        </d2-el-form-item>

        <div class="dialog-form-submit-inner-container">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" native-type="submit" @click="onSubmitForm">确 定</el-button>
        </div>
      </div>
    </el-form>

    <el-dialog
            title="修改密码"
            :visible.sync="dialogModifyPwdFormVisible"
            width="70%"
            :modal="true">
      <el-form :model="form" :rules="rulesModifyPwd" ref="modifyPwdForm" @submit.native.prevent>
        <div class="dialog-form-submit-container">

          <d2-el-form-item label="用户名" :required="true" prop="username">
            <el-input v-model="form.username" autocomplete="off" :disabled="true"></el-input>
          </d2-el-form-item>

          <d2-el-form-item label="密码" :required="true" prop="password">
            <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
          </d2-el-form-item>

          <div class="dialog-form-submit-inner-container">
            <el-button @click="dialogModifyPwdFormVisible = false">取 消</el-button>
            <el-button type="primary" native-type="submit" @click="onSubmitModifyPwdForm">确 定</el-button>
          </div>
        </div>
      </el-form>
    </el-dialog>
  </d2-mng-page>
</template>

<script>
import _ from 'lodash'
import { mapState } from 'vuex'
export default {
  name: 'mngauth-role',
  data () {
    return {
      filename: __filename,
      rolesOptions: [],
      formMode: 'add',
      dialogFormVisible: false,
      dialogModifyPwdFormVisible: false,
      rulesModifyPwd: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur' }
        ]
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur' }
        ],
        roleIds: [{ required: true, message: '请选择授权角色，可以多选', trigger: 'change' }]
      },
      chooseRoles: [],
      form: {
        username: '',
        password: '',
        phone: '',
        email: '',
        roleIds: []
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
        username: '',
        phone: '',
        email: ''
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
    onSubmitModifyPwdForm() {
      this.$refs.modifyPwdForm.validate((valid) => {
        if (valid) {
          const params = _.clone(this.form);
          delete params.roles;
          this.$vp.ajaxPut('admin/pwd', {
            params
          }).then(res => {
            this.qryData();
          }).finally(() => {
            this.dialogModifyPwdFormVisible = false
            this._submitFinally();
          });
        }
      });
    },
    _loadRecords(item, callback) {
      this.$vp.ajaxGet(`admin/${item.id}`)
        .then(res => {
          // 解析得到roleIds
          const roleIds = []
          res.roles.forEach(item => {
            roleIds.push(item.id)
          })
          res.roleIds = roleIds
          this._copy(item, res)
          this.form = _.clone(item)
          callback()
        })
    },
    onClickModifyPwd() {
      if (!_.isEmpty(this.selectRows) && this.selectRows.length === 1) {
        const item = this.selectRows[0]
        this._loadRecords(item, () => { this.dialogModifyPwdFormVisible = true })
      } else {
        this.$vp.toast('请选择一条需要编辑的记录', { type: 'warning' })
      }
    },
    qryData() {
      this.$vp.ajaxGet(`admin/${this.channel}/${this.page.current}/${this.page.size}`).then(res => { this.page = res })
      this.$vp.ajaxGet(`role/list/${this.channel}`).then(res => { this.rolesOptions = res })
    },
    handleExpandChangge(row, expandedRows) {
      this.$vp.ajaxGet(`admin/${row.id}`)
        .then(res => {
          // TODO 这里element-ui会报出，没有找到问题
          this._copy(row, res)
          this.form = _.clone(row)
        })
    },
    // 因为不能将一个普通json对象直接覆盖vue的响应式对象，故做此函数
    _copy(current, orig) {
      current.username = orig.username
      current.channel = orig.channel
      current.createTimeStr = orig.createTimeStr
      current.email = orig.email
      current.menus = orig.menus
      current.phone = orig.phone
      current.roleIds = orig.roleIds
      current.roles = orig.roles
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
          this.$vp.ajaxPostJson(`admin/search/${this.channel}/${this.page.current}/${this.page.size}`, { params: this.searchForm }).then(res => { this.page = res })
        }
      });
    },
    onCancelSubmit() {
      this.searchForm = _.clone({
        channel: this.channel
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
      this.$vp.ajaxDel(`admin/dels/${ids}`).then(res => {
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
          delete params.roles
          if (this.formMode === 'add') {
            params.channel = this.channel
            this.$vp.ajaxPostJson('admin', {
              params
            }).then(res => {
              this.qryData()
            }).finally(() => {
              this._submitFinally()
            });
          } else {
            // 普通修改不允许修改密码，受限于spring security使用的是单项加密，不能解密回显
            delete params.password
            this.$vp.ajaxPut('admin', {
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
    // 初始化列表
    this.onCancelSubmit()
  }
}
</script>
