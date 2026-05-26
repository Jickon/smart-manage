import { useEffect, useMemo } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { ConfigProvider } from '@arco-design/web-react';
import zhCN from '@arco-design/web-react/es/locale/zh-CN';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import routes from '@/router';
import { useUserStore } from '@/stores/user';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { userApi } from '@/api/user';
import { appApi } from '@/api/app';

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

  const setUserInfo = useUserStore((s) => s.setUserInfo);
  const addAppTab = useHeaderTabsStore((s) => s.addAppTab);
  const initWorkspace = useAppWorkspaceStore((s) => s.initWorkspace);

  // 启动时获取用户信息
  useEffect(() => {
    userApi.info().then((info) => {
      setUserInfo({
        id: String(info.id),
        username: info.username,
        nickname: info.nickname,
      });
      // 应用主题色（后续 ConfigProvider 切换用）
      if (info.themeColor) {
        document.documentElement.style.setProperty('--primary-6', info.themeColor);
      }
    }).catch(() => {
      // 获取失败不阻塞，可能是未登录状态
    });
  }, [setUserInfo]);

  // 如果 URL 携带非 home 的 app 参数，自动打开对应应用工作台
  useEffect(() => {
    const appParam = getInitialAppParam();
    if (appParam === 'home' || appParam === 'apps') return;
    appApi.openByNumber(appParam).then((appInfo) => {
      initWorkspace(appParam, appInfo);
      addAppTab(appParam, appInfo.name);
    }).catch(() => {
      // 应用不存在或无权访问，保持首页
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
