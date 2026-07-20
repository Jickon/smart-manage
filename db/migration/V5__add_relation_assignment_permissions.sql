INSERT INTO public.t_sys_permission
    (id, name, number, app_id, create_time, version)
VALUES
    (420000000000001011, '角色管理-分配权限', 'sys:base:role:assignPermissions', 31, CURRENT_TIMESTAMP, 0),
    (420000000000001012, '用户管理-分配角色', 'sys:base:user:assignRoles', 31, CURRENT_TIMESTAMP, 0);
