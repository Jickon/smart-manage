-- 流程服务尚未形成独立领域实现，不应作为空云保留在系统初始化数据中。
DELETE FROM public.t_sys_cloud
WHERE number = 'workflow';
