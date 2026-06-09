import { useState } from 'react';
import { Empty, Menu, Skeleton } from 'antd';
import type { MenuProps } from 'antd';
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons';
import type { MenuVO } from '@/types/api';

interface Props {
  menuTree: MenuVO | null;
  loading: boolean;
  onItemClick: (item: MenuVO) => void;
}

/** 在菜单树中递归查找 */
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

/** 递归渲染菜单项 */
function renderMenuTree(items: MenuVO[]): Required<MenuProps>['items'] {
  return items.map((item) => {
    const key = item.path || item.name;
    if (item.routes?.length) {
      return {
        key,
        label: item.name,
        icon: item.icon ? <span>{item.icon}</span> : undefined,
        children: renderMenuTree(item.routes),
      };
    }
    return {
      key,
      label: item.name,
      icon: item.icon ? <span>{item.icon}</span> : undefined,
    };
  });
}

const AppSidebar = ({ menuTree, loading, onItemClick }: Props) => {
  const [collapsed, setCollapsed] = useState(false);

  const handleClick: MenuProps['onClick'] = (info) => {
    if (!menuTree?.routes) return;
    const item = findMenuItem(menuTree.routes, info.key);
    if (item) onItemClick(item);
  };

  return (
    <div className={`sm-workspace-sidebar ${collapsed ? 'sm-workspace-sidebar--collapsed' : ''}`}>
      <div className="sm-workspace-sidebar-content">
        <div className="sm-workspace-sidebar-menu">
          {loading ? (
            <Skeleton className="sm-sidebar-skeleton" active paragraph={{ rows: 6 }} />
          ) : !menuTree?.routes?.length ? (
            <Empty description="暂无菜单" />
          ) : (
            <Menu
              mode="inline"
              inlineCollapsed={collapsed}
              onClick={handleClick}
              items={renderMenuTree(menuTree.routes)}
              theme="dark"
              style={{ background: 'transparent' }}
            />
          )}
        </div>
        <div className="sm-workspace-sidebar-switch">
          <button
            className="sm-workspace-sidebar-switch-btn"
            type="button"
            onClick={() => setCollapsed((current) => !current)}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AppSidebar;
