-- 采购管理应用的侧边栏遵循“二级分类 -> 三级页面”菜单契约。
INSERT INTO public.t_sys_menu
    (id, number, name, level, parent_id, app_id, permission_id, icon, description,
     sort, enabled, create_time)
VALUES
    (430000000000000019, 'procurement_business', '采购业务', 2, 0, 430000000000000002,
     430000000000000010, 'ShoppingOutlined', '采购业务', 1, true, CURRENT_TIMESTAMP);

UPDATE public.t_sys_menu
SET parent_id = 430000000000000019,
    update_time = CURRENT_TIMESTAMP
WHERE id = 430000000000000020;
