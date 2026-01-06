import { useState } from 'react';
import { toast } from 'sonner';

import { menuService } from '@/services/menu/menu-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeleteMenu(businessId: string) {
  const [loading, setLoading] = useState(false);

  const deleteMenu = async (menuId: string): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await menuService.deleteMenu(businessId, menuId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '메뉴 삭제에 실패했습니다.');
        return false;
      }

      toast.success('메뉴가 삭제되었습니다.');
      return true;
    } catch (err) {
      console.error('메뉴 삭제 실패:', err);
      toast.error('메뉴 삭제 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    deleteMenu,
    loading,
  };
}
