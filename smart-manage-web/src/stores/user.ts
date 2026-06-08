import { create } from 'zustand';
import type { UserInfoVO } from '@/types/api';

interface UserState {
  token: string | null;
  userInfo: UserInfoVO | null;
  setToken: (token: string) => void;
  setUserInfo: (info: UserInfoVO) => void;
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
  clearUser: () => {
    localStorage.removeItem('token');
    set({ token: null, userInfo: null });
  },
}));
