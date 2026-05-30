import { lazy, Suspense } from 'react';
import type { ReactNode } from 'react';
import { Layout, Spin } from '@arco-design/web-react';
import ShellHeader from './Header';
import { useHeaderTabsStore } from '@/stores/headerTabs';

const Content = Layout.Content;
const Home = lazy(() => import('@/pages/Home'));
const AppsView = lazy(() => import('@/pages/apps/AppsView'));
const AppWorkspace = lazy(() => import('@/pages/apps/AppWorkspace'));

const renderLazyPage = (node: ReactNode) => (
  <Suspense
    fallback={
      <div className="sm-view-loading">
        <Spin />
      </div>
    }
  >
    {node}
  </Suspense>
);

const MainLayout = () => {
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const activeKey = useHeaderTabsStore((s) => s.activeKey);
  const appTabs = tabs.filter((t) => t.closable);

  return (
    <Layout className="sm-layout">
      <ShellHeader />
      <Content className="sm-content">
        <ol className="sm-views">
          <li className={`sm-view ${activeKey === 'home' ? 'sm-view--active' : ''}`}>{renderLazyPage(<Home />)}</li>
          <li className={`sm-view ${activeKey === 'apps' ? 'sm-view--active' : ''}`}>{renderLazyPage(<AppsView />)}</li>
          {appTabs.map((tab) => (
            <li key={tab.key} className={`sm-view ${activeKey === tab.key ? 'sm-view--active' : ''}`}>
              {renderLazyPage(<AppWorkspace appNumber={tab.key} />)}
            </li>
          ))}
        </ol>
      </Content>
    </Layout>
  );
};

export default MainLayout;
