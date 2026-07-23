import { create } from 'zustand';
import type { UserInfoVO } from '@/types/api';

interface UserState {
  token: string | null;
  userInfo: UserInfoVO | null;
  setToken: (token: string) => void;
  setUserInfo: (info: UserInfoVO) => void;
  setThemeColor: (themeColor: string) => void;
  clearUser: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  token: localStorage.getItem('token'),
  userInfo: null,
  setToken: (token: string) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  setUserInfo: (userInfo: UserInfoVO) => set({ userInfo }),
  setThemeColor: (themeColor: string) =>
    set((state) => ({
      userInfo: state.userInfo ? { ...state.userInfo, themeColor } : null,
    })),
  clearUser: () => {
    localStorage.removeItem('token');
    set({ token: null, userInfo: null });
  },
}));
