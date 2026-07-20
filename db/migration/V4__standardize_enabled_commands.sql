-- 统一启用状态字段，并将启停能力拆分为独立业务命令。

UPDATE public.t_sys_cloud SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_cloud RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_cloud ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_cloud ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_cloud.enabled IS '启用状态';

UPDATE public.t_sys_app SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_app RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_app ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_app ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_app.enabled IS '启用状态';

UPDATE public.t_sys_menu SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_menu RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_menu ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_menu ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_menu.enabled IS '启用状态';

UPDATE public.t_sys_user SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_user RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_user ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_user ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_user.enabled IS '启用状态';

UPDATE public.t_sys_basic_data SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_basic_data RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_basic_data ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_basic_data ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_basic_data.enabled IS '启用状态';

UPDATE public.t_sys_basic_data_entry SET enable_flag = true WHERE enable_flag IS NULL;
ALTER TABLE public.t_sys_basic_data_entry RENAME COLUMN enable_flag TO enabled;
ALTER TABLE public.t_sys_basic_data_entry ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE public.t_sys_basic_data_entry ALTER COLUMN enabled SET NOT NULL;
COMMENT ON COLUMN public.t_sys_basic_data_entry.enabled IS '启用状态';

INSERT INTO public.t_sys_permission
    (id, name, number, app_id, create_time, version)
VALUES
    (420000000000001001, '用户管理-启用', 'sys:base:user:enable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001002, '用户管理-禁用', 'sys:base:user:disable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001003, '云管理-启用', 'sys:base:cloud:enable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001004, '云管理-禁用', 'sys:base:cloud:disable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001005, '应用管理-启用', 'sys:base:app:enable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001006, '应用管理-禁用', 'sys:base:app:disable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001007, '菜单管理-启用', 'sys:base:menu:enable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001008, '菜单管理-禁用', 'sys:base:menu:disable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001009, '基础数据管理-启用', 'sys:base:basic-data:enable', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001010, '基础数据管理-禁用', 'sys:base:basic-data:disable', 31, CURRENT_TIMESTAMP, 0);
