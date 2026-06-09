import { useEffect } from 'react';
import { Spin } from 'antd';
import { useWorkbenchStore } from '@/stores/workbench';
import type { MenuVO } from '@/types/api';
import AppSidebar from './AppSidebar';
import ContentTabsBar from './ContentTabsBar';
import PageRenderer from './PageRenderer';

interface Props {
  appNumber: string;
}

/** 从菜单项中提取组件标识 */
function getMenuComponentKey(item: MenuVO) {
  return item.component?.trim() || item.path?.trim() || item.name;
}

const Workbench = ({ appNumber }: Props) => {
  const ws = useWorkbenchStore((s) => s.workspaces[appNumber]);
  const setMenuTree = useWorkbenchStore((s) => s.setMenuTree);
  const setMenuLoading = useWorkbenchStore((s) => s.setMenuLoading);
  const openListTab = useWorkbenchStore((s) => s.openListTab);

  useEffect(() => {
    if (!ws || ws.menuTree || ws.menuLoading) return;
    setMenuLoading(appNumber, true);
    // TODO: 调用 menuApi.getUserMenusByAppNumber(appNumber) 加载菜单
    // 当前 API 未就绪，直接标记加载完成
    setMenuTree(appNumber, { name: '', path: '', component: '', icon: '', level: 0, routes: [] });
  }, [appNumber, ws, setMenuTree, setMenuLoading]);

  if (!ws) return null;

  const handleMenuItemClick = (item: MenuVO) => {
    const componentKey = getMenuComponentKey(item);
    openListTab(appNumber, componentKey, item.name);
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
        <Spin spinning={ws.menuLoading}>
          <ol className="sm-workspace-content">
            {ws.contentTabs.map((tab) => (
              <li
                key={tab.key}
                className={`sm-content-pane ${ws.activeContentTabKey === tab.key ? 'sm-content-pane--active' : ''}`}
              >
                {tab.key === '__home__' ? (
                  <div className="sm-page-renderer-empty">欢迎使用 {ws.appInfo.name}</div>
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

export default Workbench;
