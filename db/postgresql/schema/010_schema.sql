--
-- PostgreSQL database dump
--

\restrict j6ABwsHpf6emr5XDxVbbVNCcZD3mkaP8FXAVf9VVlV8H1DOWvaLpzDmq0zhLHGE

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
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


--
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


--
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80)
);


--
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent boolean,
    requests_recovery boolean
);


--
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable boolean NOT NULL,
    is_nonconcurrent boolean NOT NULL,
    is_update_data boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea
);


--
-- Name: qrtz_locks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


--
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


--
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


--
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


--
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);


--
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);


--
-- Name: t_sys_app; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_app (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    icon character varying(255) NOT NULL,
    seq integer DEFAULT 99,
    description character varying(255),
    cloud_id bigint NOT NULL,
    enable_flag boolean,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    icon_color character varying(32),
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_app; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_app IS '应用';


--
-- Name: COLUMN t_sys_app.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.id IS 'ID';


--
-- Name: COLUMN t_sys_app.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.name IS '名称';


--
-- Name: COLUMN t_sys_app.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.number IS '编码';


--
-- Name: COLUMN t_sys_app.icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.icon IS '图标';


--
-- Name: COLUMN t_sys_app.seq; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.seq IS '排序';


--
-- Name: COLUMN t_sys_app.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.description IS '描述';


--
-- Name: COLUMN t_sys_app.cloud_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.cloud_id IS '所属云ID';


--
-- Name: COLUMN t_sys_app.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_app.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_app.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_app.icon_color; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.icon_color IS '图标颜色';


--
-- Name: COLUMN t_sys_app.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.create_user IS '创建人';


--
-- Name: COLUMN t_sys_app.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_app.update_user IS '修改人';


--
-- Name: t_sys_attachment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_attachment (
    id bigint NOT NULL,
    original_name character varying(500) NOT NULL,
    stored_name character varying(500) NOT NULL,
    stored_path character varying(500) NOT NULL,
    file_size bigint,
    mime_type character varying(200),
    file_ext character varying(50),
    storage_type character varying(20) NOT NULL,
    is_temp boolean DEFAULT true,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_attachment; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_attachment IS '附件';


--
-- Name: COLUMN t_sys_attachment.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.id IS 'ID';


--
-- Name: COLUMN t_sys_attachment.original_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.original_name IS '原始文件名';


--
-- Name: COLUMN t_sys_attachment.stored_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.stored_name IS '存储文件名';


--
-- Name: COLUMN t_sys_attachment.stored_path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.stored_path IS '存储路径';


--
-- Name: COLUMN t_sys_attachment.file_size; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.file_size IS '文件大小';


--
-- Name: COLUMN t_sys_attachment.mime_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.mime_type IS 'MIME类型';


--
-- Name: COLUMN t_sys_attachment.file_ext; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.file_ext IS '文件扩展名';


--
-- Name: COLUMN t_sys_attachment.storage_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.storage_type IS '存储方式';


--
-- Name: COLUMN t_sys_attachment.is_temp; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.is_temp IS '是否临时文件';


--
-- Name: COLUMN t_sys_attachment.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_attachment.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_attachment.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.create_user IS '创建人';


--
-- Name: COLUMN t_sys_attachment.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_attachment.update_user IS '修改人';


--
-- Name: t_sys_basic_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_basic_data (
    id bigint NOT NULL,
    number character varying(64) NOT NULL,
    name character varying(128) NOT NULL,
    remark character varying(255),
    enable_flag boolean DEFAULT true,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_basic_data; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_basic_data IS '基础数据';


--
-- Name: COLUMN t_sys_basic_data.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.id IS 'ID';


--
-- Name: COLUMN t_sys_basic_data.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.number IS '编码';


--
-- Name: COLUMN t_sys_basic_data.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.name IS '名称';


--
-- Name: COLUMN t_sys_basic_data.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.remark IS '备注';


--
-- Name: COLUMN t_sys_basic_data.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_basic_data.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_basic_data.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_basic_data.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.create_user IS '创建人';


--
-- Name: COLUMN t_sys_basic_data.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data.update_user IS '修改人';


--
-- Name: t_sys_basic_data_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_basic_data_item (
    id bigint NOT NULL,
    type_number character varying(64) NOT NULL,
    item_code character varying(64) NOT NULL,
    item_label character varying(128) NOT NULL,
    sort integer DEFAULT 0,
    enable_flag boolean DEFAULT true,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_basic_data_item; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_basic_data_item IS '基础数据项';


--
-- Name: COLUMN t_sys_basic_data_item.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.id IS 'ID';


--
-- Name: COLUMN t_sys_basic_data_item.type_number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.type_number IS '字典编码';


--
-- Name: COLUMN t_sys_basic_data_item.item_code; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.item_code IS '字典项编码';


--
-- Name: COLUMN t_sys_basic_data_item.item_label; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.item_label IS '字典项名称';


--
-- Name: COLUMN t_sys_basic_data_item.sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.sort IS '排序';


--
-- Name: COLUMN t_sys_basic_data_item.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_basic_data_item.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_basic_data_item.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_basic_data_item.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.create_user IS '创建人';


--
-- Name: COLUMN t_sys_basic_data_item.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_basic_data_item.update_user IS '修改人';


--
-- Name: t_sys_biz_attachment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_biz_attachment (
    id bigint NOT NULL,
    biz_type character varying(100) NOT NULL,
    biz_id character varying(64),
    attachment_id bigint NOT NULL,
    sort integer DEFAULT 0,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_biz_attachment; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_biz_attachment IS '业务附件关联';


--
-- Name: COLUMN t_sys_biz_attachment.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.id IS 'ID';


--
-- Name: COLUMN t_sys_biz_attachment.biz_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.biz_type IS '业务类型';


--
-- Name: COLUMN t_sys_biz_attachment.biz_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.biz_id IS '业务ID';


--
-- Name: COLUMN t_sys_biz_attachment.attachment_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.attachment_id IS '附件ID';


--
-- Name: COLUMN t_sys_biz_attachment.sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.sort IS '排序';


--
-- Name: COLUMN t_sys_biz_attachment.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_biz_attachment.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_biz_attachment.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.create_user IS '创建人';


--
-- Name: COLUMN t_sys_biz_attachment.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_biz_attachment.update_user IS '修改人';


--
-- Name: t_sys_cloud; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_cloud (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    seq integer DEFAULT 99,
    enable_flag boolean,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_cloud; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_cloud IS '云（应用分组）';


--
-- Name: COLUMN t_sys_cloud.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.id IS 'ID';


--
-- Name: COLUMN t_sys_cloud.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.name IS '名称';


--
-- Name: COLUMN t_sys_cloud.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.number IS '编码';


--
-- Name: COLUMN t_sys_cloud.seq; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.seq IS '排序';


--
-- Name: COLUMN t_sys_cloud.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_cloud.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_cloud.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_cloud.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.create_user IS '创建人';


--
-- Name: COLUMN t_sys_cloud.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_cloud.update_user IS '修改人';


--
-- Name: t_sys_file_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_file_config (
    id bigint NOT NULL,
    storage_type character varying(20) DEFAULT 'LOCAL'::character varying NOT NULL,
    local_dir character varying(500),
    ftp_host character varying(200),
    ftp_port integer DEFAULT 21,
    ftp_username character varying(200),
    ftp_password character varying(200),
    ftp_dir character varying(500),
    ftp_passive_mode boolean DEFAULT true,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_file_config; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_file_config IS '文件存储配置';


--
-- Name: COLUMN t_sys_file_config.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.id IS 'ID';


--
-- Name: COLUMN t_sys_file_config.storage_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.storage_type IS '存储方式';


--
-- Name: COLUMN t_sys_file_config.local_dir; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.local_dir IS '本地存储目录';


--
-- Name: COLUMN t_sys_file_config.ftp_host; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_host IS 'FTP主机';


--
-- Name: COLUMN t_sys_file_config.ftp_port; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_port IS 'FTP端口';


--
-- Name: COLUMN t_sys_file_config.ftp_username; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_username IS 'FTP用户名';


--
-- Name: COLUMN t_sys_file_config.ftp_password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_password IS 'FTP密码';


--
-- Name: COLUMN t_sys_file_config.ftp_dir; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_dir IS 'FTP目录';


--
-- Name: COLUMN t_sys_file_config.ftp_passive_mode; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.ftp_passive_mode IS 'FTP被动模式';


--
-- Name: COLUMN t_sys_file_config.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_file_config.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_file_config.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.create_user IS '创建人';


--
-- Name: COLUMN t_sys_file_config.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_file_config.update_user IS '修改人';


--
-- Name: t_sys_job; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_job (
    id bigint NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) DEFAULT 'DEFAULT'::character varying NOT NULL,
    description character varying(500),
    job_class_name character varying(500) NOT NULL,
    cron_expression character varying(100) NOT NULL,
    job_data text,
    status character varying(20) DEFAULT 'ENABLED'::character varying NOT NULL,
    remark character varying(500),
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint,
    number character varying(100) NOT NULL,
    is_system boolean DEFAULT false
);


--
-- Name: TABLE t_sys_job; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_job IS '定时任务';


--
-- Name: COLUMN t_sys_job.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.id IS 'ID';


--
-- Name: COLUMN t_sys_job.job_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.job_name IS '任务名称';


--
-- Name: COLUMN t_sys_job.job_group; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.job_group IS '任务组';


--
-- Name: COLUMN t_sys_job.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.description IS '描述';


--
-- Name: COLUMN t_sys_job.job_class_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.job_class_name IS '执行类名';


--
-- Name: COLUMN t_sys_job.cron_expression; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.cron_expression IS 'Cron表达式';


--
-- Name: COLUMN t_sys_job.job_data; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.job_data IS '任务参数';


--
-- Name: COLUMN t_sys_job.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.status IS '状态';


--
-- Name: COLUMN t_sys_job.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.remark IS '备注';


--
-- Name: COLUMN t_sys_job.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_job.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_job.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.create_user IS '创建人';


--
-- Name: COLUMN t_sys_job.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.update_user IS '修改人';


--
-- Name: COLUMN t_sys_job.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.number IS '编码';


--
-- Name: COLUMN t_sys_job.is_system; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job.is_system IS '是否系统内置';


--
-- Name: t_sys_job_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_job_log (
    id bigint NOT NULL,
    job_id bigint,
    job_name character varying(200),
    job_group character varying(200),
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    duration_ms bigint,
    status character varying(20),
    error_message text,
    create_time timestamp without time zone
);


--
-- Name: TABLE t_sys_job_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_job_log IS '定时任务执行日志';


--
-- Name: COLUMN t_sys_job_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.id IS 'ID';


--
-- Name: COLUMN t_sys_job_log.job_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.job_id IS '任务ID';


--
-- Name: COLUMN t_sys_job_log.job_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.job_name IS '任务名称';


--
-- Name: COLUMN t_sys_job_log.job_group; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.job_group IS '任务组';


--
-- Name: COLUMN t_sys_job_log.start_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.start_time IS '开始时间';


--
-- Name: COLUMN t_sys_job_log.end_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.end_time IS '结束时间';


--
-- Name: COLUMN t_sys_job_log.duration_ms; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.duration_ms IS '耗时(ms)';


--
-- Name: COLUMN t_sys_job_log.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.status IS '状态';


--
-- Name: COLUMN t_sys_job_log.error_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.error_message IS '错误信息';


--
-- Name: COLUMN t_sys_job_log.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_job_log.create_time IS '创建时间';


--
-- Name: t_sys_login_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_login_log (
    id bigint NOT NULL,
    user_id bigint,
    username character varying(128),
    nickname character varying(255),
    event_type character varying(32) NOT NULL,
    success boolean DEFAULT true NOT NULL,
    fail_reason character varying(512),
    ip character varying(64),
    user_agent character varying(1024),
    token_hint character varying(64),
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_login_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_login_log IS '系统服务-登录登出日志';


--
-- Name: COLUMN t_sys_login_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.id IS 'ID';


--
-- Name: COLUMN t_sys_login_log.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.user_id IS '用户ID';


--
-- Name: COLUMN t_sys_login_log.username; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.username IS '用户名';


--
-- Name: COLUMN t_sys_login_log.nickname; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.nickname IS '昵称';


--
-- Name: COLUMN t_sys_login_log.event_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.event_type IS '事件类型';


--
-- Name: COLUMN t_sys_login_log.success; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.success IS '是否成功';


--
-- Name: COLUMN t_sys_login_log.fail_reason; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.fail_reason IS '失败原因';


--
-- Name: COLUMN t_sys_login_log.ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.ip IS 'IP地址';


--
-- Name: COLUMN t_sys_login_log.user_agent; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.user_agent IS 'User-Agent';


--
-- Name: COLUMN t_sys_login_log.token_hint; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.token_hint IS 'Token';


--
-- Name: COLUMN t_sys_login_log.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_login_log.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_login_log.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.create_user IS '创建人';


--
-- Name: COLUMN t_sys_login_log.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_login_log.update_user IS '修改人';


--
-- Name: t_sys_menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_menu (
    id bigint NOT NULL,
    number character varying(255),
    name character varying(255) NOT NULL,
    level integer NOT NULL,
    parent_id bigint DEFAULT 0 NOT NULL,
    app_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    path character varying(512),
    component character varying(512),
    icon character varying(255),
    description character varying(512),
    sort integer DEFAULT 99,
    enable_flag boolean DEFAULT true,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_menu; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_menu IS '菜单';


--
-- Name: COLUMN t_sys_menu.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.id IS 'ID';


--
-- Name: COLUMN t_sys_menu.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.number IS '编码';


--
-- Name: COLUMN t_sys_menu.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.name IS '名称';


--
-- Name: COLUMN t_sys_menu.level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.level IS '级别';


--
-- Name: COLUMN t_sys_menu.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.parent_id IS '父级ID';


--
-- Name: COLUMN t_sys_menu.app_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.app_id IS '所属应用ID';


--
-- Name: COLUMN t_sys_menu.permission_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.permission_id IS '权限ID';


--
-- Name: COLUMN t_sys_menu.path; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.path IS '路径';


--
-- Name: COLUMN t_sys_menu.component; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.component IS '组件';


--
-- Name: COLUMN t_sys_menu.icon; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.icon IS '图标';


--
-- Name: COLUMN t_sys_menu.description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.description IS '描述';


--
-- Name: COLUMN t_sys_menu.sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.sort IS '排序';


--
-- Name: COLUMN t_sys_menu.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_menu.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_menu.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_menu.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.create_user IS '创建人';


--
-- Name: COLUMN t_sys_menu.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_menu.update_user IS '修改人';


--
-- Name: t_sys_operate_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_operate_log (
    id bigint NOT NULL,
    biz_name character varying(256),
    success boolean DEFAULT true NOT NULL,
    error_msg text,
    request_method character varying(32),
    request_uri character varying(512),
    ip character varying(64),
    user_agent character varying(1024),
    class_name character varying(256),
    method_name character varying(128),
    duration_ms bigint,
    request_params text,
    response_body text,
    user_id bigint,
    username character varying(128),
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_operate_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_operate_log IS '系统服务-操作日志';


--
-- Name: COLUMN t_sys_operate_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.id IS 'ID';


--
-- Name: COLUMN t_sys_operate_log.biz_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.biz_name IS '业务名称';


--
-- Name: COLUMN t_sys_operate_log.success; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.success IS '是否成功';


--
-- Name: COLUMN t_sys_operate_log.error_msg; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.error_msg IS '错误信息';


--
-- Name: COLUMN t_sys_operate_log.request_method; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.request_method IS '请求方法';


--
-- Name: COLUMN t_sys_operate_log.request_uri; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.request_uri IS '请求URI';


--
-- Name: COLUMN t_sys_operate_log.ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.ip IS 'IP地址';


--
-- Name: COLUMN t_sys_operate_log.user_agent; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.user_agent IS 'User-Agent';


--
-- Name: COLUMN t_sys_operate_log.class_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.class_name IS '类名';


--
-- Name: COLUMN t_sys_operate_log.method_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.method_name IS '方法名';


--
-- Name: COLUMN t_sys_operate_log.duration_ms; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.duration_ms IS '耗时(ms)';


--
-- Name: COLUMN t_sys_operate_log.request_params; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.request_params IS '请求参数';


--
-- Name: COLUMN t_sys_operate_log.response_body; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.response_body IS '响应内容';


--
-- Name: COLUMN t_sys_operate_log.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.user_id IS '用户ID';


--
-- Name: COLUMN t_sys_operate_log.username; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.username IS '用户名';


--
-- Name: COLUMN t_sys_operate_log.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_operate_log.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_operate_log.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.create_user IS '创建人';


--
-- Name: COLUMN t_sys_operate_log.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_operate_log.update_user IS '修改人';


--
-- Name: t_sys_org; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_org (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    parent_id bigint DEFAULT 0 NOT NULL,
    sort integer DEFAULT 99,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone DEFAULT now(),
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_org; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_org IS '组织';


--
-- Name: COLUMN t_sys_org.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.id IS 'ID';


--
-- Name: COLUMN t_sys_org.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.name IS '名称';


--
-- Name: COLUMN t_sys_org.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.number IS '编码';


--
-- Name: COLUMN t_sys_org.parent_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.parent_id IS '父级ID';


--
-- Name: COLUMN t_sys_org.sort; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.sort IS '排序';


--
-- Name: COLUMN t_sys_org.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_org.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_org.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.create_user IS '创建人';


--
-- Name: COLUMN t_sys_org.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_org.update_user IS '修改人';


--
-- Name: t_sys_param; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_param (
    id bigint NOT NULL,
    number character varying(100) NOT NULL,
    name character varying(200) NOT NULL,
    value text,
    remark character varying(500),
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint,
    is_system boolean DEFAULT false NOT NULL
);


--
-- Name: TABLE t_sys_param; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_param IS '系统参数';


--
-- Name: COLUMN t_sys_param.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.id IS 'ID';


--
-- Name: COLUMN t_sys_param.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.number IS '编码';


--
-- Name: COLUMN t_sys_param.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.name IS '名称';


--
-- Name: COLUMN t_sys_param.value; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.value IS '参数值';


--
-- Name: COLUMN t_sys_param.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.remark IS '备注';


--
-- Name: COLUMN t_sys_param.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_param.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_param.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.create_user IS '创建人';


--
-- Name: COLUMN t_sys_param.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.update_user IS '修改人';


--
-- Name: COLUMN t_sys_param.is_system; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_param.is_system IS '是否系统内置';


--
-- Name: t_sys_permission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_permission (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    app_id bigint NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_permission; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_permission IS '权限';


--
-- Name: COLUMN t_sys_permission.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.id IS 'ID';


--
-- Name: COLUMN t_sys_permission.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.name IS '名称';


--
-- Name: COLUMN t_sys_permission.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.number IS '编码';


--
-- Name: COLUMN t_sys_permission.app_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.app_id IS '所属应用ID';


--
-- Name: COLUMN t_sys_permission.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_permission.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_permission.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.create_user IS '创建人';


--
-- Name: COLUMN t_sys_permission.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_permission.update_user IS '修改人';


--
-- Name: t_sys_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_role (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_role IS '角色';


--
-- Name: COLUMN t_sys_role.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.id IS 'ID';


--
-- Name: COLUMN t_sys_role.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.name IS '名称';


--
-- Name: COLUMN t_sys_role.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.number IS '编码';


--
-- Name: COLUMN t_sys_role.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_role.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_role.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.create_user IS '创建人';


--
-- Name: COLUMN t_sys_role.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role.update_user IS '修改人';


--
-- Name: t_sys_role_perms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_role_perms (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_role_perms; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_role_perms IS '角色拥有的权限';


--
-- Name: COLUMN t_sys_role_perms.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.id IS 'ID';


--
-- Name: COLUMN t_sys_role_perms.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.role_id IS '角色ID';


--
-- Name: COLUMN t_sys_role_perms.permission_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.permission_id IS '权限ID';


--
-- Name: COLUMN t_sys_role_perms.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_role_perms.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_role_perms.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.create_user IS '创建人';


--
-- Name: COLUMN t_sys_role_perms.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_role_perms.update_user IS '修改人';


--
-- Name: t_sys_script; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_script (
    id bigint NOT NULL,
    number character varying(100) NOT NULL,
    name character varying(200) NOT NULL,
    content text NOT NULL,
    remark character varying(500),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_script; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_script IS '脚本管理';


--
-- Name: COLUMN t_sys_script.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.id IS 'ID';


--
-- Name: COLUMN t_sys_script.number; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.number IS '编码';


--
-- Name: COLUMN t_sys_script.name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.name IS '名称';


--
-- Name: COLUMN t_sys_script.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.content IS '脚本内容';


--
-- Name: COLUMN t_sys_script.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.remark IS '备注';


--
-- Name: COLUMN t_sys_script.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_script.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_script.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.create_user IS '创建人';


--
-- Name: COLUMN t_sys_script.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_script.update_user IS '修改人';


--
-- Name: t_sys_sql_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_sql_log (
    id bigint NOT NULL,
    sql_text text NOT NULL,
    execute_duration integer DEFAULT 0,
    result_type character varying(50) NOT NULL,
    row_count integer DEFAULT 0,
    error_message text,
    create_name character varying(200),
    create_ip character varying(100),
    remark character varying(500),
    create_user bigint,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_user bigint,
    update_time timestamp without time zone
);


--
-- Name: TABLE t_sys_sql_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_sql_log IS 'SQL执行日志';


--
-- Name: COLUMN t_sys_sql_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.id IS 'ID';


--
-- Name: COLUMN t_sys_sql_log.sql_text; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.sql_text IS 'SQL语句';


--
-- Name: COLUMN t_sys_sql_log.execute_duration; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.execute_duration IS '执行耗时(ms)';


--
-- Name: COLUMN t_sys_sql_log.result_type; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.result_type IS '结果类型';


--
-- Name: COLUMN t_sys_sql_log.row_count; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.row_count IS '影响行数';


--
-- Name: COLUMN t_sys_sql_log.error_message; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.error_message IS '错误信息';


--
-- Name: COLUMN t_sys_sql_log.create_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.create_name IS '操作人';


--
-- Name: COLUMN t_sys_sql_log.create_ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.create_ip IS '操作IP';


--
-- Name: COLUMN t_sys_sql_log.remark; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.remark IS '备注';


--
-- Name: COLUMN t_sys_sql_log.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.create_user IS '创建人';


--
-- Name: COLUMN t_sys_sql_log.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_sql_log.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.update_user IS '修改人';


--
-- Name: COLUMN t_sys_sql_log.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_sql_log.update_time IS '更新时间';


--
-- Name: t_sys_ui_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_ui_config (
    id bigint NOT NULL,
    page_title character varying(200),
    login_banner character varying(500),
    login_logo character varying(500),
    system_name character varying(200),
    header_logo character varying(500),
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_ui_config; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_ui_config IS '界面配置';


--
-- Name: COLUMN t_sys_ui_config.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.id IS 'ID';


--
-- Name: COLUMN t_sys_ui_config.page_title; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.page_title IS '页面标题';


--
-- Name: COLUMN t_sys_ui_config.login_banner; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.login_banner IS '登录页Banner';


--
-- Name: COLUMN t_sys_ui_config.login_logo; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.login_logo IS '登录页Logo';


--
-- Name: COLUMN t_sys_ui_config.system_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.system_name IS '系统名称';


--
-- Name: COLUMN t_sys_ui_config.header_logo; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.header_logo IS '顶部Logo';


--
-- Name: COLUMN t_sys_ui_config.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_ui_config.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_ui_config.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.create_user IS '创建人';


--
-- Name: COLUMN t_sys_ui_config.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_ui_config.update_user IS '修改人';


--
-- Name: t_sys_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_user (
    id bigint NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    nickname character varying(255),
    avatar character varying(255),
    email character varying(255),
    phone character varying(255),
    theme_color character varying(255),
    enable_flag boolean,
    create_time timestamp without time zone DEFAULT now(),
    create_user bigint,
    update_time timestamp without time zone DEFAULT now(),
    update_user bigint
);


--
-- Name: TABLE t_sys_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_user IS '用户';


--
-- Name: COLUMN t_sys_user.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.id IS 'ID';


--
-- Name: COLUMN t_sys_user.username; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.username IS '用户名';


--
-- Name: COLUMN t_sys_user.password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.password IS '密码';


--
-- Name: COLUMN t_sys_user.nickname; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.nickname IS '昵称';


--
-- Name: COLUMN t_sys_user.avatar; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.avatar IS '头像';


--
-- Name: COLUMN t_sys_user.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.email IS '邮箱';


--
-- Name: COLUMN t_sys_user.phone; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.phone IS '手机号';


--
-- Name: COLUMN t_sys_user.theme_color; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.theme_color IS '主题色';


--
-- Name: COLUMN t_sys_user.enable_flag; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.enable_flag IS '启用标识';


--
-- Name: COLUMN t_sys_user.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_user.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.create_user IS '创建人';


--
-- Name: COLUMN t_sys_user.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_user.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user.update_user IS '修改人';


--
-- Name: t_sys_user_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_sys_user_role (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    org_id bigint NOT NULL,
    role_id bigint NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint
);


--
-- Name: TABLE t_sys_user_role; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_sys_user_role IS '用户在组织下的角色';


--
-- Name: COLUMN t_sys_user_role.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.id IS 'ID';


--
-- Name: COLUMN t_sys_user_role.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.user_id IS '用户ID';


--
-- Name: COLUMN t_sys_user_role.org_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.org_id IS '组织ID';


--
-- Name: COLUMN t_sys_user_role.role_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.role_id IS '角色ID';


--
-- Name: COLUMN t_sys_user_role.create_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.create_time IS '创建时间';


--
-- Name: COLUMN t_sys_user_role.update_time; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.update_time IS '更新时间';


--
-- Name: COLUMN t_sys_user_role.create_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.create_user IS '创建人';


--
-- Name: COLUMN t_sys_user_role.update_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_sys_user_role.update_user IS '修改人';


--
-- Name: qrtz_blob_triggers qrtz_blob_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_calendars qrtz_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);


--
-- Name: qrtz_cron_triggers qrtz_cron_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_fired_triggers qrtz_fired_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);


--
-- Name: qrtz_job_details qrtz_job_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);


--
-- Name: qrtz_locks qrtz_locks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);


--
-- Name: qrtz_paused_trigger_grps qrtz_paused_trigger_grps_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);


--
-- Name: qrtz_scheduler_state qrtz_scheduler_state_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);


--
-- Name: qrtz_simple_triggers qrtz_simple_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simprop_triggers qrtz_simprop_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_triggers qrtz_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: t_sys_app t_sys_app_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_app
    ADD CONSTRAINT t_sys_app_pkey PRIMARY KEY (id);


--
-- Name: t_sys_attachment t_sys_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_attachment
    ADD CONSTRAINT t_sys_attachment_pkey PRIMARY KEY (id);


--
-- Name: t_sys_biz_attachment t_sys_biz_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_biz_attachment
    ADD CONSTRAINT t_sys_biz_attachment_pkey PRIMARY KEY (id);


--
-- Name: t_sys_cloud t_sys_cloud_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_cloud
    ADD CONSTRAINT t_sys_cloud_pkey PRIMARY KEY (id);


--
-- Name: t_sys_basic_data_item t_sys_dict_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_basic_data_item
    ADD CONSTRAINT t_sys_dict_item_pkey PRIMARY KEY (id);


--
-- Name: t_sys_basic_data t_sys_dict_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_basic_data
    ADD CONSTRAINT t_sys_dict_type_pkey PRIMARY KEY (id);


--
-- Name: t_sys_file_config t_sys_file_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_file_config
    ADD CONSTRAINT t_sys_file_config_pkey PRIMARY KEY (id);


--
-- Name: t_sys_job_log t_sys_job_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_job_log
    ADD CONSTRAINT t_sys_job_log_pkey PRIMARY KEY (id);


--
-- Name: t_sys_job t_sys_job_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_job
    ADD CONSTRAINT t_sys_job_pkey PRIMARY KEY (id);


--
-- Name: t_sys_login_log t_sys_login_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_login_log
    ADD CONSTRAINT t_sys_login_log_pkey PRIMARY KEY (id);


--
-- Name: t_sys_menu t_sys_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_menu
    ADD CONSTRAINT t_sys_menu_pkey PRIMARY KEY (id);


--
-- Name: t_sys_operate_log t_sys_operate_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_operate_log
    ADD CONSTRAINT t_sys_operate_log_pkey PRIMARY KEY (id);


--
-- Name: t_sys_org t_sys_org_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_org
    ADD CONSTRAINT t_sys_org_pkey PRIMARY KEY (id);


--
-- Name: t_sys_param t_sys_param_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_param
    ADD CONSTRAINT t_sys_param_pkey PRIMARY KEY (id);


--
-- Name: t_sys_permission t_sys_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_permission
    ADD CONSTRAINT t_sys_permission_pkey PRIMARY KEY (id);


--
-- Name: t_sys_role_perms t_sys_role_perms_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_role_perms
    ADD CONSTRAINT t_sys_role_perms_pkey PRIMARY KEY (id);


--
-- Name: t_sys_role t_sys_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_role
    ADD CONSTRAINT t_sys_role_pkey PRIMARY KEY (id);


--
-- Name: t_sys_script t_sys_script_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_script
    ADD CONSTRAINT t_sys_script_pkey PRIMARY KEY (id);


--
-- Name: t_sys_sql_log t_sys_sql_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_sql_log
    ADD CONSTRAINT t_sys_sql_log_pkey PRIMARY KEY (id);


--
-- Name: t_sys_ui_config t_sys_ui_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_ui_config
    ADD CONSTRAINT t_sys_ui_config_pkey PRIMARY KEY (id);


--
-- Name: t_sys_user t_sys_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user
    ADD CONSTRAINT t_sys_user_pkey PRIMARY KEY (id);


--
-- Name: t_sys_user_role t_sys_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user_role
    ADD CONSTRAINT t_sys_user_role_pkey PRIMARY KEY (id);


--
-- Name: t_sys_basic_data uk_basic_data_number; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_basic_data
    ADD CONSTRAINT uk_basic_data_number UNIQUE (number);


--
-- Name: t_sys_role_perms uk_sys_rp_role_perm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_role_perms
    ADD CONSTRAINT uk_sys_rp_role_perm UNIQUE (role_id, permission_id);


--
-- Name: t_sys_user_role uk_sys_ur_user_org_role; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user_role
    ADD CONSTRAINT uk_sys_ur_user_org_role UNIQUE (user_id, org_id, role_id);


--
-- Name: idx_basic_data_item_type_number; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_basic_data_item_type_number ON public.t_sys_basic_data_item USING btree (type_number);


--
-- Name: idx_biz_attachment_att; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_biz_attachment_att ON public.t_sys_biz_attachment USING btree (attachment_id);


--
-- Name: idx_biz_attachment_biz; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_biz_attachment_biz ON public.t_sys_biz_attachment USING btree (biz_type, biz_id);


--
-- Name: idx_qrtz_ft_inst_job_req_rcvry; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON public.qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);


--
-- Name: idx_qrtz_ft_j_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_j_g ON public.qrtz_fired_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_ft_jg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_jg ON public.qrtz_fired_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_ft_t_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_t_g ON public.qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);


--
-- Name: idx_qrtz_ft_tg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_tg ON public.qrtz_fired_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_ft_trig_inst_name ON public.qrtz_fired_triggers USING btree (sched_name, instance_name);


--
-- Name: idx_qrtz_j_grp; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_grp ON public.qrtz_job_details USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_j_req_recovery ON public.qrtz_job_details USING btree (sched_name, requests_recovery);


--
-- Name: idx_qrtz_t_c; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_c ON public.qrtz_triggers USING btree (sched_name, calendar_name);


--
-- Name: idx_qrtz_t_g; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_g ON public.qrtz_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_t_j; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_j ON public.qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_t_jg; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_jg ON public.qrtz_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_t_n_g_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_n_g_state ON public.qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_n_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_n_state ON public.qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_next_fire_time ON public.qrtz_triggers USING btree (sched_name, next_fire_time);


--
-- Name: idx_qrtz_t_nft_misfire; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_misfire ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st ON public.qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st_misfire; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st_misfire ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);


--
-- Name: idx_qrtz_t_nft_st_misfire_grp; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON public.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_state; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_qrtz_t_state ON public.qrtz_triggers USING btree (sched_name, trigger_state);


--
-- Name: idx_sys_app_cloud; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_app_cloud ON public.t_sys_app USING btree (cloud_id);


--
-- Name: idx_sys_app_num; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_app_num ON public.t_sys_app USING btree (number);


--
-- Name: idx_sys_cloud_num; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_cloud_num ON public.t_sys_cloud USING btree (number);


--
-- Name: idx_sys_dict_item_number; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_dict_item_number ON public.t_sys_basic_data_item USING btree (type_number);


--
-- Name: idx_sys_dict_type_number; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_dict_type_number ON public.t_sys_basic_data USING btree (number);


--
-- Name: idx_sys_job_log_job_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_job_log_job_id ON public.t_sys_job_log USING btree (job_id);


--
-- Name: idx_sys_job_log_start_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_job_log_start_time ON public.t_sys_job_log USING btree (start_time);


--
-- Name: idx_sys_job_log_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_job_log_status ON public.t_sys_job_log USING btree (status);


--
-- Name: idx_sys_job_name_group; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_job_name_group ON public.t_sys_job USING btree (job_name, job_group);


--
-- Name: idx_sys_job_number; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_job_number ON public.t_sys_job USING btree (number);


--
-- Name: idx_sys_login_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_login_name ON public.t_sys_login_log USING btree (username);


--
-- Name: idx_sys_login_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_login_time ON public.t_sys_login_log USING btree (create_time);


--
-- Name: idx_sys_login_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_login_user ON public.t_sys_login_log USING btree (user_id);


--
-- Name: idx_sys_menu_app; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_menu_app ON public.t_sys_menu USING btree (app_id);


--
-- Name: idx_sys_menu_perm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_menu_perm ON public.t_sys_menu USING btree (permission_id);


--
-- Name: idx_sys_operate_time; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_operate_time ON public.t_sys_operate_log USING btree (create_time);


--
-- Name: idx_sys_operate_uri; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_operate_uri ON public.t_sys_operate_log USING btree (request_uri);


--
-- Name: idx_sys_operate_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_operate_user ON public.t_sys_operate_log USING btree (user_id);


--
-- Name: idx_sys_org_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_org_parent ON public.t_sys_org USING btree (parent_id);


--
-- Name: idx_sys_param_number; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_param_number ON public.t_sys_param USING btree (number);


--
-- Name: idx_sys_perm_app; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_perm_app ON public.t_sys_permission USING btree (app_id);


--
-- Name: idx_sys_perm_number; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_perm_number ON public.t_sys_permission USING btree (number);


--
-- Name: idx_sys_role_number; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_sys_role_number ON public.t_sys_role USING btree (number);


--
-- Name: idx_sys_rp_perm; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_rp_perm ON public.t_sys_role_perms USING btree (permission_id);


--
-- Name: idx_sys_rp_role; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_rp_role ON public.t_sys_role_perms USING btree (role_id);


--
-- Name: idx_sys_ur_org; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_ur_org ON public.t_sys_user_role USING btree (org_id);


--
-- Name: idx_sys_ur_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_ur_user ON public.t_sys_user_role USING btree (user_id);


--
-- Name: idx_sys_user_username; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_sys_user_username ON public.t_sys_user USING btree (username);


--
-- Name: t_sys_app fk_sys_app_cloud; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_app
    ADD CONSTRAINT fk_sys_app_cloud FOREIGN KEY (cloud_id) REFERENCES public.t_sys_cloud(id);


--
-- Name: t_sys_menu fk_sys_menu_app; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_menu
    ADD CONSTRAINT fk_sys_menu_app FOREIGN KEY (app_id) REFERENCES public.t_sys_app(id);


--
-- Name: t_sys_menu fk_sys_menu_perm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_menu
    ADD CONSTRAINT fk_sys_menu_perm FOREIGN KEY (permission_id) REFERENCES public.t_sys_permission(id);


--
-- Name: t_sys_permission fk_sys_perm_app; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_permission
    ADD CONSTRAINT fk_sys_perm_app FOREIGN KEY (app_id) REFERENCES public.t_sys_app(id);


--
-- Name: t_sys_role_perms fk_sys_rp_perm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_role_perms
    ADD CONSTRAINT fk_sys_rp_perm FOREIGN KEY (permission_id) REFERENCES public.t_sys_permission(id);


--
-- Name: t_sys_role_perms fk_sys_rp_role; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_role_perms
    ADD CONSTRAINT fk_sys_rp_role FOREIGN KEY (role_id) REFERENCES public.t_sys_role(id);


--
-- Name: t_sys_user_role fk_sys_ur_org; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user_role
    ADD CONSTRAINT fk_sys_ur_org FOREIGN KEY (org_id) REFERENCES public.t_sys_org(id);


--
-- Name: t_sys_user_role fk_sys_ur_role; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user_role
    ADD CONSTRAINT fk_sys_ur_role FOREIGN KEY (role_id) REFERENCES public.t_sys_role(id);


--
-- Name: t_sys_user_role fk_sys_ur_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_sys_user_role
    ADD CONSTRAINT fk_sys_ur_user FOREIGN KEY (user_id) REFERENCES public.t_sys_user(id);


--
-- Name: qrtz_blob_triggers qrtz_blob_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_cron_triggers qrtz_cron_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simple_triggers qrtz_simple_triggers_sched_name_trigger_name_trigger_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simprop_triggers qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES public.qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_triggers qrtz_triggers_sched_name_job_name_job_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_sched_name_job_name_job_group_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES public.qrtz_job_details(sched_name, job_name, job_group);


--
-- PostgreSQL database dump complete
--

\unrestrict j6ABwsHpf6emr5XDxVbbVNCcZD3mkaP8FXAVf9VVlV8H1DOWvaLpzDmq0zhLHGE

