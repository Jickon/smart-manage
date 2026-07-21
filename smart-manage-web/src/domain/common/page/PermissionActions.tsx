import { Button, Space } from 'antd';
import type { PermissionAction } from './access';
import { usePermissionAccess } from './usePermissionAccess';

interface PermissionActionsProps {
  prefix?: string;
  actions: PermissionAction[];
}

/** 统一渲染页面命令，并按后端返回的当前用户权限过滤。 */
export function PermissionActions({ prefix, actions }: PermissionActionsProps) {
  const { can } = usePermissionAccess(prefix);
  return (
    <Space>
      {actions
        .filter((action) => can(action.permission))
        .map((action) => (
          <Button
            key={action.key}
            type={action.type}
            danger={action.danger}
            disabled={action.disabled}
            loading={action.loading}
            onClick={action.onClick}
          >
            {action.label}
          </Button>
        ))}
    </Space>
  );
}
