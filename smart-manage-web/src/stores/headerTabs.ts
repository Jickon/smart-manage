import { create } from 'zustand';

export interface HeaderTabItem {
  key: string;
  label: string;
  closable: boolean;
}

interface HeaderTabsState {
  tabs: HeaderTabItem[];
  activeKey: string;
  /** 切换历史 — 关闭 tab 时智能回到上一个激活的 tab */
  activeHistory: string[];
  /** 纯状态切换 — 不操作 URL，由 navigationService 统一控制 URL 同步 */
  activate: (key: string) => void;
  /** 纯状态添加 — 不操作 URL */
  addAppTab: (key: string, label: string) => void;
  /** 纯状态移除 — 不操作 URL */
  removeAppTab: (key: string) => void;
}

/** 将 key 推到历史末尾，去重 */
function pushHistory(history: string[], key: string) {
  return [...history.filter((item) => item !== key), key];
}

/** 关闭 tab 后，从历史中找最近的仍存在的 tab */
function resolveNextActiveKey(history: string[], tabs: HeaderTabItem[], removedKey: string) {
  const remainingKeys = new Set(tabs.map((tab) => tab.key));
  const nextKey = [...history]
    .reverse()
    .find((key) => key !== removedKey && remainingKeys.has(key));
  return nextKey ?? 'apps';
}

export const useHeaderTabsStore = create<HeaderTabsState>((set, get) => ({
  tabs: [
    { key: 'home', label: '首页', closable: false },
    { key: 'apps', label: '应用', closable: false },
  ],
  activeKey: 'home',
  activeHistory: ['home'],

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
    const nextActiveKey =
      activeKey === key ? resolveNextActiveKey(activeHistory, nextTabs, key) : activeKey;
    set({
      tabs: nextTabs,
      activeKey: nextActiveKey,
      activeHistory: pushHistory(nextHistory, nextActiveKey),
    });
  },
}));
