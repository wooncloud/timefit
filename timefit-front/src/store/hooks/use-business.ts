import { useBusinessStore } from '../business-store';

/**
 * 비즈니스 정보를 관리하는 hook
 * 
 * @example
 * ```tsx
 * const { business, loading, setBusiness } = useBusiness();
 * ```
 */
export function useBusiness() {
    const business = useBusinessStore(state => state.business);
    const loading = useBusinessStore(state => state.loading);
    const setBusiness = useBusinessStore(state => state.setBusiness);
    const setLoading = useBusinessStore(state => state.setLoading);
    const reset = useBusinessStore(state => state.reset);

    return {
        business,
        loading,
        setBusiness,
        setLoading,
        reset,
    };
}

/**
 * 비즈니스 정보만 가져오는 hook
 */
export function useBusinessInfo() {
    return useBusinessStore(state => state.business);
}

/**
 * 비즈니스 로딩 상태만 가져오는 hook
 */
export function useBusinessLoading() {
    return useBusinessStore(state => state.loading);
}

/**
 * 비즈니스 액션들만 가져오는 hook
 */
export function useBusinessActions() {
    const setBusiness = useBusinessStore(state => state.setBusiness);
    const setLoading = useBusinessStore(state => state.setLoading);
    const reset = useBusinessStore(state => state.reset);

    return {
        setBusiness,
        setLoading,
        reset,
    };
}
