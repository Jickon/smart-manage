# 采购申请模块

采购申请是用于验证标准业务单据架构的可移除供应链模块，不属于系统内核。

## 模块边界

- 后端：`sm.domain.scm.procurement.purchaserequisition`
- 前端：`src/domain/scm/procurement/purchaseRequisition`
- 数据库：`t_scm_purchase_requisition`、`t_scm_purchase_requisition_entry`
- Flyway：`V7__add_scm_purchase_requisition.sql`、`V8__add_procurement_menu_category.sql`
- 云：`scm`
- 应用：`procurement`
- 组件键：`scm/procurement/purchase-requisition`
- 权限前缀：`scm:procurement:purchase-requisition`

系统内核不得反向依赖本模块。模块可以依赖用户、组织、权限、日志、异常和通用页面能力。

## 移除方式

全新数据库尚未执行 V7、V8 时，可以删除上述前后端目录及两个迁移，随后重新生成前端组件注册表。
Flyway 版本允许存在空缺。

数据库已经执行 V7 后，禁止删除或修改 V7。应新增迁移，按“菜单与权限数据、明细表、主表、应用、云”的顺序卸载，并在确认没有业务数据后执行。
