import { useEffect } from 'react';
import { Spin } from '@arco-design/web-react';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { menuApi } from '@/api/menu';
import AppSidebar from './AppSidebar';
import ContentTabsBar from './ContentTabsBar';
import type { MenuVO } from '@/types/api';

interface Props {
  appNumber: string;
}

const AppWorkspace = ({ appNumber }: Props) => {
  const ws = useAppWorkspaceStore((s) => s.workspaces[appNumber]);
  const setMenuTree = useAppWorkspaceStore((s) => s.setMenuTree);
  const setMenuLoading = useAppWorkspaceStore((s) => s.setMenuLoading);
  const addContentTab = useAppWorkspaceStore((s) => s.addContentTab);

  useEffect(() => {
    if (!ws || ws.menuTree || ws.menuLoading) return;
    setMenuLoading(appNumber, true);
    menuApi
      .getUserMenusByAppNumber(appNumber)
      .then((tree) => {
        setMenuTree(appNumber, tree);
      })
      .catch(() => {
        setMenuLoading(appNumber, false);
      });
  }, [appNumber, ws, setMenuTree, setMenuLoading]);

  if (!ws) return null;

  const handleMenuItemClick = (item: MenuVO) => {
    const key = item.path || item.name;
    addContentTab(appNumber, { key, label: item.name, closable: true });
  };

  return (
    <div className="sm-workspace">
      <AppSidebar
        menuTree={ws.menuTree}
        loading={ws.menuLoading}
        onItemClick={handleMenuItemClick}
      />
      <div className="sm-workspace-body">
        <ContentTabsBar appNumber={appNumber} />
        <Spin loading={ws.menuLoading} className="sm-workspace-spin">
          <ol className="sm-workspace-content">
            {ws.contentTabs.map((tab) => (
              <li
                key={tab.key}
                className={`sm-content-pane ${ws.activeContentTabKey === tab.key ? 'sm-content-pane--active' : ''}`}
              >
                {tab.key === '__home__' ? '欢迎使用' : `${tab.label} — 页面开发中`}
              </li>
            ))}
          </ol>
        </Spin>
      </div>
    </div>
  );
};

export default AppWorkspace;
