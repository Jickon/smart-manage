import { useState } from 'react';
import { Empty, Menu, Skeleton } from '@arco-design/web-react';
import {
  IconApps,
  IconFile,
  IconMenuFold,
  IconMenuUnfold,
  IconSafe,
  IconSettings,
  IconUserGroup,
} from '@arco-design/web-react/icon';
import type { MenuVO } from '@/types/api';

interface Props {
  menuTree: MenuVO | null;
  loading: boolean;
  onItemClick: (item: MenuVO) => void;
}

function findMenuItem(items: MenuVO[], key: string): MenuVO | null {
  for (const item of items) {
    if ((item.path || item.name) === key) return item;
    if (item.routes?.length) {
      const found = findMenuItem(item.routes, key);
      if (found) return found;
    }
  }
  return null;
}

function resolveMenuIcon(item: MenuVO) {
  const iconKey = [item.icon, item.path, item.component, item.name].filter(Boolean).join(' ').toLowerCase();
  if (iconKey.includes('user') || iconKey.includes('用户') || iconKey.includes('角色')) return <IconUserGroup />;
  if (iconKey.includes('permission') || iconKey.includes('权限')) return <IconSafe />;
  if (iconKey.includes('file') || iconKey.includes('文件')) return <IconFile />;
  if (iconKey.includes('app') || iconKey.includes('应用')) return <IconApps />;
  if (iconKey.includes('base') || iconKey.includes('基础')) return <IconSettings />;
  return <IconFile />;
}

function renderMenuTitle(item: MenuVO) {
  return (
    <span className="sm-sidebar-menu-title">
      <span className="sm-sidebar-menu-icon">{resolveMenuIcon(item)}</span>
      <span className="sm-sidebar-menu-text">{item.name}</span>
    </span>
  );
}

function renderMenuTree(items: MenuVO[]): React.ReactNode {
  return items.map((item) => {
    const key = item.path || item.name;
    if (item.routes?.length) {
      return (
        <Menu.SubMenu key={key} title={renderMenuTitle(item)} selectable={false}>
          {renderMenuTree(item.routes)}
        </Menu.SubMenu>
      );
    }
    return <Menu.Item key={key}>{renderMenuTitle(item)}</Menu.Item>;
  });
}

const AppSidebar = ({ menuTree, loading, onItemClick }: Props) => {
  const [collapsed, setCollapsed] = useState(false);

  const handleClick = (key: string) => {
    if (!menuTree?.routes) return;
    const item = findMenuItem(menuTree.routes, key);
    if (item) onItemClick(item);
  };

  return (
    <div className={`sm-workspace-sidebar ${collapsed ? 'sm-workspace-sidebar--collapsed' : ''}`}>
      <div className="sm-workspace-sidebar-content">
        <div className="sm-workspace-sidebar-meun">
          {loading ? (
            <Skeleton className="sm-sidebar-skeleton" />
          ) : !menuTree?.routes?.length ? (
            <Empty description="暂无菜单" />
          ) : (
            <Menu mode="pop" collapse={collapsed} onClickMenuItem={handleClick}>
              {renderMenuTree(menuTree.routes)}
            </Menu>
          )}
        </div>
        <div className="sm-workspace-sidebar-switch">
          <button
            className="sm-workspace-sidebar-switch-btn"
            type="button"
            onClick={() => setCollapsed((current) => !current)}
          >
            {collapsed ? <IconMenuUnfold /> : <IconMenuFold />}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AppSidebar;
