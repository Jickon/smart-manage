import { useMutation } from '@tanstack/react-query';
import { Message } from '@arco-design/web-react';

interface UseSaveMutationOptions {
  /** 成功提示文案，默认 "保存成功" */
  successMessage?: string;
}

/**
 * 保存/提交操作的 TanStack Query useMutation 封装
 * 统一处理 loading 状态、成功/错误消息提示
 */
export function useSaveMutation<TData = unknown, TVariables = unknown>(
  mutationFn: (variables: TVariables) => Promise<TData>,
  options: UseSaveMutationOptions = {},
) {
  const { successMessage = '保存成功' } = options;

  return useMutation({
    mutationFn,
    onSuccess: () => {
      Message.success(successMessage);
    },
    onError: (error) => {
      Message.error(error instanceof Error ? error.message : '保存失败');
    },
  });
}
