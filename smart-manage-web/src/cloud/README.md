# 前端业务页面目录约定

业务页面按 `src/cloud/{领域}/{应用}/{单据}` 分层，与后端 `sm.cloud.{领域}.{应用}.{单据}` 保持一致。

## 页面注册

每个可由菜单打开的页面目录应提供 `pageRegistration.ts`，默认导出 `definePageRegistration(...)` 的结果。

```ts
import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const DemoPage = lazy(() => import('./DemoPage'));
export default definePageRegistration('sys/base/demo', 'LIST', DemoPage);
```

执行 `pnpm gen:registry` 后会生成 `src/cloud/common/registry/componentRegistry.generated.tsx`。

## 页面类型

- `LIST`：标准列表页。
- `EDIT`：新增、编辑、查看共用的单据页。
- `CUSTOM`：配置页、报表、监控页等非标准页面。

页面组件通过 `PageComponentProps` 接收 `appNumber`、`componentKey`、`tabKey`、`operationType`、`billId` 等运行时上下文。

## 通用页面组合

标准列表页使用 `ListPage`：

- `filterSummary`：收起过滤时显示已选择条件的回显内容。
- `filterContent`：展开过滤时显示具体筛选控件。
- `toolbarActions`：放置业务按钮；表格右侧不放行操作按钮。
- `treePanel`：按需开启左树右表形态。左侧树由业务页面传入，`ListPage` 内部使用 Arco `ResizeBox.Split` 承担拖拽分割。
- `table`：放置 Arco `Table`。表格列应在左侧配置多选和行号 `#`，单据编号列负责打开单据页。

标准编辑页使用 `EditPage`：

- 顶部按钮栏固定，`sections` 内容区滚动。
- `sections[].extra` 放置分组内操作按钮，例如明细数据表格的 `增行`、`删行`。
- 明细分录优先使用 `EditableEntryTable`。
- 附件区域按需使用 `AttachmentPanel`，不是每个页面都必须启用。

当前示例：

- `src/cloud/sys/base/cloud`：普通列表 + 编辑页示例，对应 `t_sys_cloud`。
- `src/cloud/sys/base/app`：左树右表列表 + 编辑页示例，对应 `t_sys_app`。
