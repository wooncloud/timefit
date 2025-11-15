import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import type { AuthBusinessInfo } from '@/types/auth/user';

interface BusinessState {
  business: AuthBusinessInfo | null;
  loading: boolean;
  _hasHydrated: boolean;
}

interface BusinessActions {
  setBusiness: (business: AuthBusinessInfo | null) => void;
  setLoading: (loading: boolean) => void;
  reset: () => void;
}

const initialState: BusinessState = {
  business: null,
  loading: false,
  _hasHydrated: false,
};

export const useBusinessStore = create<BusinessState & BusinessActions>()(
  devtools(
    persist(
      (set) => ({
        ...initialState,

        setBusiness: (business) => set({ business }),

        setLoading: (loading) => set({ loading }),

        reset: () => set(initialState),
      }),
      {
        name: 'business-store',
        onRehydrateStorage: () => (state) => {
          state && (state._hasHydrated = true);
        },
      }
    )
  )
);
