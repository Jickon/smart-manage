import { create } from 'zustand';

interface AppState {
  /** 侧边栏折叠状态 */
  collapsed: boolean;
  /** 当前应用模块，对应 URL 参数 app */
  appModule: string;
  toggleCollapsed: () => void;
  setAppModule: (module: string) => void;
}

export const useAppStore = create<AppState>((set) => ({
  collapsed: false,
  appModule: 'home',
  toggleCollapsed: () => set((state) => ({ collapsed: !state.collapsed })),
  setAppModule: (appModule) => set({ appModule }),
}));
