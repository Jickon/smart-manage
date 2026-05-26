import { create } from 'zustand';
import type { AppVO, MenuVO } from '@/types/api';

export interface ContentTabItem {
  key: string;
  label: string;
  closable: boolean;
}

interface WorkspaceState {
  appInfo: AppVO | null;
  menuTree: MenuVO | null;
  menuLoading: boolean;
  contentTabs: ContentTabItem[];
  activeContentTabKey: string;
}

interface AppWorkspaceState {
  workspaces: Record<string, WorkspaceState>;
  initWorkspace: (appNumber: string, appInfo: AppVO) => void;
  setMenuTree: (appNumber: string, menuTree: MenuVO) => void;
  setMenuLoading: (appNumber: string, loading: boolean) => void;
  addContentTab: (appNumber: string, tab: ContentTabItem) => void;
  removeContentTab: (appNumber: string, tabKey: string) => void;
  activateContentTab: (appNumber: string, tabKey: string) => void;
}

function defaultWorkspace(appInfo: AppVO): WorkspaceState {
  return {
    appInfo,
    menuTree: null,
    menuLoading: false,
    contentTabs: [{ key: '__home__', label: '', closable: false }],
    activeContentTabKey: '__home__',
  };
}

export const useAppWorkspaceStore = create<AppWorkspaceState>((set, get) => ({
  workspaces: {},

  initWorkspace: (appNumber, appInfo) => {
    const { workspaces } = get();
    if (workspaces[appNumber]) return;
    set({ workspaces: { ...workspaces, [appNumber]: defaultWorkspace(appInfo) } });
  },

  setMenuTree: (appNumber, menuTree) => {
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: { ...ws, menuTree, menuLoading: false },
      },
    });
  },

  setMenuLoading: (appNumber, loading) => {
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: { ...ws, menuLoading: loading },
      },
    });
  },

  addContentTab: (appNumber, tab) => {
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    const exists = ws.contentTabs.find((t) => t.key === tab.key);
    if (exists) {
      set({
        workspaces: {
          ...workspaces,
          [appNumber]: { ...ws, activeContentTabKey: tab.key },
        },
      });
      return;
    }
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: {
          ...ws,
          contentTabs: [...ws.contentTabs, tab],
          activeContentTabKey: tab.key,
        },
      },
    });
  },

  removeContentTab: (appNumber, tabKey) => {
    if (tabKey === '__home__') return;
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    const idx = ws.contentTabs.findIndex((t) => t.key === tabKey);
    const newTabs = ws.contentTabs.filter((t) => t.key !== tabKey);
    let newActiveKey = ws.activeContentTabKey;
    if (ws.activeContentTabKey === tabKey) {
      newActiveKey = newTabs[Math.min(idx - 1, newTabs.length - 1)].key;
    }
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: { ...ws, contentTabs: newTabs, activeContentTabKey: newActiveKey },
      },
    });
  },

  activateContentTab: (appNumber, tabKey) => {
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: { ...ws, activeContentTabKey: tabKey },
      },
    });
  },
}));
