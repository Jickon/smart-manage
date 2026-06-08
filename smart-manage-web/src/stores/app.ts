import { create } from 'zustand';

/** Tab 标签 */
export interface TabItem {
  key: string;
  title: string;
  /** 页面组件路径，用于组件注册表映射 */
  component: string;
  closable: boolean;
}

interface AppState {
  /** 当前应用标识（Header tabs） */
  currentApp: string;
  /** 侧边栏折叠状态 */
  siderCollapsed: boolean;
  /** 应用工作台内打开的 tabs */
  tabs: TabItem[];
  /** 当前激活的 tab key */
  activeTabKey: string;

  setCurrentApp: (app: string) => void;
  toggleSider: () => void;
  openTab: (tab: TabItem) => void;
  closeTab: (key: string) => void;
  setActiveTab: (key: string) => void;
}

export const useAppStore = create<AppState>((set, get) => ({
  currentApp: 'home',
  siderCollapsed: false,
  tabs: [],
  activeTabKey: '',

  setCurrentApp: (currentApp: string) => set({ currentApp }),

  toggleSider: () => set((state) => ({ siderCollapsed: !state.siderCollapsed })),

  openTab: (tab: TabItem) => {
    const { tabs } = get();
    const existing = tabs.find((t) => t.key === tab.key);
    if (existing) {
      // 已存在则激活
      set({ activeTabKey: tab.key });
    } else {
      set({ tabs: [...tabs, tab], activeTabKey: tab.key });
    }
  },

  closeTab: (key: string) => {
    const { tabs, activeTabKey } = get();
    const newTabs = tabs.filter((t) => t.key !== key);
    let newActiveTab = activeTabKey;
    if (activeTabKey === key && newTabs.length > 0) {
      // 关闭当前 tab 时自动切换到相邻 tab
      const index = tabs.findIndex((t) => t.key === key);
      const nextIndex = Math.min(index, newTabs.length - 1);
      newActiveTab = newTabs[nextIndex]?.key ?? '';
    }
    set({ tabs: newTabs, activeTabKey: newActiveTab });
  },

  setActiveTab: (activeTabKey: string) => set({ activeTabKey }),
}));
