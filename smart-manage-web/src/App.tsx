import { useEffect, useMemo } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { ConfigProvider } from '@arco-design/web-react';
import zhCN from '@arco-design/web-react/es/locale/zh-CN';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import routes from '@/router';
import { userApi } from '@/api/user';
import { appApi } from '@/cloud/sys/base/app/api';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useUserStore } from '@/stores/user';

function getInitialAppParam(): string {
  const params = new URLSearchParams(window.location.search);
  return params.get('app') || 'home';
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
});

const App = () => {
  const initialPath = useMemo(() => {
    const app = getInitialAppParam();
    return `/${app}`;
  }, []);

  const router = useMemo(
    () =>
      createMemoryRouter(routes, {
        initialEntries: [initialPath],
      }),
    [initialPath],
  );

  const setUserInfo = useUserStore((store) => store.setUserInfo);
  const addAppTab = useHeaderTabsStore((store) => store.addAppTab);
  const activate = useHeaderTabsStore((store) => store.activate);
  const initWorkspace = useAppWorkspaceStore((store) => store.initWorkspace);

  // 根据 URL 参数激活对应的 header tab
  useEffect(() => {
    activate(getInitialAppParam());
  }, [activate]);

  useEffect(() => {
    userApi
      .info()
      .then((info) => {
        setUserInfo({
          id: String(info.id),
          username: info.username,
          nickname: info.nickname,
        });
        if (info.themeColor) {
          document.documentElement.style.setProperty('--primary-6', info.themeColor);
        }
      })
      .catch(() => {
        // 用户信息失败不阻塞应用初始化，登录拦截由 request 统一处理。
      });
  }, [setUserInfo]);

  useEffect(() => {
    const appParam = getInitialAppParam();
    if (appParam === 'home' || appParam === 'apps') {
      return;
    }
    appApi
      .openByNumber(appParam)
      .then((appInfo) => {
        initWorkspace(appParam, appInfo);
        addAppTab(appParam, appInfo.name);
      })
      .catch(() => {
        // 应用不存在或无权访问时保持默认页。
      });
  }, [addAppTab, initWorkspace]);

  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider locale={zhCN}>
        <RouterProvider router={router} />
      </ConfigProvider>
    </QueryClientProvider>
  );
};

export default App;
