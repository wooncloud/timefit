import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import type { MyBusinessItem } from '@/types/business/myBusiness';

interface BusinessState {
  business: MyBusinessItem | null;
  businesses: MyBusinessItem[];
  loading: boolean;
}

interface BusinessActions {
  setBusiness: (business: MyBusinessItem | null) => void;
  setLoading: (loading: boolean) => void;
  reset: () => void;
}

const initialState: BusinessState = {
  business: null,
  businesses: [],
  loading: false,
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
      }
    )
  )
);
