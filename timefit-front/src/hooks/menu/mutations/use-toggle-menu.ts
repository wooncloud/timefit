import { useState } from 'react';
import { toast } from 'sonner';

import type { Menu } from '@/types/menu/menu';
import { menuService } from '@/services/menu/menu-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useToggleMenu(businessId: string) {
  const [loading, setLoading] = useState(false);

  const toggleMenu = async (menuId: string): Promise<Menu | null> => {
    try {
      setLoading(true);

      const result = await menuService.toggleMenu(businessId, menuId);

      if (handleAuthError(result)) {
        return null;
      }

      if (!result.success || !result.data) {
        toast.error(result.message || '메뉴 상태 변경에 실패했습니다.');
        return null;
      }

      toast.success('메뉴 상태가 변경되었습니다.');
      return result.data;
    } catch (err) {
      console.error('메뉴 상태 변경 실패:', err);
      toast.error('메뉴 상태 변경 중 오류가 발생했습니다.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return {
    toggleMenu,
    loading,
  };
}
