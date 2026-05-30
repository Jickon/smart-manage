# PostgreSQL 数据库基线

本目录用于沉淀 smart-manage 的 PostgreSQL 表结构、备注和初始化数据。项目当前处于框架搭建阶段，数据库仍以实际环境为准；但每次稳定下来的结构变更，都应逐步补充到本目录，保证新环境可以复现基础框架。

## 目录约定

```text
db/postgresql/
├── README.md
├── schema/      # 建表、索引、表备注、字段备注
└── seed/        # 初始化数据，如超级管理员、应用、菜单、权限
```

## 脚本规范

- 创建表必须包含表备注和字段备注，使用 PostgreSQL `COMMENT ON` 语法。
- 主键统一使用雪花 ID，字段名为 `id`。
- 业务编码字段统一使用 `number`，不使用 `code`。
- 通用审计字段使用 `create_time`、`create_user`、`update_time`、`update_user`。
- 业务单据状态字段建议使用 `bill_status char(1)`，取值：`A` 暂存、`B` 已提交、`C` 审核通过、`D` 已关闭。
- 查询或导出数据时按项目经验使用 GBK；插入或更新数据时使用 UTF8。

## 执行参考

`psql` 路径：

```text
D:\Program Files\PostgreSQL\16\bin\psql.exe
```

执行脚本时必须带密码或设置 `PGPASSWORD`，避免命令卡在交互输入。

示例：

```powershell
$env:PGPASSWORD='postgres'
& 'D:\Program Files\PostgreSQL\16\bin\psql.exe' -h localhost -p 5432 -U postgres -d smart_manage -f .\db\postgresql\schema\001_init.sql
```
