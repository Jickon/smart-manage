import { Layout } from '@arco-design/web-react';
import ShellHeader from './Header';
import Home from '@/pages/Home';
import AppsView from '@/pages/apps/AppsView';
import AppWorkspace from '@/pages/apps/AppWorkspace';
import { useHeaderTabsStore } from '@/stores/headerTabs';

const Content = Layout.Content;

const MainLayout = () => {
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const activeKey = useHeaderTabsStore((s) => s.activeKey);
  const appTabs = tabs.filter((t) => t.closable);

  return (
    <Layout className="sm-layout">
      <ShellHeader />
      <Content className="sm-content">
        <ol className="sm-views">
          <li className={`sm-view ${activeKey === 'home' ? 'sm-view--active' : ''}`}>
            <Home />
          </li>
          <li className={`sm-view ${activeKey === 'apps' ? 'sm-view--active' : ''}`}>
            <AppsView />
          </li>
          {appTabs.map((tab) => (
            <li key={tab.key} className={`sm-view ${activeKey === tab.key ? 'sm-view--active' : ''}`}>
              <AppWorkspace appNumber={tab.key} />
            </li>
          ))}
        </ol>
      </Content>
    </Layout>
  );
};

export default MainLayout;
