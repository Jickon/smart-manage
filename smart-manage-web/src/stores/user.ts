import { create } from 'zustand';

interface UserInfo {
  id: string;
  username: string;
  nickname: string;
}

interface UserState {
  token: string | null;
  userInfo: UserInfo | null;
  setToken: (token: string) => void;
  setUserInfo: (info: UserInfo) => void;
  logout: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  token: localStorage.getItem('token'),
  userInfo: null,
  setToken: (token) => {
    localStorage.setItem('token', token);
    set({ token });
  },
  setUserInfo: (userInfo) => set({ userInfo }),
  logout: () => {
    localStorage.removeItem('token');
    set({ token: null, userInfo: null });
  },
}));
