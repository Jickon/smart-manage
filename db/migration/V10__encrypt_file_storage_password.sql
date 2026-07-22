ALTER TABLE public.t_sys_file_config
    ADD COLUMN ftp_password_cipher character varying(1000);

COMMENT ON COLUMN public.t_sys_file_config.ftp_password_cipher IS 'FTP密码密文';

-- 历史明文密码不具备安全迁移条件，架构阶段直接移除，FTP 配置需由管理员重新录入。
ALTER TABLE public.t_sys_file_config
    DROP COLUMN ftp_password;
