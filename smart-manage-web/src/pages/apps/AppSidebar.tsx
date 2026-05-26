import {Empty, Menu, Skeleton} from '@arco-design/web-react';
import type {MenuVO} from '@/types/api';
import {IconMenuFold} from "@arco-design/web-react/icon";

interface Props {
  menuTree: MenuVO | null;
  loading: boolean;
  onItemClick: (item: MenuVO) => void;
}

/** 在菜单树中递归查找指定 key 的节点 */
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

function renderMenuTree(items: MenuVO[]): React.ReactNode {
  return items.map((item) => {
    const key = item.path || item.name;
    if (item.routes?.length) {
      return (
        <Menu.SubMenu key={key} title={item.name}>
          {renderMenuTree(item.routes)}
        </Menu.SubMenu>
      );
    }
    return <Menu.Item key={key}>{item.name}</Menu.Item>;
  });
}

const AppSidebar = ({ menuTree, loading, onItemClick }: Props) => {
  const handleClick = (key: string) => {
    if (!menuTree?.routes) return;
    const item = findMenuItem(menuTree.routes, key);
    if (item) onItemClick(item);
  };

  return (
    <div className="sm-workspace-sidebar">
      <div className="sm-workspace-sidebar-content">
        <div className="sm-workspace-sidebar-meun">
          {loading ? (
              <Skeleton className="sm-sidebar-skeleton"/>
          ) : !menuTree?.routes?.length ? (
              <Empty description="暂无菜单"/>
          ) : (
              <Menu mode="pop" onClickMenuItem={handleClick}>
                {renderMenuTree(menuTree.routes)}
              </Menu>
          )}
        </div>
        <div className="sm-workspace-sidebar-switch">
          <div className="sm-workspace-sidebar-switch-btn">
            <IconMenuFold/>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AppSidebar;
