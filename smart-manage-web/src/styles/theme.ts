/** Ant Design ConfigProvider 主题配置 — 全局设计令牌 */

const themeConfig = {
  token: {
    // 品牌色
    colorPrimary: '#1677ff',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#ff4d4f',
    colorInfo: '#1677ff',
    colorTextBase: '#212121',

    // 圆角
    borderRadius: 2,

    // 中性色 — 表面/背景/边框/文字层级
    colorBgLayout: '#f5f5f5',
    colorBgContainer: '#ffffff',
    colorBgElevated: '#ffffff',
    colorBorder: '#d9d9d9',
    colorBorderSecondary: '#f0f0f0',
    colorText: 'rgba(0, 0, 0, 0.88)',
    colorTextSecondary: 'rgba(0, 0, 0, 0.65)',
    colorTextTertiary: 'rgba(0, 0, 0, 0.45)',

    // 间距
    paddingXS: 4,
    paddingSM: 8,
    padding: 12,
    paddingMD: 16,
    paddingLG: 20,
    paddingXL: 24,
    marginXS: 4,
    marginSM: 8,
    margin: 12,
    marginMD: 16,
    marginLG: 20,
    marginXL: 24,

    // 高度规范
    controlHeight: 32,
    controlHeightSM: 24,
    controlHeightLG: 40,
    lineHeight: 1.5715,

    // 字号
    fontSize: 14,
    fontSizeSM: 12,
    fontSizeLG: 16,
    fontSizeXL: 18,
    fontSizeHeading1: 24,
    fontSizeHeading2: 20,
    fontSizeHeading3: 16,

    // z-index 层级（注释标注使用场景，实际 z-index 由组件内部控制）
    // zIndexPopupBase 由 antd 内部控制，此处仅列参考值
  },
  components: {
    // 侧边栏菜单
    Menu: {},
    // 表格
    Table: {},
    Card: {
      lineWidth: 0,
    },
  },
  // algorithm: theme.compactAlgorithm,
};

export default themeConfig;
