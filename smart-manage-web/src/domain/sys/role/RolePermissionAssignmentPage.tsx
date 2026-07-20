import { useMemo, useState } from 'react';
import { App, Checkbox, Collapse, Empty } from 'antd';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { AssignmentPage } from '@/domain/common/page/AssignmentPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { permissionApi } from '@/domain/sys/permission/api';
import { permissionQueryKeys } from '@/domain/sys/permission/queryKeys';
import type { PermissionListAllVO } from '@/domain/sys/permission/types';
import type { PageComponentProps } from '@/domain/common/page/types';
import { roleApi } from './api';
import { roleQueryKeys } from './queryKeys';

interface PermissionGroup {
  appId: string;
  permissions: PermissionListAllVO[];
}

/** 角色权限分配专用页面。 */
const RolePermissionAssignmentPage = ({ appNumber, tabKey, billId }: PageComponentProps) => {
  const { message } = App.useApp();
  const queryClient = useQueryClient();
  const [localIds, setLocalIds] = useState<string[] | null>(null);
  const detailQuery = useQuery({
    queryKey: roleQueryKeys.detail(billId),
    queryFn: () => roleApi.detail(billId!),
    enabled: Boolean(billId),
  });
  const permissionsQuery = useQuery({
    queryKey: permissionQueryKeys.listAll(),
    queryFn: permissionApi.listAll,
  });
  const checkedIds = localIds ?? detailQuery.data?.permissionIds ?? [];
  const groups = useMemo(() => {
    const groupMap = new Map<string, PermissionGroup>();
    for (const permission of permissionsQuery.data ?? []) {
      const group = groupMap.get(permission.appId) ?? { appId: permission.appId, permissions: [] };
      group.permissions.push(permission);
      groupMap.set(permission.appId, group);
    }
    return [...groupMap.values()];
  }, [permissionsQuery.data]);
  const mutation = useMutation({
    mutationFn: () => roleApi.assignPermissions(billId!, checkedIds),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: roleQueryKeys.detail(billId) });
      message.success('权限分配成功');
    },
    onError: (error) => message.error(error instanceof Error ? error.message : '权限分配失败'),
  });
  const close = () => useWorkbenchStore.getState().removeContentTab(appNumber, tabKey);

  return (
    <AssignmentPage
      loading={detailQuery.isLoading || permissionsQuery.isLoading}
      saving={mutation.isPending}
      error={(detailQuery.error || permissionsQuery.error) as Error | null}
      onRetry={() => void Promise.all([detailQuery.refetch(), permissionsQuery.refetch()])}
      onSave={() => mutation.mutate()}
      onExit={close}
    >
      <Collapse
        className="sm-edit-collapse"
        defaultActiveKey={groups.map((group) => group.appId)}
        items={groups.map((group) => ({
          key: group.appId,
          label: `应用 ${group.appId}（${group.permissions.length}）`,
          children: (
            <Checkbox.Group
              value={checkedIds}
              onChange={(values) => setLocalIds(values.map(String))}
            >
              <div className="sm-edit-checkbox-column">
                {group.permissions.map((permission) => (
                  <Checkbox key={permission.id} value={permission.id}>
                    {permission.number} — {permission.name}
                  </Checkbox>
                ))}
              </div>
            </Checkbox.Group>
          ),
        }))}
      />
      {groups.length === 0 && <Empty description="暂无权限数据" />}
    </AssignmentPage>
  );
};

export default RolePermissionAssignmentPage;
