import { App } from 'antd';
import { useMutation } from '@tanstack/react-query';

interface EnabledCommand {
  ids: string[];
  enabled: boolean;
}

/** 列表批量启停命令，成功后由调用方刷新数据并清空选择。 */
export function useEnabledMutation(
  command: (ids: string[], enabled: boolean) => Promise<unknown>,
  onSuccess: () => void | Promise<void>,
) {
  const { message } = App.useApp();
  return useMutation({
    mutationFn: ({ ids, enabled }: EnabledCommand) => command(ids, enabled),
    onSuccess: async (_data, variables) => {
      message.success(variables.enabled ? '启用成功' : '禁用成功');
      await onSuccess();
    },
  });
}
