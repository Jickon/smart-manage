import { create } from 'zustand';
import {
  createAddNewTabKey,
  createBillTabKey,
  createListTabKey,
} from '@/cloud/common/page/tabKeys';
import { OperationType } from '@/cloud/common/page/types';
import type { MenuVO } from '@/types/api';
import type { AppVO } from '@/cloud/sys/app/types';

export interface ContentTabItem {
  key: string;
  label: string;
  closable: boolean;
  componentKey?: string;
  pageType?: 'LIST' | 'EDIT' | 'CUSTOM';
  operationType?: OperationType;
  billId?: string;
  /** 新增页临时标记 — 保存后替换为真实单据 tab */
  temporary?: boolean;
}

interface WorkspaceState {
  appInfo: AppVO;
  menuTree: MenuVO | null;
  menuLoading: boolean;
  contentTabs: ContentTabItem[];
  activeContentTabKey: string;
  activeContentTabHistory: string[];
}

interface WorkbenchState {
  workspaces: Record<string, WorkspaceState>;
  initWorkspace: (appNumber: string, appInfo: AppVO) => void;
  destroyWorkspace: (appNumber: string) => void;
  setMenuTree: (appNumber: string, menuTree: MenuVO) => void;
  setMenuLoading: (appNumber: string, loading: boolean) => void;
  /** 内部方法 — 添加/激活 content tab（去重） */
  addContentTab: (appNumber: string, tab: ContentTabItem) => void;
  openListTab: (appNumber: string, componentKey: string, label: string) => void;
  openAddNewTab: (appNumber: string, componentKey: string, label: string) => void;
  openBillTab: (
    appNumber: string,
    componentKey: string,
    label: string,
    billId: string,
    operationType: OperationType,
  ) => void;
  replaceContentTab: (appNumber: string, oldTabKey: string, nextTab: ContentTabItem) => void;
  removeContentTab: (appNumber: string, tabKey: string) => void;
  activateContentTab: (appNumber: string, tabKey: string) => void;
}

function defaultWorkspace(appInfo: AppVO): WorkspaceState {
  return {
    appInfo,
    menuTree: null,
    menuLoading: false,
    contentTabs: [{ key: '__home__', label: '应用首页', closable: false }],
    activeContentTabKey: '__home__',
    activeContentTabHistory: ['__home__'],
  };
}

function pushContentHistory(history: string[], key: string) {
  return [...history.filter((item) => item !== key), key];
}

export const useWorkbenchStore = create<WorkbenchState>((set, get) => ({
  workspaces: {},

  initWorkspace: (appNumber, appInfo) => {
    const { workspaces } = get();
    if (workspaces[appNumber]) return;
    set({ workspaces: { ...workspaces, [appNumber]: defaultWorkspace(appInfo) } });
  },

  destroyWorkspace: (appNumber) => {
    const { workspaces } = get();
    if (!workspaces[appNumber]) return;
    const next = { ...workspaces };
    delete next[appNumber];
    set({ workspaces: next });
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
          [appNumber]: {
            ...ws,
            activeContentTabKey: tab.key,
            activeContentTabHistory: pushContentHistory(ws.activeContentTabHistory, tab.key),
          },
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
          activeContentTabHistory: pushContentHistory(ws.activeContentTabHistory, tab.key),
        },
      },
    });
  },

  openListTab: (appNumber, componentKey, label) => {
    const state = get();
    const ws = state.workspaces[appNumber];
    if (!ws) return;
    state.addContentTab(appNumber, {
      key: createListTabKey(componentKey),
      label,
      closable: true,
      componentKey,
      pageType: 'LIST',
    });
  },

  openAddNewTab: (appNumber, componentKey, label) => {
    const state = get();
    const ws = state.workspaces[appNumber];
    if (!ws) return;
    state.addContentTab(appNumber, {
      key: createAddNewTabKey(componentKey),
      label,
      closable: true,
      componentKey,
      pageType: 'EDIT',
      operationType: OperationType.ADDNEW,
      temporary: true,
    });
  },

  openBillTab: (appNumber, componentKey, label, billId, operationType) => {
    const state = get();
    const ws = state.workspaces[appNumber];
    if (!ws) return;
    state.addContentTab(appNumber, {
      key: createBillTabKey(componentKey, billId),
      label,
      closable: true,
      componentKey,
      pageType: 'EDIT',
      operationType,
      billId,
    });
  },

  replaceContentTab: (appNumber, oldTabKey, nextTab) => {
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    const contentTabs = ws.contentTabs.map((tab) => (tab.key === oldTabKey ? nextTab : tab));
    const activeContentTabKey =
      ws.activeContentTabKey === oldTabKey ? nextTab.key : ws.activeContentTabKey;
    const activeContentTabHistory = pushContentHistory(
      ws.activeContentTabHistory.filter((key) => key !== oldTabKey),
      activeContentTabKey,
    );
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: { ...ws, contentTabs, activeContentTabKey, activeContentTabHistory },
      },
    });
  },

  removeContentTab: (appNumber, tabKey) => {
    if (tabKey === '__home__') return;
    const { workspaces } = get();
    const ws = workspaces[appNumber];
    if (!ws) return;
    const newTabs = ws.contentTabs.filter((t) => t.key !== tabKey);
    const remainingKeys = new Set(newTabs.map((tab) => tab.key));
    const nextHistory = ws.activeContentTabHistory.filter((key) => key !== tabKey);
    let newActiveKey = ws.activeContentTabKey;
    if (ws.activeContentTabKey === tabKey) {
      newActiveKey =
        [...ws.activeContentTabHistory]
          .reverse()
          .find((key) => key !== tabKey && remainingKeys.has(key)) ?? '__home__';
    }
    set({
      workspaces: {
        ...workspaces,
        [appNumber]: {
          ...ws,
          contentTabs: newTabs,
          activeContentTabKey: newActiveKey,
          activeContentTabHistory: pushContentHistory(nextHistory, newActiveKey),
        },
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
        [appNumber]: {
          ...ws,
          activeContentTabKey: tabKey,
          activeContentTabHistory: pushContentHistory(ws.activeContentTabHistory, tabKey),
        },
      },
    });
  },
}));
