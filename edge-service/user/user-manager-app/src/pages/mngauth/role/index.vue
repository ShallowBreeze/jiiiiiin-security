<template>
  <d2-mng-page
          :filename="filename"
          @qry-data="qryData"
          @create="onCreate"
          :create-access-rule="{url: 'role', method: 'POST'}"
          @update="onUpdate"
          update-access-rule="MNG_ROLE_UPDATE"
          @del="onDel"
          :del-access-rule="[{url: 'role', method: 'DELETE'}]"
          :page="page"
          :form-mode.sync="formMode"
          :dialog-form-visible.sync="dialogFormVisible"
          :select-rows="selectRows"
          :show-option-box="true">
    <el-form v-access="{url: 'role/search/eleui/*/*/*', method: 'GET'}" slot="search-inner-box" :inline="true" :model="searchForm" :rules="searchRules" ref="ruleSearchForm" class="demo-form-inline">
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
      <el-form-item label="标识">
        <el-input
                size="small"
                placeholder="请输入标识"
                v-model="searchForm.authorityName">
        </el-input>
      </el-form-item>
      <el-form-item class="search-inner-btn-box">
        <el-button size="small" type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="onCancelSubmit">重置</el-button>
      </el-form-item>
    </el-form>

    <ul slot="hint-msg-box">
      <li>`角色名称和角色标识`必须唯一</li>
      <li>不能创建和系统管理员角色相同角色标识的记录</li>
      <li>系统管理员角色不允许修改</li>
    </ul>
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
            <el-form-item label="资源授权">
              <span v-show="props.row.authorityName === 'ADMIN'" style="font-style: italic; color: darkred">注：系统管理员默认具有所有资源授权</span>
              <el-tree
                      ref="tableTree"
                      :data="resources"
                      :props="treeProps"
                      :check-strictly="true"
                      node-key="id"
                      :default-expanded-keys="props.row.expandedKeys"
                      :default-checked-keys="props.row.checkedKeys"
                      @check-change="handleTableRowCheckChange"
                      :show-checkbox="props.row.authorityName !== 'ADMIN'"></el-tree>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
              type="selection"
              :width="tableSelectionWidth">
      </el-table-column>
      <el-table-column
              prop="name"
              label="角色名称">
      </el-table-column>
      <el-table-column
              prop="authorityName"
              label="角色标识">
      </el-table-column>
    </el-table>

    <el-form slot="form" :model="form" :rules="rules" ref="form" @submit.native.prevent>
      <div class="dialog-form-submit-container">

        <d2-el-form-item label="角色名称" :required="true" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </d2-el-form-item>

        <d2-el-form-item label="角色标识" :required="true" prop="authorityName">
          <el-input v-model="form.authorityName" autocomplete="off"></el-input>
        </d2-el-form-item>

        <d2-el-form-item label="资源授权">
          <el-tree
                  ref="formTree"
                  :data="resources"
                  :props="treeProps"
                  :check-strictly="true"
                  :default-expand-all="true"
                  node-key="id"
                  :default-expanded-keys="form.expandedKeys"
                  :default-checked-keys="form.checkedKeys"
                  @check-change="handleCheckChange"
                  show-checkbox style="margin-top: 10px"></el-tree>
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
import mngPageMixin from '@/mixin/mng-page-mixin'

const ADMIN_AUTHORITY_NAME = 'ADMIN'

export default {
  name: 'mngauth-role',
  mixins: [
    mngPageMixin
  ],
  data () {
    return {
      filename: __filename,
      resources: [],
      treeProps: {
        children: 'children',
        label: 'name'
      },
      formMode: 'add',
      dialogFormVisible: false,
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        authorityName: [
          { required: true, message: '请输入角色标识', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ]
      },
      form: {
        id: '',
        name: '',
        authorityName: '',
        resources: [],
        // http://element.eleme.io/#/zh-CN/component/tree#mo-ren-zhan-kai-he-mo-ren-xuan-zhong
        expandedKeys: [],
        checkedKeys: []
      },
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
        authorityName: 'ADMIN'
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
    /**
     * @param data 传递给 data 属性的数组中该节点所对应的对象
     * @param checked 节点本身是否被选中
     * @param indeterminate 节点的子树中是否有被选中的节点
     */
    handleCheckChange(data, checked, indeterminate) {
      const { id, pids, name } = data;
      if (checked && !_.isObject(_.find(this.form.resources, item => { return item.id === id; })) && !_.isObject(_.find(this.form.checkedKeys, item => { return item.id === id; }))) {
        // 检测并选中data的父节点
        if (!_.isNil(pids) && pids !== '0') {
          const tempId = pids.split(',').reverse()[0];
          if (!_.isObject(_.find(this.form.checkedKeys, item => { return item.id === tempId; }))) {
            this.form.checkedKeys.push(tempId);
          }
        }
        this.form.checkedKeys.push(id);
        this.$refs.formTree.setCheckedKeys(this.form.checkedKeys);
        // 重新设置，不然会存在children
        this.form.resources.push({ id, pids, name });
      } else if (!checked) {
        // 检测如果节点下面还勾选了子节点则不允许取消
        const id = data.id;
        let temp = false;
        this.form.resources.forEach(item => {
          if (!_.isNil(item.pids) && item.pids.indexOf(id) !== -1) {
            temp = true;
          }
        });
        if (temp) {
          // 初次点击更新时防止下面内容弹出
          if (!checked) {
            this.$vp.toast('当前节点下还有被选中的子节点，不允许取消', { type: 'warning' });
            this.$refs.formTree.setCheckedKeys(this.form.checkedKeys);
          }
        } else {
          _.remove(this.form.resources, item => {
            return item.id === id;
          });
          _.remove(this.form.checkedKeys, item => {
            return item === id;
          });
        }
      }
    },
    _updateRoleRelationResourceRecords() {
      const params = _.clone(this.tableTreeForm);
      this.$vp.ajaxPut('role', {
        params
      }).then(res => {
        this.$vp.toast('授权修改成功', { type: 'success' });
      })
    },
    // 如果两棵树的代码进行了抽象就很容易出错，还有一点，如果不需要和模板进行绑定的值，可以不需要定义在data里面减少开支
    handleTableRowCheckChange(data, checked, indeterminate) {
      // 首次展开会通知，data为根节点，这时不做处理
      if (data.id !== '0') {
        const { id, pids, name } = data;
        if (checked && !_.isObject(_.find(this.tableTreeForm.resources, item => { return item.id === id; })) && !_.isObject(_.find(this.tableTreeForm.checkedKeys, item => { return item.id === id; }))) {
          // 检测并选中data的父节点
          if (!_.isNil(pids) && pids !== '0') {
            const tempId = pids.split(',').reverse()[0];
            if (!_.isObject(_.find(this.tableTreeForm.checkedKeys, item => { return item.id === tempId; }))) {
              this.tableTreeForm.checkedKeys.push(tempId);
            }
          }
          this.tableTreeForm.checkedKeys.push(id);
          this.$refs.tableTree.setCheckedKeys(this.tableTreeForm.checkedKeys);
          // 重新设置，不然会存在children
          this.tableTreeForm.resources.push({ id, pids, name });
          this._updateRoleRelationResourceRecords()
        } else if (!checked) {
          // 检测如果节点下面还勾选了子节点则不允许取消
          const id = data.id;
          let temp = false;
          this.tableTreeForm.resources.forEach(item => {
            if (!_.isNil(item.pids) && item.pids.indexOf(id) !== -1) {
              temp = true;
            }
          });
          if (temp) {
            // 初次点击更新时防止下面内容弹出
            if (!checked) {
              this.$vp.toast('当前节点下还有被选中的子节点，不允许取消', { type: 'warning' });
              this.$refs.tableTree.setCheckedKeys(this.tableTreeForm.checkedKeys);
            }
          } else {
            _.remove(this.tableTreeForm.resources, item => {
              return item.id === id;
            });
            _.remove(this.tableTreeForm.checkedKeys, item => {
              return item === id;
            });
            this._updateRoleRelationResourceRecords()
          }
        }
      }
    },
    qryData() {
      this.$vp.ajaxGet(`resource/${this.channel}`).then(res => { this.resources = res })
      this.$vp.ajaxGet(`role/eleui/${this.channel}/${this.page.current}/${this.page.size}`).then(res => { this.page = res })
      this.setInitAjaxNum(2)
    },
    handleExpandChangge(row, expandedRows) {
      if (!_.isNil(this.prevExpandedRow) && this.prevExpandedRow.id !== row.id) {
        // 防止等下面ajax更新`this._copyRoleDto(row, res[1].data)`导致表单中的tree触发`check-change`事件导致问题
        this.resources = []
        // 收缩上一次展开的角色对应的资源树，防止共用`this.form`到时的问题
        this.$refs.table.toggleRowExpansion(this.prevExpandedRow, false)
      }
      this.prevExpandedRow = row
      this.$vp.ajaxAll([
        {
          url: `resource/${this.channel}`,
          mode: 'GET'
        }, {
          url: `role/${row.id}`,
          mode: 'GET'
        }
      ])
        .then(resArr => {
          // 这里需要应用手动把axios的data属性解析掉
          const res = _.map(resArr, (item) => {
            return item.data;
          })
          // 设置`表单 资源树`
          this.resources = res[0].data
          // 更新记录，主要是expandedKeys和checkedKeys
          this._copyRoleDto(row, res[1].data)
          this.tableTreeForm = _.clone(row)
        })
    },
    _copyRoleDto(current, orig) {
      current.name = orig.name
      current.authorityName = orig.authorityName
      current.channel = orig.channel
      current.resources = orig.resources
      current.expandedKeys = orig.expandedKeys
      current.checkedKeys = orig.checkedKeys
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
          const params = this.searchForm
          this.$vp.ajaxPostJson(`role/search/eleui/${this.channel}/${this.page.current}/${this.page.size}`, { params }).then(res => { this.page = res })
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
    _checkAdminRole(item) {
      if (item.authorityName === ADMIN_AUTHORITY_NAME) {
        this.$vp.toast('系统管理员不允许修改', { type: 'error' })
        return true
      }
    },
    onUpdate(item) {
      if (!this._checkAdminRole(item)) {
        this.$vp.ajaxAll([
          {
            url: `resource/${this.channel}`,
            mode: 'GET'
          }, {
            url: `role/${item.id}`,
            mode: 'GET'
          }
        ])
          .then(resArr => {
            // 这里需要应用手动把axios的data属性解析掉
            const res = _.map(resArr, (item) => {
              return item.data;
            })
            // 设置`表单 资源树`
            this.resources = res[0].data
            this.form = res[1].data
            this.dialogFormVisible = true
          })
      }
    },
    onDel() {
      const roles = _.clone(this.selectRows)
      if (_.filter(roles, item => item.authorityName === ADMIN_AUTHORITY_NAME).length !== 1) {
        this.$vp.ajaxDel(`role`, {
          params: {
            roles
          }
        }).then(res => {
          this.qryData();
          this.selectRows = [];
        });
      } else {
        this.$vp.toast('系统管理员不允许删除', { type: 'error' });
      }
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
            this.$vp.ajaxPostJson('role', {
              params
            }).then(res => {
              this.qryData()
            }).finally(() => {
              this._submitFinally()
            });
          } else {
            this.$vp.ajaxPut('role', {
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
    this.formTmpl = {
      id: '',
      name: '',
      authorityName: '',
      resources: [],
      expandedKeys: [],
      checkedKeys: []
    };
    this.onCancelSubmit()
  }
}
</script>
