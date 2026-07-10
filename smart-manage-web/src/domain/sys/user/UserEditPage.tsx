import { useMemo, useState } from 'react';
import { message, Collapse, Checkbox, Spin, Empty } from 'antd';
import { useQuery } from '@tanstack/react-query';
import EditPage from '@/domain/common/page/EditPage';
import { OperationType } from '@/domain/common/page/types';
import type { EditField } from '@/domain/common/page/EditPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { userApi } from './api';
import { roleApi } from '@/domain/sys/role/api';
import type { RoleListAllVO } from '@/domain/sys/role/types';
import type { PageComponentProps } from '@/domain/common/page/types';

/** 从 query data 派生初始勾选集合（无 useEffect） */
function deriveCheckedRoleIds(roleIds: string[] | undefined): Set<string> {
  return new Set(roleIds ?? []);
}

/** 用户编辑页 — 全页 Tab，包含角色分配面板 */
const UserEditPage = (props: PageComponentProps) => {
  const { appNumber, tabKey, operationType, billId } = props;
  const isAddNew = operationType === OperationType.ADDNEW;
  const replaceContentTab = useWorkbenchStore((s) => s.replaceContentTab);
  const activateContentTab = useWorkbenchStore((s) => s.activateContentTab);

  // 详情查询（仅编辑模式）
  const detailQuery = useQuery({
    queryKey: ['user-detail', billId],
    queryFn: () => userApi.detail(billId!),
    enabled: !!billId,
  });

  // 全量角色（用于分配面板）
  const allRolesQuery = useQuery({
    queryKey: ['role-list-all'],
    queryFn: () => roleApi.listAll(),
    staleTime: 5 * 60 * 1000,
  });

  // 用户已有角色（按当前组织，仅编辑模式）
  const detail = detailQuery.data;

  // 角色勾选状态：query 派生 + 本地修改
  const checkedRoleIds = deriveCheckedRoleIds(detail?.roleIds);
  const [localCheckedRoleIds, setLocalCheckedRoleIds] = useState<Set<string> | null>(null);
  const displayCheckedIds = useMemo(() => {
    if (localCheckedRoleIds !== null) return localCheckedRoleIds;
    return checkedRoleIds;
  }, [localCheckedRoleIds, checkedRoleIds]);

  // 字段定义（password 类型由公共 EditPage 渲染 Input.Password）
  const fields: EditField[] = useMemo(() => {
    const base: EditField[] = [
      {
        label: '用户名',
        dataIndex: 'username',
        type: 'text',
        rules: [{ required: true, message: '用户名不能为空' }],
      },
      {
        label: '密码',
        dataIndex: 'password',
        type: 'password',
        placeholder: isAddNew ? '请输入密码' : '留空则不修改',
        rules: isAddNew ? [{ required: true, message: '密码不能为空' }] : [],
      },
      { label: '昵称', dataIndex: 'nickname', type: 'text' },
      { label: '邮箱', dataIndex: 'email', type: 'text' },
      { label: '手机号', dataIndex: 'phone', type: 'text' },
      { label: '头像URL', dataIndex: 'avatar', type: 'text' },
      { label: '主题色', dataIndex: 'themeColor', type: 'text', placeholder: '如 #1677ff' },
      { label: '启用', dataIndex: 'enableFlag', type: 'switch' },
      { label: '创建时间', dataIndex: 'createTime', type: 'readonly' },
      { label: '更新时间', dataIndex: 'updateTime', type: 'readonly' },
    ];
    return base;
  }, [isAddNew]);

  // Form 初始值（不含密码）
  const initialValues = useMemo(() => {
    if (!detail) return {};
    return {
      username: detail.username ?? '',
      nickname: detail.nickname ?? '',
      email: detail.email ?? '',
      phone: detail.phone ?? '',
      avatar: detail.avatar ?? '',
      themeColor: detail.themeColor ?? '',
      enableFlag: detail.enableFlag ?? true,
      createTime: detail.createTime ?? '',
      updateTime: detail.updateTime ?? '',
    };
  }, [detail]);

  const handleRoleChange = (vals: string[]) => {
    setLocalCheckedRoleIds(new Set(vals));
  };

  const handleSave = async (values: Record<string, unknown>) => {
    const username = (values.username as string).trim();
    const savedId = await userApi.save({
      id: billId ?? undefined,
      mutex: detail?.mutex,
      username,
      password: (values.password as string) || undefined,
      nickname: (values.nickname as string) ?? undefined,
      email: (values.email as string) ?? undefined,
      phone: (values.phone as string) ?? undefined,
      avatar: (values.avatar as string) ?? undefined,
      themeColor: (values.themeColor as string) ?? undefined,
      enableFlag: values.enableFlag != null ? Boolean(values.enableFlag) : undefined,
      roleIds: [...displayCheckedIds],
    });

    if (isAddNew && tabKey !== `bill:${props.componentKey}:${savedId}`) {
      replaceContentTab(appNumber, tabKey, {
        key: `bill:${props.componentKey}:${savedId}`,
        label: username,
        closable: true,
        componentKey: props.componentKey,
        pageType: 'EDIT',
        operationType: OperationType.EDIT,
        billId: String(savedId),
      });
      activateContentTab(appNumber, `bill:${props.componentKey}:${savedId}`);
    }
    message.success(isAddNew ? '新增成功' : '保存成功');
  };

  const loading = detailQuery.isLoading || allRolesQuery.isLoading;
  const error = (detailQuery.error || allRolesQuery.error) as Error | null;

  const roleList: RoleListAllVO[] = allRolesQuery.data ?? [];

  return (
    <EditPage
      title="用户管理"
      fields={fields}
      initialValues={initialValues}
      operationType={operationType ?? OperationType.EDIT}
      loading={loading}
      error={error}
      onRetry={() => {
        detailQuery.refetch();
        allRolesQuery.refetch();
      }}
      onSave={handleSave}
      onExit={() => {
        useWorkbenchStore.getState().removeContentTab(appNumber, tabKey);
      }}
      headerExtra={
        <div className="sm-edit-body sm-edit-header-extra">
          <Collapse
            className="sm-edit-collapse"
            defaultActiveKey={['roles']}
            items={[
              {
                key: 'roles',
                label: '角色分配',
                children: (
                  <Spin spinning={allRolesQuery.isLoading}>
                    {roleList.length === 0 ? (
                      <Empty description="暂无角色数据" />
                    ) : (
                      <Checkbox.Group value={[...displayCheckedIds]} onChange={handleRoleChange}>
                        <div className="sm-edit-checkbox-column">
                          {roleList.map((role) => (
                            <Checkbox key={role.id} value={role.id}>
                              {role.number} — {role.name}
                            </Checkbox>
                          ))}
                        </div>
                      </Checkbox.Group>
                    )}
                  </Spin>
                ),
              },
            ]}
          />
        </div>
      }
    />
  );
};

export default UserEditPage;
