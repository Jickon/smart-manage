import { useQuery } from '@tanstack/react-query';
import { Empty, Spin } from '@arco-design/web-react';
import { appApi } from '@/api/app';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import type { AppVO } from '@/types/api';
import { IconCommon } from '@arco-design/web-react/icon';

const AppsView = () => {
  const { data, isLoading } = useQuery({
    queryKey: ['cloud-apps'],
    queryFn: appApi.apps,
  });

  const addAppTab = useHeaderTabsStore((s) => s.addAppTab);
  const activate = useHeaderTabsStore((s) => s.activate);
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const setActivatingKey = useHeaderTabsStore((s) => s.setActivatingKey);
  const initWorkspace = useAppWorkspaceStore((s) => s.initWorkspace);

  const handleAppClick = (app: AppVO) => {
    // 如果 tab 已存在，直接激活
    if (tabs.find((t) => t.key === app.number)) {
      activate(app.number);
      return;
    }
    setActivatingKey(app.number);
    appApi
      .openByNumber(app.number)
      .then((appInfo) => {
        initWorkspace(app.number, appInfo);
        addAppTab(app.number, app.name);
      })
      .catch(() => {
        // 应用不存在或无权访问
      })
      .finally(() => {
        setActivatingKey(null);
      });
  };

  return (
    <div className="sm-apps">
      <Spin loading={isLoading}>
        {data?.map((cloud) => (
          <div key={cloud.number} className="sm-cloud-domain">
            <div className="sm-cloud-name">
              <div className="sm-cloud-name-text">{cloud.name}</div>
            </div>
            <div className="sm-cloud-apps">
              {cloud.appList?.map((app) => (
                <div key={app.number} className="sm-app-card" onClick={() => handleAppClick(app)}>
                  <IconCommon className="sm-app-card-icon" />
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
