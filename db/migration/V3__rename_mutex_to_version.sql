ALTER TABLE public.t_sys_app RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_basic_data RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_cloud RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_param RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_permission RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_role RENAME COLUMN mutex TO version;
ALTER TABLE public.t_sys_user RENAME COLUMN mutex TO version;

COMMENT ON COLUMN public.t_sys_app.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_basic_data.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_cloud.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_param.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_permission.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_role.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_sys_user.version IS '乐观锁版本号';
