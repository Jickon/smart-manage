import { create } from 'zustand';

export interface HeaderTabItem {
  key: string;
  label: string;
  closable: boolean;
}

interface HeaderTabsState {
  tabs: HeaderTabItem[];
  activeKey: string;
  activeHistory: string[];
  activatingKey: string | null;
  activate: (key: string) => void;
  addAppTab: (key: string, label: string) => void;
  removeAppTab: (key: string) => void;
  setActivatingKey: (key: string | null) => void;
}

function pushHistory(history: string[], key: string) {
  return [...history.filter((item) => item !== key), key];
}

function resolveNextActiveKey(history: string[], tabs: HeaderTabItem[], removedKey: string) {
  const remainingKeys = new Set(tabs.map((tab) => tab.key));
  const nextKey = [...history].reverse().find((key) => key !== removedKey && remainingKeys.has(key));
  return nextKey ?? 'apps';
}

export const useHeaderTabsStore = create<HeaderTabsState>((set, get) => ({
  tabs: [
    { key: 'home', label: '首页', closable: false },
    { key: 'apps', label: '应用', closable: false },
  ],
  activeKey: 'home',
  activeHistory: ['home'],
  activatingKey: null,

  activate: (key) =>
    set((state) => ({
      activeKey: key,
      activeHistory: pushHistory(state.activeHistory, key),
    })),

  addAppTab: (key, label) => {
    const { tabs, activeHistory } = get();
    const exists = tabs.find((tab) => tab.key === key);
    if (exists) {
      set({ activeKey: key, activeHistory: pushHistory(activeHistory, key) });
      return;
    }
    set({
      tabs: [...tabs, { key, label, closable: true }],
      activeKey: key,
      activeHistory: pushHistory(activeHistory, key),
    });
  },

  removeAppTab: (key) => {
    const { tabs, activeKey, activeHistory } = get();
    const target = tabs.find((tab) => tab.key === key);
    if (!target?.closable) return;
    const nextTabs = tabs.filter((tab) => tab.key !== key);
    const nextHistory = activeHistory.filter((item) => item !== key);
    const nextActiveKey = activeKey === key ? resolveNextActiveKey(activeHistory, nextTabs, key) : activeKey;
    set({
      tabs: nextTabs,
      activeKey: nextActiveKey,
      activeHistory: pushHistory(nextHistory, nextActiveKey),
    });
  },

  setActivatingKey: (key) => set({ activatingKey: key }),
}));
