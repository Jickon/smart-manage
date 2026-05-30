import { useEffect } from 'react';
import { Spin } from '@arco-design/web-react';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { menuApi } from '@/api/menu';
import PageRenderer from '@/cloud/common/page/PageRenderer';
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
  const openListTab = useAppWorkspaceStore((s) => s.openListTab);

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

  const getMenuComponentKey = (item: MenuVO) => item.component?.trim() || item.path?.trim() || item.name;

  const handleMenuItemClick = (item: MenuVO) => {
    const componentKey = getMenuComponentKey(item);
    openListTab(appNumber, componentKey, item.name);
  };

  return (
    <div className="sm-workspace">
      <AppSidebar menuTree={ws.menuTree} loading={ws.menuLoading} onItemClick={handleMenuItemClick} />
      <div className="sm-workspace-body">
        <ContentTabsBar appNumber={appNumber} />
        <Spin loading={ws.menuLoading} className="sm-workspace-spin">
          <ol className="sm-workspace-content">
            {ws.contentTabs.map((tab) => (
              <li
                key={tab.key}
                className={`sm-content-pane ${ws.activeContentTabKey === tab.key ? 'sm-content-pane--active' : ''}`}
              >
                {tab.key === '__home__' ? (
                  '欢迎使用'
                ) : (
                  <PageRenderer
                    appNumber={appNumber}
                    tabKey={tab.key}
                    title={tab.label}
                    componentKey={tab.componentKey}
                    operationType={tab.operationType}
                    billId={tab.billId}
                    temporary={tab.temporary}
                  />
                )}
              </li>
            ))}
          </ol>
        </Spin>
      </div>
    </div>
  );
};

export default AppWorkspace;
