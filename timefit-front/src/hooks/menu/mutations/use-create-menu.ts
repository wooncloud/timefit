import { useState } from 'react';
import { toast } from 'sonner';

import { menuService } from '@/services/menu/menu-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';
import type { Menu, CreateUpdateMenuRequest } from '@/types/menu/menu';

export function useCreateMenu(businessId: string) {
    const [loading, setLoading] = useState(false);

    const createMenu = async (
        data: CreateUpdateMenuRequest
    ): Promise<Menu | null> => {
        try {
            setLoading(true);

            const result = await menuService.createMenu(businessId, data);

            if (handleAuthError(result)) {
                return null;
            }

            if (!result.success || !result.data) {
                toast.error(result.message || '메뉴 생성에 실패했습니다.');
                return null;
            }

            toast.success('메뉴가 생성되었습니다.');
            return result.data;
        } catch (err) {
            console.error('메뉴 생성 실패:', err);
            toast.error('메뉴 생성 중 오류가 발생했습니다.');
            return null;
        } finally {
            setLoading(false);
        }
    };

    return {
        createMenu,
        loading,
    };
}
