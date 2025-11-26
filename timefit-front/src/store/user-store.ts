import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

import type { AuthUserProfile } from '@/types/auth/user';

import { useBusinessStore } from './business-store';

interface UserState {
  user: AuthUserProfile | null;
  isAuthenticated: boolean;
  _hasHydrated: boolean;
}

interface UserActions {
  setUser: (user: AuthUserProfile | null) => void;
  logout: () => void;
}

const initialState: UserState = {
  user: null,
  isAuthenticated: false,
  _hasHydrated: false,
};

export const useUserStore = create<UserState & UserActions>()(
  devtools(set => ({
    ...initialState,

    setUser: user =>
      set({
        user,
        isAuthenticated: !!user,
        _hasHydrated: true,
      }),

    logout: () => {
      set(initialState);
      useBusinessStore.getState().reset();
    },
  }))
);
