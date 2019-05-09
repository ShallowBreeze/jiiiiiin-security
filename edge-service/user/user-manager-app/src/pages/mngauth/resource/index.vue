<template>
    <d2-container :filename="filename">
        <el-row>
            <el-col :span="18" v-access="{url: 'resource/search/*/*', method: 'GET'}">
                <el-form :inline="true" :model="searchForm" :rules="searchRules" ref="ruleSearchForm" class="demo-form-inline">
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
                    <el-form-item label="状态">
                        <el-switch
                                v-model="searchForm.status"
                                inactive-value="STOP"
                                active-value="ENABLE">
                        </el-switch>
                    </el-form-item>
                    <el-form-item class="search-inner-btn-box">
                        <el-button size="small" type="primary" icon="el-icon-search" @click="onSearch" >查询</el-button>
                        <el-button size="small" icon="el-icon-refresh" @click="onCancelSubmit">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
            <el-col :span="5">
                <div style="margin-top: 10px;">
                    <el-switch
                            v-model="isFold"
                            inactive-text="关闭资源树"
                            active-text="展开资源树"
                            :inactive-value="false"
                            :active-value="true">
                    </el-switch>
                </div>
            </el-col>
            <el-col :span="1">
                <el-popover
                        placement="top-start"
                        title="温馨提示"
                        width="400"
                        trigger="hover">
                    <ul>
                        <li>资源名称必填</li>
                        <li>资源名称或资源别名再同一渠道必须唯一</li>
                        <li>只可以删除叶子节点</li>
                        <li>点击`重置按钮`可以恢复列表初始化状态</li>
                        <li>直接点击记录的`状态切换开关`可以直接修改节点状态</li>
                    </ul>
                    <el-button slot="reference" size="small" icon="el-icon-info" class="mng-list-title-hint-btn">操作提示</el-button>
                </el-popover>
            </el-col>
        </el-row>

        <zk-table
                ref="table"
                sum-text="sum"
                index-text="#"
                :data="data"
                empty-text="暂无数据"
                :columns="columns"
                :stripe="true"
                :border="true"
                :show-index="false"
                :tree-type="true"
                :is-fold="!isFold"
                :expand-type="false"
                :selection-type="false">
            <!--展开存在bug-->
            <!--[Vue warn]: Duplicate keys detected: '0'. This may cause an update error.-->
            <!--以上警告是树形插件本身爆出来的，待解决-->
            <!--<template slot="$expand" slot-scope="scope">-->
                <!--{{scope.row.name}}-->
            <!--</template>-->
            <template slot="icon" slot-scope="scope">
                <d2-icon v-if="scope.row.icon" :name="scope.row.icon"/>
                <span v-else>空</span>
            </template>
            <template slot="type" slot-scope="scope">
                <el-tag v-if="scope.row.id !== '0'" size="mini" type="[scope.row.type ? '': 'success']"> {{ scope.row.type ? '菜单' : '按钮' }}</el-tag>
            </template>
            <template slot="status" slot-scope="scope">
                <el-switch v-if="scope.row.id !== '0'" v-model="scope.row.status" inactive-value="STOP" active-value="ENABLE" @change="onTableItemStatusChange(scope.row)"></el-switch>
                <!--<el-tag v-else size="mini" type="[scope.row.status === 1 ? '': 'success']"> {{ scope.row.status === 'ENABLE' ? '启用' : '停用' }}</el-tag>-->
            </template>
            <template slot="option" slot-scope="scope">
                <el-button v-access="{url: 'resource/qry/*', method: 'GET'}" type="primary" plain size="mini" v-if="scope.row.id !== '0'" @click="onClickQryRelationInterfaceRecords(scope.row)">查看关联接口记录</el-button>
                <el-button v-access="{url: 'resource', method: 'POST'}" type="primary" plain size="mini" @click="onClickAdd(scope.row)">新增</el-button>
                <el-button v-access="{url: 'resource', method: 'UPDATE'}" plain size="mini" @click="onClickUpdate(scope.row)" v-if="scope.row.id !== '0'">修改</el-button>
                <!--根节点才可以删除-->
                <el-button v-access="{url: 'resource', method: 'DELETE'}" type="danger" plain size="mini" v-if="scope.row.id !== '0' && (!scope.row.children || (scope.row.children && scope.row.children.length === 0))" @click.stop.prevent="onClickDel(scope.row, $event)">删除</el-button>
            </template>
        </zk-table>

        <el-dialog
                title="查看关联接口记录"
                :visible.sync="dialogQryRelationInterfaceRecordsVisible"
                width="70%"
                :modal="true">
            <el-row>
                <el-col :span="8">
                    <el-form :model="form" :rules="rules" ref="form" @submit.native.prevent>
                        <d2-el-form-item label="类型" label-width="100px">
                            <el-radio-group v-model="form.type" @change="onChangeType" :disabled="true">
                                <el-radio label="MENU">菜单</el-radio>
                                <el-radio label="BTN">按钮</el-radio>
                            </el-radio-group>
                        </d2-el-form-item>
                        <d2-el-form-item label="名称" :required="true" prop="name" label-width="100px">
                            <el-input v-model="form.name" autocomplete="off" disabled="disabled"></el-input>
                        </d2-el-form-item>
                        <d2-el-form-item label="别名" label-width="100px">
                            <el-input v-model="form.alias" autocomplete="off" disabled="disabled"></el-input>
                        </d2-el-form-item>
                        <d2-el-form-item label="父级菜单" label-width="100px">
                            <el-input v-model="form.pname" autocomplete="off" disabled="disabled"></el-input>
                        </d2-el-form-item>
                        <d2-el-form-item label="状态" label-width="100px">
                            <el-switch v-model="form.status" inactive-value="STOP" active-value="ENABLE" disabled="disabled"></el-switch>
                        </d2-el-form-item>
                        <d2-el-form-item label="路由" label-width="100px">
                            <el-input v-model="form.path" autocomplete="off" disabled="disabled"></el-input>
                        </d2-el-form-item>
                        <d2-el-form-item label="同级排序" label-width="100px">
                            <el-input-number v-model="form.num" label="排序" disabled="disabled"></el-input-number>
                        </d2-el-form-item>
                        <d2-el-form-item label="图标" v-show="visibleMenuFromField" label-width="100px">
                            <el-select v-model="form.icon" filterable placeholder="请选择" disabled="disabled">
                                <el-option
                                        v-for="item in iconOptions"
                                        :key="item.key"
                                        :value="item.value"
                                        :disabled="item.disabled">
                                    <div v-if="item.disabled">
                                        <span style="float: left">{{ item.label }}</span>
                                    </div>
                                    <div v-else>
                                        <d2-icon :name="item.value" style="width: 30px"/>&nbsp;&nbsp;&nbsp;&nbsp;{{
                                        item.value }}
                                    </div>
                                </el-option>
                            </el-select>
                        </d2-el-form-item>
                    </el-form>
                </el-col>
                <el-col :span="15" style="float: right">
                    <label class="el-form-item__label" style="width: 100px;">关联接口记录:</label>
                    <el-table
                            ref="table"
                            :data="interfacesData"
                            stripe
                            border
                            style="width: 100%">
                        <el-table-column
                                prop="name"
                                :min-width="120"
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
                                <el-switch v-if="scope.row.id !== '0'" v-model="scope.row.status" inactive-value="STOP" active-value="ENABLE" disabled></el-switch>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-col>
            </el-row>
            <div class="dialog-form-submit-container">
                <div class="dialog-form-submit-inner-container">
                    <el-button type="danger" @click="dialogQryRelationInterfaceRecordsVisible = false">关闭</el-button>
                </div>
            </div>
        </el-dialog>

        <el-dialog
                :title="formMode === 'edit' ? '编辑' : '新增'"
                :visible.sync="dialogFormVisible"
                width="70%"
                :modal="true">
            <el-form :model="form" :rules="rules" ref="form" @submit.native.prevent>
                <d2-el-form-item label="类型" label-width="100px">
                    <el-radio-group v-model="form.type" @change="onChangeType" :disabled="formTypeRadioStatus">
                        <el-radio label="MENU">菜单</el-radio>
                        <el-radio label="BTN">按钮</el-radio>
                    </el-radio-group>
                </d2-el-form-item>
                <d2-el-form-item label="名称" :required="true" prop="name" label-width="100px">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                </d2-el-form-item>
                <d2-el-form-item label="别名" label-width="100px">
                    <el-input v-model="form.alias" autocomplete="off"></el-input>
                </d2-el-form-item>
                <d2-el-form-item label="父级菜单" label-width="100px">
                    <el-input v-model="form.pname" autocomplete="off" disabled="disabled"></el-input>
                </d2-el-form-item>
                <d2-el-form-item label="状态" label-width="100px">
                    <!--！注意label需要类型匹配-->
                    <el-switch v-model="form.status" inactive-value="STOP" active-value="ENABLE"></el-switch>
                </d2-el-form-item>
                <d2-el-form-item label="路由" label-width="100px">
                    <el-input v-model="form.path" autocomplete="off"></el-input>
                </d2-el-form-item>
                <d2-el-form-item label="接口记录管理" label-width="100px">
                    <el-transfer
                            class="mng-resource-dialog-transfer"
                            :titles="['未关联接口列表', '已关联接口列表']"
                            filterable
                            filter-placeholder="请输入接口地址"
                            v-model="selectInterfaces"
                            :data="interfacesData">
                    </el-transfer>
                </d2-el-form-item>
                <d2-el-form-item label="同级排序" label-width="100px">
                    <el-input-number v-model="form.num" :min="1" :max="numMax" label="排序"></el-input-number>
                </d2-el-form-item>
                <d2-el-form-item label="图标"  v-show="visibleMenuFromField" label-width="100px">
                    <el-select v-model="form.icon" filterable placeholder="请选择">
                        <el-option
                                v-for="item in iconOptions"
                                :key="item.key"
                                :value="item.value"
                                :disabled="item.disabled">
                            <div v-if="item.disabled">
                                <span style="float: left">{{ item.label }}</span>
                            </div>
                            <div v-else>
                                <d2-icon :name="item.value" style="width: 30px"/>&nbsp;&nbsp;&nbsp;&nbsp;{{ item.value }}
                            </div>
                        </el-option>
                    </el-select>
                </d2-el-form-item>
                <div class="dialog-form-submit-container">
                    <div class="dialog-form-submit-inner-container">
                        <el-button @click="dialogFormVisible = false">取 消</el-button>
                        <el-button type="primary" native-type="submit" @click="onSubmitForm">确 定</el-button>
                    </div>
                </div>
            </el-form>
        </el-dialog>
    </d2-container>
</template>

<script>
import icon from './data/index'
import _ from 'lodash'

export default {
  name: 'mngauth-resource',
  data() {
    return {
      filename: __filename,
      isFold: true,
      dialogQryRelationInterfaceRecordsVisible: false,
      // 关联的接口记录集合
      interfacesData: [],
      selectInterfaces: [],
      icon,
      channel: this.$store.state.d2admin.page.defChannel,
      channelOptions: this.$store.state.d2admin.page.channelOptions,
      visibleMenuFromField: true,
      dialogFormVisible: false,
      formMode: 'add',
      formTypeRadioStatus: true,
      numMax: 1,
      selectNode: {},
      searchRules: {
        channel: [
          { required: true, message: '请选择渠道', trigger: 'change' }
        ]
      },
      searchForm: {
        channel: this.$store.state.d2admin.page.defChannel,
        status: 'ENABLE'
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ]
      },
      form: {
        channel: '',
        icon: '',
        id: -1,
        levels: 1,
        name: '',
        alias: '',
        num: 1,
        path: '',
        pid: 0,
        pname: '',
        status: 'ENABLE',
        type: 'MENU',
        interfacceIds: []
      },
      data: [],
      columns: [
        {
          label: '名称',
          prop: 'name',
          minWidth: '100px'
        },
        {
          label: '别名',
          prop: 'alias',
          minWidth: '100px'
        },
        {
          label: '图标',
          prop: 'icon',
          type: 'template',
          template: 'icon',
          width: '50px',
          align: 'center'
        },
        {
          label: '类型',
          prop: 'type',
          type: 'template',
          template: 'type',
          width: '70px',
          align: 'center'
        },
        {
          label: '排序',
          prop: 'num',
          width: '50px',
          align: 'center'
        },
        {
          label: '路由',
          prop: 'path'
        },
        {
          label: '状态',
          prop: 'status',
          type: 'template',
          template: 'status',
          width: '70px',
          align: 'center'
        },
        {
          label: '操作',
          width: '415px',
          type: 'template',
          template: 'option'
        }
      ]
    };
  },
  computed: {
    iconOptions () {
      const res = [];
      let idx = 0;
      this.icon.forEach(item => {
        res.push({
          label: item.title,
          disabled: true
        });
        item.icon.forEach((icon) => {
          idx += 1;
          res.push({
            key: idx,
            label: icon,
            value: icon
          })
        })
      });
      return res
    }
  },
  methods: {
    onClickQryRelationInterfaceRecords(row) {
      this.$vp.ajaxGet(`resource/qry/${row.id}`)
        .then(res => {
          if (_.isNil(res.interfaces) || res.interfaces.length === 0) {
            this.$vp.toast('无关联记录', { type: 'warning' });
            return;
          }
          this.interfacesData = res.interfaces
          this._copy(row, res)
          this.form = res;
          const pnode = this._findParentNode(row, this.data);
          this.form.pname = pnode.name;
          this.dialogQryRelationInterfaceRecordsVisible = true
        })
    },
    // 只有更新node才不会为`undefined`
    generateInterfacesData() {
      this.interfacesData = []
      this.$vp.ajaxGet(`interface/list/${this.channel}`).then(res => {
        res.forEach((item, index) => {
          this.interfacesData.push({
            key: item.id,
            label: `${item.name} - ${item.url}`,
            disabled: item.status !== 'ENABLE',
            id: item.id
          })
        })
      })
    },
    onChangeSearchChannel(idx) {
      this.channel = this.channelOptions[idx].value
    },
    onSearch() {
      this.$refs.ruleSearchForm.validate((valid) => {
        if (valid) {
          this.$vp.ajaxGet(`resource/search/${this.searchForm.channel}/${this.searchForm.status}`).then(res => { this.data = res })
        }
      });
    },
    onCancelSubmit() {
      this.searchForm = _.clone({
        channel: '0',
        status: 'ENABLE'
      });
      this.$vp.ajaxGet(`resource/${this.channel}`).then(res => { this.data = res })
    },
    onTableItemStatusChange(node) {
      this.$vp.ajaxPut('resource', {
        params: node
      }).then(res => {
        this.$vp.toast('修改成功', { type: 'success' })
      });
    },
    // 查找对应`item`节点对应的父节点等信息
    _findParentNode(item, treeData, pnode, callback) {
      const size = treeData.length;
      for (let i = 0; i < size; i++) {
        const node = treeData[i];
        if (node.id === item.id) {
          if (_.isFunction(callback)) {
            this::callback(pnode, treeData, i, node)
          }
          return pnode
        } else {
          if (!_.isEmpty(node.children)) {
            const temp = this._findParentNode(item, node.children, node, callback);
            if (!_.isEmpty(temp) || size === i) {
              return temp
            }
          }
        }
      }
    },
    _findNode(id, treeData, pnode, callback) {
      const size = treeData.length;
      for (let i = 0; i < size; i++) {
        const node = treeData[i];
        if (node.id === id) {
          if (_.isFunction(callback)) {
            this::callback(node, pnode, treeData, i)
          }
          return pnode
        } else {
          if (!_.isEmpty(node.children)) {
            const temp = this._findNode(id, node.children, node, callback);
            if (!_.isEmpty(temp) || size === i) {
              return temp
            }
          }
        }
      }
    },
    _preHandlerAddOrUpdate(mode, currentSelectNode) {
      this.formMode = mode;
      // 新增currentSelectNode是父节点、更新currentSelectNode是当前节点
      this.selectNode = currentSelectNode;
      // 业务：更新不可以调整资源的`type`
      this.formTypeRadioStatus = (mode !== 'add');
      this.generateInterfacesData()
      this.dialogFormVisible = true
    },
    onClickAdd(node) {
      this.form = _.clone(this.formTempl);
      this.form.channel = node.channel;
      this.form.pid = node.id;
      this.form.pname = node.name;
      this.form.levels = node.levels + 1;
      this.numMax = _.isEmpty(node.children) ? 1 : node.children.length + 1;
      this.form.num = this.numMax;
      this.selectInterfaces = []
      this._preHandlerAddOrUpdate('add', node)
    },
    onClickUpdate(node) {
      this.$vp.ajaxGet(`resource/qry/${node.id}`)
        .then(res => {
          if (!_.isNil(res.interfaces) || res.interfaces.length !== 0) {
            this.selectInterfaces = []
            res.interfaces.forEach(item => {
              this.selectInterfaces.push(item.id)
            })
          }
          this._copy(node, res);
          const pnode = this._findParentNode(node, this.data);
          this.form = res;
          this.form.pname = pnode.name;
          this.numMax = _.isEmpty(pnode.children) ? 1 : pnode.children.length + 1;
          this._preHandlerAddOrUpdate('edit', node);
        });
    },
    onChangeType(value) {
      this.visibleMenuFromField = value === 'MENU'
    },
    _onAddSuccessUpdateTreeData(res) {
      this._findNode(this.selectNode.id, this.data, null, (node, pnode, treeData, i) => {
        const addNodeNum = res.num;
        if (!_.isEmpty(node.children)) {
          const size = node.children.length;
          if (size > 1 && addNodeNum <= size) {
            // 需要检查父节点下面的子节点的排序
            let needUpdate = false;
            node.children.forEach((item, idx) => {
              const itemNum = item.num;
              if (itemNum === addNodeNum) {
                needUpdate = true;
              }
              if (needUpdate) {
                node.children[idx].num = itemNum + 1;
              }
            });
          }
        } else {
          this.$set(node, 'children', [])
        }
        node.children.splice(addNodeNum - 1, 0, _.clone(res))
      })
    },
    _copy(current, orig) {
      current.name = orig.name;
      current.alias = orig.alias;
      current.icon = orig.icon;
      current.num = orig.num;
      current.path = orig.path;
      current.status = orig.status;
    },
    _updateParentChildrenNode(node) {
      this._findParentNode(node, this.data, null, (pnode, pnodeChildrenData, idx, node) => {
        this.$vp.ajaxGet(`resource/${this.channel}/${pnode.id}`)
          .then(res => {
            pnodeChildrenData.forEach((item, idx) => {
              this._copy(item, res[idx]);
            });
          });
      });
    },
    // 业务逻辑校验
    _businessValid(params) {
      let res = true
      const { type, path } = params;
      if (type === 'MENU' && _.isEmpty(path)) {
        this.$vp.toast('菜单资源必须填写前端路由页面路径', { type: 'warning' })
        res = false;
      }
      return res
    },
    onSubmitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this._businessValid(this.form)) {
            let params = _.clone(this.form);
            delete params.pname;
            params.interfacesIds = this.selectInterfaces;
            if (this.formMode === 'add') {
              delete params.id;
              this.$vp.ajaxPostJson('resource', {
                params
              }).then(res => {
                this._onAddSuccessUpdateTreeData(res);
              });
            } else {
              this.$vp.ajaxPut('resource', {
                params
              }).then(res => {
                // 修改当前节点信息
                this._updateParentChildrenNode(params);
              });
            }
            this.dialogFormVisible = false;
          }
        }
      });
    },
    /**
     * 查找`treeData`树下面`item`对应节点的父节点的`children`集合
     * @param item
     * @param treeData
     * @param callback
     * @returns {*}
     * @private
     */
    _findNodeParentChildren(item, treeData, callback) {
      const size = treeData.length;
      for (let i = 0; i < size; i++) {
        const node = treeData[i];
        if (node.id === item.id) {
          if (_.isFunction(callback)) {
            this::callback(treeData, i, node)
          }
          return treeData
        } else {
          if (_.isEmpty(node.children)) {

          } else {
            const temp = this._findNodeParentChildren(item, node.children, callback);
            if (!_.isEmpty(temp) || size === i) {
              return temp
            }
          }
        }
      }
    },
    onClickDel(item, event) {
      this.$vp.ajaxDel(`resource/${this.channel}/${item.id}`)
        .then(res => {
          if (res) {
            this._findNodeParentChildren(item, this.data, (pnodeChildrenData, idx) => {
              pnodeChildrenData.splice(idx, 1);
              // 查看父节点是否还有子节点，如果有需要进行`排序`字段修改，**在前台修改之前后台数据库已经被更新**
              if (_.isArray(pnodeChildrenData) && pnodeChildrenData.length > 0) {
                const delNum = item.num;
                const size = pnodeChildrenData.length;
                if (size > 0) {
                  for (let i = 0; i < size; i++) {
                    const node = pnodeChildrenData[i];
                    if (node.num > delNum) {
                      node.num = node.num - 1;
                    }
                  }
                }
              }
            });
            this.$vp.toast(`成功删除【${item.name}】`)
          }
        })
    }
  },
  created() {
    this.formTempl = {
      channel: '',
      icon: '',
      id: -1,
      levels: 1,
      name: '',
      alias: '',
      num: 1,
      path: '',
      pid: 0,
      pname: '',
      status: 'ENABLE',
      type: 'MENU'
    }
    this.$vp.ajaxGet(`resource/${this.channel}`).then(res => { this.data = res })
  }
};
</script>

<style lang="scss" scoped>
    .icon-card {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        height: 50px;

        &:hover {
            .icon {
                transform: scale(1.1);
            }

            .icon-title {
                color: $color-text-main;
            }
        }
    }

    .icon {
        height: 30px;
        width: 30px;
        transition: all .3s;
        cursor: pointer;
    }

    .icon-title {
        font-size: 12px;
        margin-top: 3px;
        color: $color-text-normal;
    }
</style>
