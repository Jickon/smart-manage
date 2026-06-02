import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Message, Modal } from '@arco-design/web-react';

interface UseDeleteMutationOptions {
  /** 删除确认提示中显示的数据名称，如 "应用"、"云" */
  label?: string;
  /** 删除成功后需要刷新的 queryKey */
  invalidateQueryKey?: unknown[];
}

/**
 * 删除操作的 TanStack Query useMutation 封装
 * 统一处理确认弹窗、loading 状态、成功/错误消息、查询刷新
 */
export function useDeleteMutation(
  mutationFn: (id: string) => Promise<unknown>,
  options: UseDeleteMutationOptions = {},
) {
  const { label = '数据', invalidateQueryKey } = options;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (ids: string[]) =>
      new Promise<void>((resolve, reject) => {
        if (ids.length === 0) {
          Message.warning('请先选择要删除的数据');
          reject(new Error('未选择数据'));
          return;
        }
        Modal.confirm({
          title: '确认删除',
          content: `确定删除已选的 ${ids.length} 条${label}吗？`,
          onOk: async () => {
            try {
              await Promise.all(ids.map((id) => mutationFn(id)));
              Message.success('删除成功');
              if (invalidateQueryKey) {
                await queryClient.invalidateQueries({ queryKey: invalidateQueryKey });
              }
              resolve();
            } catch (error) {
              Message.error(error instanceof Error ? error.message : '删除失败');
              reject(error instanceof Error ? error : new Error('删除失败'));
            }
          },
          onCancel: () => {
            reject(new Error('已取消'));
          },
        });
      }),
  });
}
