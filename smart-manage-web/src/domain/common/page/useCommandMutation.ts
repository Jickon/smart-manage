import { App } from 'antd';
import { useMutation } from '@tanstack/react-query';

interface CommandVariables {
  command: (values: Record<string, unknown>) => Promise<void>;
  values: Record<string, unknown>;
}

/** 统一管理业务命令的提交状态和失败提示。 */
export function useCommandMutation() {
  const { message } = App.useApp();

  return useMutation({
    mutationFn: ({ command, values }: CommandVariables) => command(values),
    onError: (error) => {
      message.error(error instanceof Error ? error.message : '操作失败，请稍后重试');
    },
  });
}
