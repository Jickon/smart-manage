import { useQuery } from '@tanstack/react-query';
import { Empty, Spin } from 'antd';
import { AppstoreOutlined } from '@ant-design/icons';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useWorkbenchStore } from '@/stores/workbench';
import { fetchApps } from '@/cloud/sys/app/api';
import type { AppVO } from '@/cloud/sys/app/types';
import './AppsView.css';

const AppsView = () => {
  const { data, isLoading } = useQuery({
    queryKey: ['cloud-apps'],
    queryFn: fetchApps,
  });

  const addAppTab = useHeaderTabsStore((s) => s.addAppTab);
  const activate = useHeaderTabsStore((s) => s.activate);
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const initWorkspace = useWorkbenchStore((s) => s.initWorkspace);

  const handleAppClick = (app: AppVO) => {
    if (tabs.find((tab) => tab.key === app.number)) {
      activate(app.number);
      return;
    }
    initWorkspace(app.number, app);
    addAppTab(app.number, app.name);
  };

  return (
    <div className="sm-apps">
      <Spin spinning={isLoading}>
        {data?.map((cloud) => (
          <div key={cloud.number} className="sm-cloud-domain">
            <div className="sm-cloud-name">
              <div className="sm-cloud-name-text">{cloud.name}</div>
            </div>
            <div className="sm-cloud-apps">
              {cloud.appList?.map((app) => (
                <div key={app.number} className="sm-app-card" onClick={() => handleAppClick(app)}>
                  <AppstoreOutlined className="sm-app-card-icon" />
                  <div className="sm-app-card-text">
                    <div className="sm-app-card-name">{app.name}</div>
                    <div className="sm-app-card-desc">{app.description}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
        {!isLoading && (!data || data.length === 0) && (
          <div className="sm-apps-empty">
            <Empty description="暂无可用应用" />
          </div>
        )}
      </Spin>
    </div>
  );
};

export default AppsView;
