import { create } from 'zustand';

export interface HeaderTabItem {
  key: string;
  label: string;
  closable: boolean;
}

interface HeaderTabsState {
  tabs: HeaderTabItem[];
  activeKey: string;
  activatingKey: string | null;
  activate: (key: string) => void;
  addAppTab: (key: string, label: string) => void;
  removeAppTab: (key: string) => void;
  setActivatingKey: (key: string | null) => void;
}

export const useHeaderTabsStore = create<HeaderTabsState>((set, get) => ({
  tabs: [
    { key: 'home', label: '首页', closable: false },
    { key: 'apps', label: '应用', closable: false },
  ],
  activeKey: 'home',
  activatingKey: null,

  activate: (key) => set({ activeKey: key }),

  addAppTab: (key, label) => {
    const { tabs } = get();
    const exists = tabs.find((t) => t.key === key);
    if (exists) {
      set({ activeKey: key });
      return;
    }
    set({
      tabs: [...tabs, { key, label, closable: true }],
      activeKey: key,
    });
  },

  removeAppTab: (key) => {
    const { tabs, activeKey } = get();
    const idx = tabs.findIndex((t) => t.key === key);
    if (idx < 2) return; // 固定 tab 不可关闭
    const newTabs = tabs.filter((t) => t.key !== key);
    let newActiveKey = activeKey;
    if (activeKey === key) {
      newActiveKey = newTabs[Math.min(idx - 1, newTabs.length - 1)].key;
    }
    set({ tabs: newTabs, activeKey: newActiveKey });
  },

  setActivatingKey: (key) => set({ activatingKey: key }),
}));
