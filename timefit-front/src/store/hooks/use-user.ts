import { useUserStore } from '../user-store';

/**
 * 사용자 정보를 관리하는 hook
 * 
 * @example
 * ```tsx
 * const { user, isAuthenticated, setUser, logout } = useUser();
 * ```
 */
export function useUser() {
    const user = useUserStore(state => state.user);
    const isAuthenticated = useUserStore(state => state.isAuthenticated);
    const setUser = useUserStore(state => state.setUser);
    const logout = useUserStore(state => state.logout);

    return {
        user,
        isAuthenticated,
        setUser,
        logout,
    };
}

/**
 * 사용자 정보만 가져오는 hook
 */
export function useUserInfo() {
    return useUserStore(state => state.user);
}

/**
 * 인증 상태만 가져오는 hook
 */
export function useIsAuthenticated() {
    return useUserStore(state => state.isAuthenticated);
}

/**
 * 사용자 액션들만 가져오는 hook
 */
export function useUserActions() {
    const setUser = useUserStore(state => state.setUser);
    const logout = useUserStore(state => state.logout);

    return {
        setUser,
        logout,
    };
}
