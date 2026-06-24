# 单据页面架构改造计划

## 背景

本项目目标是 ERP/管理系统，前端参考金蝶苍穹/星瀚的设计理念，采用应用工作台和业务单据多页签模式。当前处于框架搭建阶段，可以接受整体大改，不做旧逻辑兼容。

## 目标

- 建立前端双 tabs 架构：Header tabs 切换应用，应用内部 tabs 切换业务单据/页面。
- 建立统一页面类型：`LIST`、`EDIT`、`CUSTOM`。
- 建立业务单据生命周期：保存、提交、查看、编辑能力由单据状态驱动。
- 建立新增页临时 uuid 到真实单据 id 的替换流程。
- 建立后端 `save`、`submit` 标准流程和可插拔保存校验能力。

## 前端页面模型

- `ListPage`：标准列表页，负责查询、分页、过滤、打开单据。
- `EditPage`：标准单据页，负责新增、编辑、查看。
- `CustomPage`：非标准页面兜底，例如配置页、报表、监控页、SQL 控制台。

页面类型枚举：

```ts
export type PageType = 'LIST' | 'EDIT' | 'CUSTOM';
```

操作类型枚举：

```ts
export enum OperationType {
  ADDNEW = 'ADDNEW',
  EDIT = 'EDIT',
  VIEW = 'VIEW',
}
```

## 组件注册表

- 后端菜单只维护元数据和稳定业务键。
- 前端组件注册表负责把 `component` 映射到真实组件。
- 新增业务页面时在页面目录新增 `pageRegistration.ts`，默认导出 `definePageRegistration(...)` 的结果。
- `pnpm gen:registry` 会扫描 `src/domain/**/pageRegistration.ts` 或 `.tsx` 并生成 `src/domain/common/registry/componentRegistry.generated.tsx`。

示例业务键：

```text
sys/base/user/list
sys/base/user/edit
sys/base/file-config/custom
```

## Tab 规则

- 列表页单实例。
- 编辑页按单据 id 多实例。
- 新增页每次新开临时实例，使用前端 uuid 作为临时 tab key。
- 同一单据编辑/查看共用一个 tab key，不能同时存在编辑 tab 和查看 tab。
- 切换 tab 不卸载 DOM，只做显隐。

新增保存流程：

1. 用户点击新增，前端创建 uuid 临时 tab key。
2. 前端调用 `createNewData` 获取初始数据，后端不返回 id。
3. 用户点击保存，前端调用 `save`。
4. 后端新增成功后返回真实 id。
5. 前端调用 `detail(id)` 获取完整回显数据。
6. 前端把临时 tab key 替换为真实单据 tab key。
7. 保存后不关闭 `EditPage`，继续停留当前单据。

## 单据状态

业务单据建议使用 `char(1)` 存储状态：

| 存储值 | 枚举名 | 含义 |
| --- | --- | --- |
| A | SAVED | 暂存 |
| B | SUBMITTED | 已提交 |
| C | AUDITED | 审核通过 |
| D | CLOSED | 已关闭 |

说明：

- 使用正确拼写 `SUBMITTED`。
- 日志、监控记录、单条配置等非业务单据不强制加入单据状态。
- 单据状态应放在业务单据基类或单据能力接口中，不放进所有实体通用的 `BaseEntity`。

## 保存与提交

前端按钮：

- 保存：暂存数据，状态保持 `SAVED`，用户后续可以继续修改。
- 提交：状态从 `SAVED` 推进为 `SUBMITTED`，提交后不可普通编辑或保存。

后端接口：

- `save`：负责新增 insert 和暂存 update。
- `submit`：负责提交并推进单据状态。

`save` 的 Service 内部建议结构：

```java
@Transactional(rollbackFor = Exception.class)
public Long save(BillSaveForm form) {
    if (form.getId() == null) {
        return addNew(form);
    }
    return update(form);
}
```

约束：

- 新增由 `id == null` 判定。
- 修改由 `id != null` 判定。
- 后端不能相信前端传来的 id 和状态来决定数据库行为。
- 修改、提交校验必须读取数据库当前状态。

## 可插拔保存校验

需要快速限制“只有新增或暂存单据才允许保存”的场景时，可以设计注解式校验，例如：

```java
@BillSaveCheck
public Long save(BillSaveForm form) {
    ...
}
```

默认规则：

- `id == null`：新增，允许进入 insert。
- `id != null`：读取数据库当前单据，只有 `SAVED` 状态允许 update。

该能力应可选，不强制所有单据使用。

## 待确认

- 真实单据 tab key 的最终格式。
- `submit` 是否每个单据都单独实现，还是先抽象公共接口。
- 通用 `ListPage`、`EditPage`、`CustomPage` 的视觉设计，需用户打开参考网站后再定稿。
