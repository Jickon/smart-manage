-- 可独立移除的供应链采购申请模块：云、应用、权限、菜单与业务表集中在同一版本。

INSERT INTO public.t_sys_cloud
    (id, name, number, seq, enabled, create_time, version)
VALUES
    (430000000000000001, '供应链', 'scm', 10, true, CURRENT_TIMESTAMP, 0);

INSERT INTO public.t_sys_app
    (id, name, number, icon, icon_color, seq, description, cloud_id, enabled, create_time, version)
VALUES
    (430000000000000002, '采购管理', 'procurement', 'ShoppingCartOutlined', '#1677ff', 1,
     '采购业务管理', 430000000000000001, true, CURRENT_TIMESTAMP, 0);

INSERT INTO public.t_sys_permission
    (id, name, number, app_id, create_time, version)
VALUES
    (430000000000000010, '采购申请', 'scm:procurement:purchase-requisition', 430000000000000002, CURRENT_TIMESTAMP, 0),
    (430000000000000011, '采购申请-列表', 'scm:procurement:purchase-requisition:listPage', 430000000000000002, CURRENT_TIMESTAMP, 0),
    (430000000000000012, '采购申请-详情', 'scm:procurement:purchase-requisition:detail', 430000000000000002, CURRENT_TIMESTAMP, 0),
    (430000000000000013, '采购申请-保存', 'scm:procurement:purchase-requisition:save', 430000000000000002, CURRENT_TIMESTAMP, 0),
    (430000000000000014, '采购申请-提交', 'scm:procurement:purchase-requisition:submit', 430000000000000002, CURRENT_TIMESTAMP, 0),
    (430000000000000015, '采购申请-删除', 'scm:procurement:purchase-requisition:delete', 430000000000000002, CURRENT_TIMESTAMP, 0);

INSERT INTO public.t_sys_menu
    (id, number, name, level, parent_id, app_id, permission_id, path, component, icon,
     description, sort, enabled, create_time)
VALUES
    (430000000000000020, 'purchase_requisition', '采购申请', 3, 0, 430000000000000002,
     430000000000000010, '/scm/procurement/purchase-requisition',
     'scm/procurement/purchase-requisition', 'FileAddOutlined', '采购申请单', 1, true, CURRENT_TIMESTAMP);

CREATE TABLE public.t_scm_purchase_requisition (
    id bigint NOT NULL,
    number character varying(64) NOT NULL,
    subject character varying(255) NOT NULL,
    apply_org_id bigint NOT NULL,
    applicant_id bigint NOT NULL,
    apply_date date NOT NULL,
    required_date date,
    reason character varying(1000),
    bill_status character(1) DEFAULT 'A' NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint,
    CONSTRAINT pk_scm_purchase_requisition PRIMARY KEY (id),
    CONSTRAINT uk_scm_purchase_requisition_number UNIQUE (number),
    CONSTRAINT ck_scm_purchase_requisition_status CHECK (bill_status IN ('A', 'B', 'C', 'D')),
    CONSTRAINT fk_scm_purchase_requisition_org FOREIGN KEY (apply_org_id) REFERENCES public.t_sys_org(id),
    CONSTRAINT fk_scm_purchase_requisition_applicant FOREIGN KEY (applicant_id) REFERENCES public.t_sys_user(id)
);

COMMENT ON TABLE public.t_scm_purchase_requisition IS '采购申请';
COMMENT ON COLUMN public.t_scm_purchase_requisition.id IS 'ID';
COMMENT ON COLUMN public.t_scm_purchase_requisition.number IS '编码';
COMMENT ON COLUMN public.t_scm_purchase_requisition.subject IS '主题';
COMMENT ON COLUMN public.t_scm_purchase_requisition.apply_org_id IS '申请组织ID';
COMMENT ON COLUMN public.t_scm_purchase_requisition.applicant_id IS '申请人ID';
COMMENT ON COLUMN public.t_scm_purchase_requisition.apply_date IS '申请日期';
COMMENT ON COLUMN public.t_scm_purchase_requisition.required_date IS '需求日期';
COMMENT ON COLUMN public.t_scm_purchase_requisition.reason IS '申请原因';
COMMENT ON COLUMN public.t_scm_purchase_requisition.bill_status IS '单据状态：A暂存，B已提交，C审核通过，D已关闭';
COMMENT ON COLUMN public.t_scm_purchase_requisition.version IS '乐观锁版本号';
COMMENT ON COLUMN public.t_scm_purchase_requisition.create_time IS '创建时间';
COMMENT ON COLUMN public.t_scm_purchase_requisition.update_time IS '更新时间';
COMMENT ON COLUMN public.t_scm_purchase_requisition.create_user IS '创建人';
COMMENT ON COLUMN public.t_scm_purchase_requisition.update_user IS '修改人';

CREATE TABLE public.t_scm_purchase_requisition_entry (
    id bigint NOT NULL,
    parent_id bigint NOT NULL,
    material_name character varying(255) NOT NULL,
    specification character varying(255),
    unit character varying(32) NOT NULL,
    quantity numeric(19, 6) NOT NULL,
    required_date date,
    remark character varying(500),
    sort integer DEFAULT 99 NOT NULL,
    create_time timestamp without time zone DEFAULT now(),
    update_time timestamp without time zone,
    create_user bigint,
    update_user bigint,
    CONSTRAINT pk_scm_purchase_requisition_entry PRIMARY KEY (id),
    CONSTRAINT ck_scm_purchase_requisition_entry_quantity CHECK (quantity > 0),
    CONSTRAINT fk_scm_purchase_requisition_entry_parent FOREIGN KEY (parent_id)
        REFERENCES public.t_scm_purchase_requisition(id)
);

CREATE INDEX idx_scm_purchase_requisition_entry_parent_id
    ON public.t_scm_purchase_requisition_entry(parent_id);

COMMENT ON TABLE public.t_scm_purchase_requisition_entry IS '采购申请明细';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.id IS 'ID';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.parent_id IS '父级ID';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.material_name IS '物料名称';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.specification IS '规格型号';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.unit IS '单位';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.quantity IS '数量';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.required_date IS '需求日期';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.remark IS '备注';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.sort IS '排序';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.create_time IS '创建时间';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.update_time IS '更新时间';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.create_user IS '创建人';
COMMENT ON COLUMN public.t_scm_purchase_requisition_entry.update_user IS '修改人';
