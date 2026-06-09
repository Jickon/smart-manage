import { useEffect, useMemo } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ConfigProvider, App as AntApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import routes from '@/router';
import themeConfig from '@/styles/theme';
import { getCurrentUser } from '@/api/user';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useWorkbenchStore } from '@/stores/workbench';
import { useUserStore } from '@/stores/user';
import { openByNumber } from '@/cloud/sys/app/api';

/** 从 URL 提取 app 参数 */
function getInitialAppParam(): string {
  const params = new URLSearchParams(window.location.search);
  return params.get('app') || 'home';
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  const initialPath = useMemo(() => {
    const appParam = getInitialAppParam();
    return `/${appParam}`;
  }, []);

  const router = useMemo(
    () =>
      createMemoryRouter(routes, {
        initialEntries: [initialPath],
      }),
    [initialPath],
  );

  const setUserInfo = useUserStore((s) => s.setUserInfo);
  const activate = useHeaderTabsStore((s) => s.activate);

  // 根据 URL 参数激活对应的 header tab
  useEffect(() => {
    activate(getInitialAppParam());
  }, [activate]);

  // 初始化用户信息
  useEffect(() => {
    getCurrentUser()
      .then((res) => {
        const info = res.data;
        setUserInfo({
          id: String(info.id),
          username: info.username,
          nickname: info.nickname,
          avatar: info.avatar,
          themeColor: info.themeColor,
        });
      })
      .catch(() => {
        // 用户信息失败不阻塞应用初始化，登录拦截由 request 统一处理
      });
  }, [setUserInfo]);

  // 如果 URL 带有应用 number 参数，初始化对应 workspace
  useEffect(() => {
    const appParam = getInitialAppParam();
    if (appParam === 'home' || appParam === 'apps') return;

    const { addAppTab } = useHeaderTabsStore.getState();
    const { initWorkspace } = useWorkbenchStore.getState();

    openByNumber(appParam)
      .then((appInfo) => {
        initWorkspace(appParam, appInfo);
        addAppTab(appParam, appInfo.name);
      })
      .catch(() => {
        // 应用不存在或无权访问时保持默认页
      });
  }, []);

  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider theme={themeConfig} locale={zhCN}>
        <AntApp>
          <RouterProvider router={router} />
        </AntApp>
      </ConfigProvider>
    </QueryClientProvider>
  );
}
