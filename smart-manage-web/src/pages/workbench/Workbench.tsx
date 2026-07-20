import { useCallback } from 'react';
import { Spin, Modal } from 'antd';
import { useQuery } from '@tanstack/react-query';
import { menuQueryKeys } from '@/domain/sys/menu/queryKeys';
import { useWorkbenchStore } from '@/stores/workbench';
import { getUserMenusByAppNumber } from '@/domain/sys/menu/api';
import AppSidebar from './AppSidebar';
import ContentTabsBar from './ContentTabsBar';
import PageRenderer from './PageRenderer';
import './Workbench.css';

interface Props {
  appNumber: string;
}

const Workbench = ({ appNumber }: Props) => {
  const ws = useWorkbenchStore((s) => s.workspaces[appNumber]);
  const openListTab = useWorkbenchStore((s) => s.openListTab);

  const menuQuery = useQuery({
    queryKey: menuQueryKeys.userByApp(appNumber),
    queryFn: () => getUserMenusByAppNumber(appNumber),
    staleTime: 5 * 60 * 1000,
  });

  const handleMenuItemClick = useCallback(
    (item: { component?: string; path?: string; name: string }) => {
      const componentKey = item.component?.trim() || item.path?.trim() || item.name;
      const result = openListTab(appNumber, componentKey, item.name);
      if (result === 'limit_reached') {
        Modal.warning({
          title: '页签数量已达上限',
          content: '请先关闭不再使用的页签后再打开新页面。',
        });
      }
    },
    [appNumber, openListTab],
  );

  if (!ws) return null;

  return (
    <div className="sm-workspace">
      <AppSidebar
        menuTree={menuQuery.data ?? null}
        loading={menuQuery.isLoading}
        onItemClick={handleMenuItemClick}
      />
      <div className="sm-workspace-body">
        <ContentTabsBar appNumber={appNumber} />
        <Spin spinning={menuQuery.isLoading}>
          <ol className="sm-workspace-content">
            {ws.contentTabs.map((tab) => {
              const isActive = ws.activeContentTabKey === tab.key;
              return (
                <li
                  key={tab.key}
                  className={`sm-content-pane ${isActive ? 'sm-content-pane--active' : ''}`}
                >
                  {tab.key === '__home__' ? (
                    <div className="sm-page-renderer-empty">欢迎使用 {ws.appInfo.name}</div>
                  ) : (
                    <PageRenderer
                      appNumber={appNumber}
                      tabKey={tab.key}
                      title={tab.label}
                      componentKey={tab.componentKey}
                      pageType={tab.pageType}
                      operationType={tab.operationType}
                      billId={tab.billId}
                      temporary={tab.temporary}
                      active={isActive}
                    />
                  )}
                </li>
              );
            })}
          </ol>
        </Spin>
      </div>
    </div>
  );
};

export default Workbench;
