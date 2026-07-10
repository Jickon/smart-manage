import { App } from 'antd';
import { useMutation } from '@tanstack/react-query';

interface BatchDeleteOptions {
  deleteFn: (id: string) => Promise<unknown>;
  onSuccess: () => void | Promise<void>;
}

/** 统一管理列表批量删除的提交状态、结果提示和成功后刷新。 */
export function useBatchDeleteMutation({ deleteFn, onSuccess }: BatchDeleteOptions) {
  const { message } = App.useApp();

  return useMutation({
    mutationFn: (ids: string[]) => Promise.all(ids.map((id) => deleteFn(id))),
    onSuccess: async () => {
      message.success('删除成功');
      await onSuccess();
    },
    onError: (error) => {
      message.error(error instanceof Error ? error.message : '删除失败，请稍后重试');
    },
  });
}
