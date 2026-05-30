--
-- PostgreSQL database dump
--

\restrict GVDA8qKgu50wgAwilB63wkqupLVc5wCf1onierZPqqXb56hjKNebaAMlU6ge0wC

-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: t_sys_cloud; Type: TABLE DATA; Schema: public; Owner: -
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.t_sys_cloud DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_cloud (id, name, number, seq, enable_flag, create_time, update_time, create_user, update_user) VALUES (5, '流程服务云', 'workflow', 98, true, '2026-04-23 22:36:49.946408', '2026-04-24 23:55:10.435839', NULL, NULL);
INSERT INTO public.t_sys_cloud (id, name, number, seq, enable_flag, create_time, update_time, create_user, update_user) VALUES (4, '系统服务云', 'sys', 99, true, '2026-04-22 13:47:23.710312', '2026-04-25 18:21:13.852705', NULL, NULL);


ALTER TABLE public.t_sys_cloud ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_app; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_app DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_app (id, name, number, icon, seq, description, cloud_id, enable_flag, create_time, update_time, icon_color, create_user, update_user) VALUES (30, '系统监控', 'monitor', 'Monitoring', 2, '系统监控', 4, true, '2026-04-22 13:47:23.710312', '2026-04-26 01:11:48.444742', '#ff0000', NULL, NULL);
INSERT INTO public.t_sys_app (id, name, number, icon, seq, description, cloud_id, enable_flag, create_time, update_time, icon_color, create_user, update_user) VALUES (31, '系统建模', 'base', 'BasicModeling', 1, '云、应用、菜单、用户等基础数据', 4, true, '2026-04-22 18:06:56.092765', '2026-05-13 17:18:14.544957', '#1BA854', NULL, 1);


ALTER TABLE public.t_sys_app ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_permission; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_permission DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10031, '云管理-查询', 'sys:base:cloud:listPage', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10032, '云管理-详情', 'sys:base:cloud:detail', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10033, '云管理-保存', 'sys:base:cloud:save', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10034, '云管理-删除', 'sys:base:cloud:delete', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10035, '应用管理-查询', 'sys:base:app:listPage', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10036, '应用管理-详情', 'sys:base:app:detail', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10037, '应用管理-保存', 'sys:base:app:save', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10038, '应用管理-删除', 'sys:base:app:delete', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10014, '权限管理-查询', 'sys:base:permission:listPage', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10012, '用户管理-查询', 'sys:base:user:listPage', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10015, '角色管理-查询', 'sys:base:role:listPage', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10016, '权限管理-保存', 'sys:base:permission:save', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10017, '权限管理-详情', 'sys:base:permission:detail', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10013, '菜单管理-查询', 'sys:base:menu:listPage', 31, NULL, '2026-04-27 12:17:57.584541', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10039, '角色管理-详情', 'sys:base:role:detail', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10040, '角色管理-保存', 'sys:base:role:save', 31, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10041, '权限管理-选择', 'sys:base:permission:select', 31, '2026-04-27 13:45:08.495725', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10042, '权限管理-删除', 'sys:base:permission:delete', 31, '2026-04-27 13:45:51.39581', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10043, '角色权限映射-查询', 'sys:base:roleperms:list', 31, '2026-04-27 13:50:15.037814', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (406250201727746048, '菜单管理-详情', 'sys:base:menu:detail', 31, '2026-04-27 13:54:15.855314', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10044, '角色权限映射-保存', 'sys:base:roleperms:save', 31, '2026-04-27 13:55:08.1592', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10030, '系统建模-应用入口', 'sys:base:access', 31, NULL, '2026-04-27 14:00:32.890395', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (406254838245605376, '菜单管理-保存', 'sys:base:menu:save', 31, '2026-04-27 14:12:41.287791', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (406259661691011072, '菜单管理-选择', 'sys:base:menu:select', 31, '2026-04-27 14:31:51.286645', NULL, 1, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10022, '登录日志-查询', 'sys:log:login:listPage', 30, NULL, '2026-04-27 15:38:27.280076', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10023, '操作日志-查询', 'sys:log:operate:listPage', 30, NULL, '2026-04-27 15:38:38.913184', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (10020, '日志应用入口', 'sys:log:access', 30, NULL, '2026-05-13 18:13:36.35951', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413172783453237248, '界面配置列表', 'sys:base:ui-config:listPage', 31, '2026-05-16 16:22:07.956473', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413172783499374592, '界面配置详情', 'sys:base:ui-config:detail', 31, '2026-05-16 16:22:07.965499', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413172783507763200, '界面配置保存', 'sys:base:ui-config:save', 31, '2026-05-16 16:22:07.968478', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413172783520346112, '界面配置删除', 'sys:base:ui-config:delete', 31, '2026-05-16 16:22:07.970606', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413196675722964992, '文件配置列表', 'sys:base:file-config:listPage', 31, '2026-05-16 17:57:04.317767', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413196675756519424, '文件配置详情', 'sys:base:file-config:detail', 31, '2026-05-16 17:57:04.323767', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413196675764908032, '文件配置保存', 'sys:base:file-config:save', 31, '2026-05-16 17:57:04.325767', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413196675777490944, '文件配置删除', 'sys:base:file-config:delete', 31, '2026-05-16 17:57:04.328146', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413260828487667712, '定时任务列表', 'sys:monitor:job:listPage', 30, '2026-05-16 22:11:59.528179', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413260828529610752, '定时任务详情', 'sys:monitor:job:detail', 30, '2026-05-16 22:11:59.536189', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413260828537999360, '定时任务编辑', 'sys:monitor:job:save', 30, '2026-05-16 22:11:59.538188', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413260828546387968, '定时任务删除', 'sys:monitor:job:delete', 30, '2026-05-16 22:11:59.540189', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413260828550582272, '执行实例列表', 'sys:monitor:job-log:listPage', 30, '2026-05-16 22:11:59.541187', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (5001, '任务调度分类', 'sys:monitor:job:category', 30, '2026-05-16 22:33:07.430001', '2026-05-16 22:33:07.430001', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50050, '系统参数分类', 'sys:base:param:category', 31, '2026-05-17 01:13:34.738535', '2026-05-17 01:13:34.738535', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50051, '系统参数列表', 'sys:base:param:listPage', 31, '2026-05-17 01:13:34.738535', '2026-05-17 01:13:34.738535', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50052, '系统参数详情', 'sys:base:param:detail', 31, '2026-05-17 01:13:34.738535', '2026-05-17 01:13:34.738535', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50053, '系统参数编辑', 'sys:base:param:save', 31, '2026-05-17 01:13:34.738535', '2026-05-17 01:13:34.738535', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50054, '系统参数删除', 'sys:base:param:delete', 31, '2026-05-17 01:13:34.738535', '2026-05-17 01:13:34.738535', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413501707269836800, '服务监控列表', 'sys:monitor:node:listPage', 30, '2026-05-17 14:09:09.506432', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413501707311779840, '数据源监控列表', 'sys:monitor:druid:listPage', 30, '2026-05-17 14:09:09.513432', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413501707320168448, 'Arthas命令执行', 'sys:monitor:arthas:execute', 30, '2026-05-17 14:09:09.515591', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50070, '缓存管理列表', 'sys:monitor:cache:listPage', 30, '2026-05-17 20:09:21.311371', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (50071, '缓存管理操作', 'sys:monitor:cache:save', 30, '2026-05-17 20:09:21.311371', NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413501707400000001, 'SQL 控制台', 'sys:monitor:sql', 30, '2026-05-18 00:27:43.408601', '2026-05-18 00:27:43.408601', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (413501707400000003, 'SQL 执行历史', 'sys:monitor:sql-log:listPage', 30, '2026-05-18 00:43:01.026314', '2026-05-18 00:43:01.026314', NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (411644663060602880, '基础数据管理-删除', 'sys:base:basic-data:delete', 31, '2026-05-12 11:09:55.661809', '2026-05-12 11:15:10.76583', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (411644663027048448, '基础数据管理-详情', 'sys:base:basic-data:detail', 31, '2026-05-12 11:09:55.653679', '2026-05-12 11:15:21.394506', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (411644662943162368, '基础数据管理-列表', 'sys:base:basic-data:listPage', 31, '2026-05-12 11:09:55.638553', '2026-05-12 11:15:30.967268', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (411644663043825664, '基础数据管理-保存', 'sys:base:basic-data:save', 31, '2026-05-12 11:09:55.65768', '2026-05-12 11:15:44.892574', NULL, 1);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000001, '脚本执行', 'sys:monitor:script:execute', 30, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000002, '脚本列表', 'sys:monitor:script:listPage', 30, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000003, '脚本详情', 'sys:monitor:script:detail', 30, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000004, '脚本编辑', 'sys:monitor:script:save', 30, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000005, '脚本删除', 'sys:monitor:script:delete', 30, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_permission (id, name, number, app_id, create_time, update_time, create_user, update_user) VALUES (419000000000000006, '脚本控制台', 'sys:monitor:script', 30, NULL, NULL, NULL, NULL);


ALTER TABLE public.t_sys_permission ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_menu; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_menu DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3103, 'app', '应用管理', 3, 3101, 31, 10035, '/sys/base/app', 'sys/base/app', 'allowance-workbench', '应用管理', 3, true, '2026-04-22 18:06:56.092765', '2026-04-27 14:40:29.817439', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707345334272, 'SYS_MONITOR_NODE', '节点监控', 3, 413501707332751360, 30, 413501707269836800, '/sys/monitor/node', 'sys/monitor/node', 'monitor', NULL, 10, true, '2026-05-17 14:09:09.521627', NULL, NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707362111488, 'SYS_MONITOR_DRUID', '数据源监控', 3, 413501707332751360, 30, 413501707311779840, '/sys/monitor/druid', 'sys/monitor/druid', 'database', NULL, 20, true, '2026-05-17 14:09:09.525589', NULL, NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707391471616, 'SYS_DIAG_ARTHAS', 'Arthas诊断', 3, 413501707370500096, 30, 413501707269836800, '/sys/monitor/arthas', 'sys/monitor/arthas', 'tool', NULL, 10, true, '2026-05-17 14:09:09.53259', NULL, NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (50081, 'CACHE_PAGE', '缓存管理', 3, 50080, 30, 50070, '/sys/monitor/cache', 'sys/monitor/cache', 'link', NULL, 1, true, '2026-05-17 20:09:21.311371', '2026-05-17 20:09:21.311371', NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707400000002, 'sys_monitor_sql', 'SQL 执行', 3, 413501707410000001, 30, 413501707400000001, '/sys/monitor/sql', 'sys/monitor/sql', 'sql', NULL, 1, true, '2026-05-18 00:28:23.884474', '2026-05-18 00:28:23.884474', NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707400000004, 'sys_monitor_sql_log', '执行历史', 3, 413501707410000001, 30, 413501707400000003, '/sys/monitor/sql-log', 'sys/monitor/sql-log', 'log', NULL, 2, true, '2026-05-18 00:43:01.026314', '2026-05-18 00:43:01.026314', NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3003, 'operate_log', '操作日志', 3, 3000, 30, 10023, '/sys/monitor/operate-log', 'sys/monitor/operate-log', 'detect', '操作日志', 2, true, '2026-04-22 13:47:23.710312', '2026-05-12 18:52:33.314528', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3002, 'login_log', '登录日志', 3, 3000, 30, 10022, '/sys/monitor/login-log', 'sys/monitor/login-log', 'detail', '登录日志', 1, true, '2026-04-22 13:47:23.710312', '2026-05-12 18:53:49.107036', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413172783545511936, 'UI_CONFIG_PAGE', '界面配置', 3, 413172783532929024, 31, 413172783453237248, '/sys/base/ui-config', 'sys/base/ui-config', 'setting', NULL, 10, true, '2026-05-16 16:22:07.976623', '2026-05-18 17:33:03.450756', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3101, 'base_data', '基础数据', 2, 0, 31, 10030, NULL, NULL, 'DataModel', '分组', 1, true, '2026-04-22 18:06:56.092765', '2026-05-18 14:46:10.789543', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (2102, 'user', '用户管理', 3, 3101, 31, 10012, '/sys/base/user', 'sys/base/user', 'person-solid', '用户', 1, true, '2026-04-14 13:59:27.544725', '2026-04-27 14:42:29.342171', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (2103, 'menu', '菜单管理', 3, 3101, 31, 10013, '/sys/base/menu', 'sys/base/menu', 'menu', '菜单', 4, true, '2026-04-14 13:59:27.544725', '2026-04-27 14:41:15.109114', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (2104, 'permission', '权限管理', 3, 3101, 31, 10014, '/sys/base/permission', 'sys/base/permission', 'setting', '权限', 5, true, '2026-04-14 13:59:27.544725', '2026-04-27 14:41:48.634741', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (2105, 'role', '角色管理', 3, 3101, 31, 10015, '/sys/base/role', 'sys/base/role', 'job-info', '角色', 6, true, '2026-04-21 10:54:03.230143', '2026-04-27 14:42:00.628409', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413260828563165184, 'JOB001', '定时任务', 3, 5001, 30, 413260828487667712, '/sys/monitor/job', 'sys/monitor/job', 'clock-circle', NULL, 30, true, '2026-05-16 22:11:59.544189', NULL, NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413172783532929024, 'UI_CONFIG_CAT', '界面配置', 2, 0, 31, 413172783453237248, NULL, NULL, 'BaselineUiAutomationCoverage', NULL, 80, true, '2026-05-16 16:22:07.973646', '2026-05-18 14:47:07.270243', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413260828571553792, 'JOB002', '执行实例', 3, 5001, 30, 413260828550582272, '/sys/monitor/job-log', 'sys/monitor/job-log', 'history', NULL, 31, true, '2026-05-16 22:11:59.546207', NULL, NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413196675785879552, 'FILE_CONFIG_CAT', '文件配置', 2, 0, 31, 413196675722964992, NULL, NULL, 'ProcessFileManagement', NULL, 90, true, '2026-05-16 17:57:04.331269', '2026-05-18 14:48:03.979705', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (50060, 'PARAM_CAT', '系统参数', 2, 0, 31, 50050, NULL, NULL, 'StoreSystemParameters', NULL, 110, true, '2026-05-17 01:13:51.334518', '2026-05-18 14:48:50.366788', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (50061, 'PARAM001', '系统参数', 3, 50060, 31, 50051, '/sys/base/sys-param', 'sys/base/sys-param', 'setting', NULL, 1, true, '2026-05-17 01:13:51.334518', '2026-05-18 15:00:10.577802', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3102, 'cloud', '云管理', 3, 3101, 31, 10031, '/sys/base/cloud', 'sys/base/cloud', 'platform', '云管理', 2, true, '2026-04-22 18:06:56.092765', '2026-04-27 14:40:06.614008', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (411644663089963008, 'dict', '基础数据管理', 3, 3101, 31, 411644662943162368, 'sys/base/basic-data', 'sys/base/basic-data', 'business-type', NULL, 70, true, '2026-05-12 11:09:55.668845', '2026-05-12 12:32:16.205663', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (419000000000000011, 'script_page', '脚本控制台', 3, 419000000000000010, 30, 419000000000000002, '/sys/monitor/script', 'sys/monitor/script', NULL, NULL, 1, true, '2026-05-19 14:17:47.759591', '2026-05-19 14:17:47.759591', NULL, NULL);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (3000, 'group_log', '日志监控', 2, 0, 30, 10020, NULL, NULL, 'ChangeTheLog', '分组', 1, true, '2026-04-22 13:47:23.710312', '2026-05-19 15:31:44.170063', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413196675798462464, 'FILE_CONFIG_PAGE', '文件配置', 3, 413196675785879552, 31, 413196675722964992, '/sys/base/file-config', 'sys/base/file-config', 'file', NULL, 10, true, '2026-05-16 17:57:04.333267', '2026-05-18 16:06:31.020184', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (5001, 'JOB001', '任务调度', 2, 0, 30, 5001, NULL, NULL, 'JobSequence', NULL, 2, true, '2026-05-16 22:33:07.430001', '2026-05-19 15:32:11.552601', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (50080, 'CACHE_CAT', '缓存管理', 2, 0, 30, 50070, NULL, NULL, 'BusinessCircleOperation', NULL, 20, true, '2026-05-17 20:09:21.311371', '2026-05-19 15:33:55.639086', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707332751360, 'SYS_MONITOR_CAT', '服务监控', 2, 0, 30, 413501707269836800, NULL, NULL, 'PerformanceMonitoring', NULL, 30, true, '2026-05-17 14:09:09.518595', '2026-05-19 15:34:34.586321', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707410000001, 'sql_cat', 'SQL 控制台', 2, 0, 30, 413501707400000001, NULL, NULL, 'BusinessDataProcess', NULL, 35, true, '2026-05-18 00:42:29.47291', '2026-05-19 15:35:51.28396', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (413501707370500096, 'SYS_DIAG_CAT', '诊断工具', 2, 0, 30, 413501707269836800, NULL, NULL, 'BusinessData', NULL, 40, true, '2026-05-17 14:09:09.527589', '2026-05-19 15:37:14.565005', NULL, 1);
INSERT INTO public.t_sys_menu (id, number, name, level, parent_id, app_id, permission_id, path, component, icon, description, sort, enable_flag, create_time, update_time, create_user, update_user) VALUES (419000000000000010, 'script_cat', '脚本控制台', 2, 0, 30, 419000000000000006, NULL, NULL, 'ScriptApplication', NULL, 40, true, '2026-05-19 14:17:47.757984', '2026-05-19 15:37:33.547326', NULL, 1);


ALTER TABLE public.t_sys_menu ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_org; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_org DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_org (id, name, number, parent_id, sort, create_time, update_time, create_user, update_user) VALUES (1, '默认组织', 'DEFAULT_ORG', 0, 1, '2026-04-14 13:15:18.817269', '2026-04-14 13:15:18.817269', NULL, NULL);


ALTER TABLE public.t_sys_org ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_role; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_role DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_role (id, name, number, create_time, update_time, create_user, update_user) VALUES (1, '系统管理员', 'admin', NULL, '2026-04-27 15:39:34.171162', NULL, 1);


ALTER TABLE public.t_sys_role ENABLE TRIGGER ALL;

--
-- Data for Name: t_sys_role_perms; Type: TABLE DATA; Schema: public; Owner: -
--

ALTER TABLE public.t_sys_role_perms DISABLE TRIGGER ALL;

INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477120, 1, 10020, '2026-04-27 15:39:34.19102', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477121, 1, 10031, '2026-04-27 15:39:34.192028', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477122, 1, 10032, '2026-04-27 15:39:34.192028', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477123, 1, 10033, '2026-04-27 15:39:34.192028', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477124, 1, 10034, '2026-04-27 15:39:34.192028', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477125, 1, 10035, '2026-04-27 15:39:34.193162', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477126, 1, 10036, '2026-04-27 15:39:34.193162', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477127, 1, 10037, '2026-04-27 15:39:34.193994', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477128, 1, 10038, '2026-04-27 15:39:34.194304', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477129, 1, 10014, '2026-04-27 15:39:34.194304', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477130, 1, 10012, '2026-04-27 15:39:34.194304', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477131, 1, 10015, '2026-04-27 15:39:34.195304', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477132, 1, 10016, '2026-04-27 15:39:34.195653', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477133, 1, 10017, '2026-04-27 15:39:34.195946', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702745477134, 1, 10013, '2026-04-27 15:39:34.196238', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671424, 1, 10039, '2026-04-27 15:39:34.19652', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671425, 1, 10040, '2026-04-27 15:39:34.19652', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671426, 1, 10041, '2026-04-27 15:39:34.197102', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671427, 1, 10042, '2026-04-27 15:39:34.197396', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671428, 1, 10043, '2026-04-27 15:39:34.197695', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671429, 1, 406250201727746048, '2026-04-27 15:39:34.197999', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671430, 1, 10044, '2026-04-27 15:39:34.1983', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671431, 1, 10030, '2026-04-27 15:39:34.1983', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671432, 1, 406254838245605376, '2026-04-27 15:39:34.198917', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671433, 1, 406259661691011072, '2026-04-27 15:39:34.19923', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671434, 1, 10022, '2026-04-27 15:39:34.199547', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (406276702749671435, 1, 10023, '2026-04-27 15:39:34.199547', NULL, 1, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000020, 1, 419000000000000001, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000021, 1, 419000000000000002, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000022, 1, 419000000000000003, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000023, 1, 419000000000000004, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000024, 1, 419000000000000005, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_role_perms (id, role_id, permission_id, create_time, update_time, create_user, update_user) VALUES (419000000000000025, 1, 419000000000000006, NULL, NULL, NULL, NULL);


ALTER TABLE public.t_sys_role_perms ENABLE TRIGGER ALL;

--
-- PostgreSQL database dump complete
--

\unrestrict GVDA8qKgu50wgAwilB63wkqupLVc5wCf1onierZPqqXb56hjKNebaAMlU6ge0wC

