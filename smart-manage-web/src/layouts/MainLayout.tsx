import { lazy, Suspense } from 'react';
import type { ReactNode } from 'react';
import { Layout, Spin } from 'antd';
import Header from './Header';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import './MainLayout.css';

const { Content } = Layout;

const Home = lazy(() => import('@/pages/home/Home'));
const AppsView = lazy(() => import('@/pages/app/AppsView'));
const Workbench = lazy(() => import('@/pages/workbench/Workbench'));

/** Suspense fallback — 页面懒加载时显示 */
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
  const appTabs = tabs.filter((tab) => tab.closable);

  return (
    <Layout className="sm-layout">
      <Header />
      <Content className="sm-layout-content">
        <ol className="sm-views">
          {/* 首页 */}
          <li className={`sm-view ${activeKey === 'home' ? 'sm-view--active' : ''}`}>
            {renderLazyPage(<Home />)}
          </li>

          {/* 应用选择页 */}
          <li className={`sm-view ${activeKey === 'apps' ? 'sm-view--active' : ''}`}>
            {renderLazyPage(<AppsView />)}
          </li>

          {/* 动态应用工作台 — 每个已打开的应用一个 li */}
          {appTabs.map((tab) => (
            <li
              key={tab.key}
              className={`sm-view ${activeKey === tab.key ? 'sm-view--active' : ''}`}
            >
              {renderLazyPage(<Workbench appNumber={tab.key} />)}
            </li>
          ))}
        </ol>
      </Content>
    </Layout>
  );
};

export default MainLayout;
