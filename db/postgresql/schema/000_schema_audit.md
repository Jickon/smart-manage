# 当前数据库结构审计

审计时间：2026-05-30

数据来源：本地 `smart_manage` 数据库 `public` schema。

## 表清单

### 系统表

| 表名 | 表备注 | 字段数 | 已备注字段数 |
| --- | --- | ---: | ---: |
| t_sys_app | 应用 | 13 | 13 |
| t_sys_attachment | 附件 | 13 | 13 |
| t_sys_basic_data | 基础数据 | 9 | 9 |
| t_sys_basic_data_item | 基础数据项 | 10 | 10 |
| t_sys_biz_attachment | 业务附件关联 | 9 | 9 |
| t_sys_cloud | 云（应用分组） | 9 | 9 |
| t_sys_file_config | 文件存储配置 | 13 | 13 |
| t_sys_job | 定时任务 | 15 | 15 |
| t_sys_job_log | 定时任务执行日志 | 10 | 10 |
| t_sys_login_log | 系统服务-登录登出日志 | 14 | 14 |
| t_sys_menu | 菜单 | 17 | 17 |
| t_sys_operate_log | 系统服务-操作日志 | 19 | 19 |
| t_sys_org | 组织 | 9 | 9 |
| t_sys_param | 系统参数 | 10 | 10 |
| t_sys_permission | 权限 | 8 | 8 |
| t_sys_role | 角色 | 7 | 7 |
| t_sys_role_perms | 角色拥有的权限 | 7 | 7 |
| t_sys_script | 脚本管理 | 9 | 9 |
| t_sys_sql_log | SQL执行日志 | 13 | 13 |
| t_sys_ui_config | 界面配置 | 10 | 10 |
| t_sys_user | 用户 | 13 | 13 |
| t_sys_user_role | 用户在组织下的角色 | 8 | 8 |

### Quartz 表

当前库存在 Quartz 标准表：

- `qrtz_blob_triggers`
- `qrtz_calendars`
- `qrtz_cron_triggers`
- `qrtz_fired_triggers`
- `qrtz_job_details`
- `qrtz_locks`
- `qrtz_paused_trigger_grps`
- `qrtz_scheduler_state`
- `qrtz_simple_triggers`
- `qrtz_simprop_triggers`
- `qrtz_triggers`

## 待补齐

本次审计发现的空表备注已通过 `001_table_comments.sql` 补齐。

## 结论

- `t_sys_*` 字段备注已全部覆盖。
- 表备注已全部覆盖。
- Quartz 表属于第三方框架表，优先使用 Quartz 官方 PostgreSQL 初始化脚本，不强制按业务表备注规范处理。
