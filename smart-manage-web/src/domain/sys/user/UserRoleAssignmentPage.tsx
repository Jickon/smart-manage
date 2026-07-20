import { useState } from 'react';
import { App, Checkbox, Collapse, Empty } from 'antd';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { AssignmentPage } from '@/domain/common/page/AssignmentPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { roleApi } from '@/domain/sys/role/api';
import { roleQueryKeys } from '@/domain/sys/role/queryKeys';
import type { PageComponentProps } from '@/domain/common/page/types';
import { userApi } from './api';
import { userQueryKeys } from './queryKeys';

/** 用户角色分配专用页面。 */
const UserRoleAssignmentPage = ({ appNumber, tabKey, billId }: PageComponentProps) => {
  const { message } = App.useApp();
  const queryClient = useQueryClient();
  const [localIds, setLocalIds] = useState<string[] | null>(null);
  const detailQuery = useQuery({
    queryKey: userQueryKeys.detail(billId),
    queryFn: () => userApi.detail(billId!),
    enabled: Boolean(billId),
  });
  const rolesQuery = useQuery({ queryKey: roleQueryKeys.listAll(), queryFn: roleApi.listAll });
  const checkedIds = localIds ?? detailQuery.data?.roleIds ?? [];
  const mutation = useMutation({
    mutationFn: () => userApi.assignRoles(billId!, checkedIds),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: userQueryKeys.detail(billId) });
      message.success('角色分配成功');
    },
    onError: (error) => message.error(error instanceof Error ? error.message : '角色分配失败'),
  });

  return (
    <AssignmentPage
      loading={detailQuery.isLoading || rolesQuery.isLoading}
      saving={mutation.isPending}
      error={(detailQuery.error || rolesQuery.error) as Error | null}
      onRetry={() => void Promise.all([detailQuery.refetch(), rolesQuery.refetch()])}
      onSave={() => mutation.mutate()}
      onExit={() => useWorkbenchStore.getState().removeContentTab(appNumber, tabKey)}
    >
      <Collapse
        className="sm-edit-collapse"
        defaultActiveKey={['roles']}
        items={[
          {
            key: 'roles',
            label: `角色分配（${rolesQuery.data?.length ?? 0}）`,
            children:
              rolesQuery.data && rolesQuery.data.length > 0 ? (
                <Checkbox.Group
                  value={checkedIds}
                  onChange={(values) => setLocalIds(values.map(String))}
                >
                  <div className="sm-edit-checkbox-column">
                    {rolesQuery.data.map((role) => (
                      <Checkbox key={role.id} value={role.id}>
                        {role.number} — {role.name}
                      </Checkbox>
                    ))}
                  </div>
                </Checkbox.Group>
              ) : (
                <Empty description="暂无角色数据" />
              ),
          },
        ]}
      />
    </AssignmentPage>
  );
};

export default UserRoleAssignmentPage;
