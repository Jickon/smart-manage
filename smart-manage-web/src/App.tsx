import { useEffect, useMemo } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ConfigProvider, App as AntApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import routes from '@/router';
import themeConfig from '@/styles/theme';
import { getCurrentUser } from '@/api/user';
import { useUserStore } from '@/stores/user';
// 自动生成的组件注册表导入 — 由 pnpm gen:registry 生成
import '@/domain/common/registry/registry.gen';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  const router = useMemo(() => createMemoryRouter(routes), []);

  const setUserInfo = useUserStore((s) => s.setUserInfo);

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

  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider theme={{ ...themeConfig, cssVar: {} }} locale={zhCN}>
        <AntApp>
          <RouterProvider router={router} />
        </AntApp>
      </ConfigProvider>
    </QueryClientProvider>
  );
}
