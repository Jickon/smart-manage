import { useMemo, useState } from 'react';
import { message, Collapse, Checkbox, Spin, Empty } from 'antd';
import { useQuery } from '@tanstack/react-query';
import EditPage from '@/domain/common/page/EditPage';
import { OperationType } from '@/domain/common/page/types';
import type { EditField } from '@/domain/common/page/EditPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { roleApi, rolePermsApi } from './api';
import { permissionApi } from '@/domain/sys/permission/api';
import type { PermissionListAllVO } from '@/domain/sys/permission/types';
import type { PageComponentProps } from '@/domain/common/page/types';

/** 角色编辑字段定义 */
const fields: EditField[] = [
  {
    label: '编码',
    dataIndex: 'number',
    type: 'text',
    rules: [{ required: true, message: '编码不能为空' }],
  },
  {
    label: '名称',
    dataIndex: 'name',
    type: 'text',
    rules: [{ required: true, message: '名称不能为空' }],
  },
];

/** 权限分组 */
interface GroupedPermissions {
  appId: string;
  appName: string;
  permissions: PermissionListAllVO[];
}

/**
 * 从 query data 派生初始勾选集合（不通过 useEffect setState）。
 * 若 data 尚未就绪返回空 Set，组件在渲染时直接派生最终值。
 */
function deriveCheckedPermIds(data: { permissionId: string }[] | undefined): Set<string> {
  if (!data) return new Set<string>();
  return new Set(data.map((p) => p.permissionId));
}

/** 角色编辑页 — 全页 Tab，包含权限分配面板 */
const RoleEditPage = (props: PageComponentProps) => {
  const { appNumber, tabKey, operationType, billId } = props;
  const isAddNew = operationType === OperationType.ADDNEW;
  const replaceContentTab = useWorkbenchStore((s) => s.replaceContentTab);
  const activateContentTab = useWorkbenchStore((s) => s.activateContentTab);

  // 详情查询（仅编辑模式）
  const detailQuery = useQuery({
    queryKey: ['role-detail', billId],
    queryFn: () => roleApi.detail(billId!),
    enabled: !!billId,
  });

  // 全量权限（用于分配面板）
  const allPermsQuery = useQuery({
    queryKey: ['permission-list-all'],
    queryFn: () => permissionApi.listAll(),
    staleTime: 5 * 60 * 1000,
  });

  // 角色已有权限（仅编辑模式）
  const rolePermsQuery = useQuery({
    queryKey: ['role-perms', billId],
    queryFn: () => rolePermsApi.listByRole(billId!),
    enabled: !!billId,
  });

  const detail = detailQuery.data;

  // 已勾选权限 ID 从 query data 派生（符合 React 规则，不用 useEffect setState）
  const checkedPermIds = deriveCheckedPermIds(rolePermsQuery.data);
  // 本地修改用 state 管理，与 query 派生值做合并
  const [localCheckedPermIds, setLocalCheckedPermIds] = useState<Set<string> | null>(null);

  // 展示用的勾选集合：本地修改优先，否则用查询派生
  const displayCheckedIds = useMemo(() => {
    if (localCheckedPermIds !== null) return localCheckedPermIds;
    return checkedPermIds;
  }, [localCheckedPermIds, checkedPermIds]);

  // Form 初始值
  const initialValues = useMemo(() => {
    if (!detail) return {};
    return {
      number: detail.number ?? '',
      name: detail.name ?? '',
    };
  }, [detail]);

  // 权限按 appId 分组
  const groupedPerms = useMemo((): GroupedPermissions[] => {
    if (!allPermsQuery.data) return [];
    const map = new Map<string, GroupedPermissions>();
    for (const p of allPermsQuery.data) {
      const key = p.appId;
      if (!map.has(key)) {
        map.set(key, { appId: key, appName: `应用 ${key}`, permissions: [] });
      }
      map.get(key)!.permissions.push(p);
    }
    return [...map.values()];
  }, [allPermsQuery.data]);

  const handlePermChange = (vals: string[]) => {
    setLocalCheckedPermIds(new Set(vals));
  };

  const handleSave = async (values: Record<string, unknown>) => {
    const name = (values.name as string).trim();
    const number = (values.number as string).trim();
    const savedId = await roleApi.saveWithPerms({
      id: billId ?? undefined,
      name,
      number,
      permissionIds: [...displayCheckedIds],
    });
    if (isAddNew && tabKey !== `bill:${props.componentKey}:${savedId}`) {
      replaceContentTab(appNumber, tabKey, {
        key: `bill:${props.componentKey}:${savedId}`,
        label: name,
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

  const loading = detailQuery.isLoading || allPermsQuery.isLoading || rolePermsQuery.isLoading;
  const error = (detailQuery.error || allPermsQuery.error || rolePermsQuery.error) as Error | null;

  return (
    <EditPage
      title="角色管理"
      fields={fields}
      initialValues={initialValues}
      operationType={operationType ?? OperationType.EDIT}
      loading={loading}
      error={error}
      onRetry={() => {
        detailQuery.refetch();
        allPermsQuery.refetch();
        rolePermsQuery.refetch();
      }}
      onSave={handleSave}
      onExit={() => {
        useWorkbenchStore.getState().removeContentTab(appNumber, tabKey);
      }}
      headerExtra={
        <div className="sm-edit-body sm-edit-header-extra">
          <Collapse
            className="sm-edit-collapse"
            defaultActiveKey={['perms']}
            items={[
              {
                key: 'perms',
                label: '权限分配',
                children: (
                  <Spin spinning={allPermsQuery.isLoading}>
                    {groupedPerms.length === 0 ? (
                      <Empty description="暂无权限数据" />
                    ) : (
                      <Collapse
                        size="small"
                        items={groupedPerms.map((group) => ({
                          key: group.appId,
                          label: `${group.appName}（${group.permissions.length}）`,
                          children: (
                            <Checkbox.Group
                              value={[...displayCheckedIds]}
                              onChange={handlePermChange}
                            >
                              <div className="sm-edit-checkbox-column">
                                {group.permissions.map((perm) => (
                                  <Checkbox key={perm.id} value={perm.id}>
                                    {perm.number} — {perm.name}
                                  </Checkbox>
                                ))}
                              </div>
                            </Checkbox.Group>
                          ),
                        }))}
                      />
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

export default RoleEditPage;
