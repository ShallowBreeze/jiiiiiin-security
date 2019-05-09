export default {
  // 快捷键
  // 支持快捷键 例如 ctrl+shift+s
  hotkey: {
    search: {
      open: 's',
      close: 'esc'
    }
  },
  // 侧边栏默认折叠状态
  menu: {
    asideCollapse: false
  },
  // 在读取持久化数据失败时默认页面
  page: {
    opened: [
      {
        name: 'index',
        fullPath: '/index',
        meta: {
          title: '首页',
          auth: false
        }
      }
    ],
    mng: {
      // 管理页面列表的table高度
      // TODO 动态计算当前设备的屏幕高度 - 上下冗余高度
      listHeight: 630,
      // 管理页面列表的table多选Checkbox列的宽度
      tableSelectionWidth: 45,
      // 管理页面列表默认查询的渠道标识
      defChannel: '0',
      // TODO 待后台`字典表`完成以下两个options要从后台获取数据
      channelOptions: [
        {
          value: '0',
          label: '内管'
        }
      ],
      // 管理页面列表默认分页设置
      defCurrent: 1,
      // defSize要在pageSizes范围中的某一个值
      defSize: 10,
      pageSizes: [5, 10, 50, 100]
    }
  },
  // 菜单搜索
  search: {
    enable: true
  },
  // 注册的主题
  theme: {
    list: [
      {
        title: 'd2admin 经典',
        name: 'd2',
        preview: 'image/theme/d2/preview@2x.png'
      },
      {
        title: '紫罗兰',
        name: 'violet',
        preview: 'image/theme/violet/preview@2x.png'
      },
      {
        title: '简约线条',
        name: 'line',
        backgroundImage: 'image/theme/line/bg.jpg',
        preview: 'image/theme/line/preview@2x.png'
      },
      {
        title: '流星',
        name: 'star',
        backgroundImage: 'image/theme/star/bg.jpg',
        preview: 'image/theme/star/preview@2x.png'
      },
      {
        title: 'Tomorrow Night Blue (vsCode)',
        name: 'tomorrow-night-blue',
        preview: 'image/theme/tomorrow-night-blue/preview@2x.png'
      }
    ]
  },
  // 是否默认开启页面切换动画
  transition: {
    active: true
  },
  // 在读取持久化数据失败时默认用户信息
  user: {
    info: {
      name: 'Ghost'
    }
  }
}
