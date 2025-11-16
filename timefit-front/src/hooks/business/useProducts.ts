'use client';

import { useState, useEffect } from 'react';
import { toast } from 'sonner';

import type {
  Product,
  CreateProductRequest,
  UpdateProductRequest,
} from '@/types/product/product';
import { handleAuthError } from '@/lib/api/handle-auth-error';

interface UseProductsResult {
  products: Product[] | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  createProduct: (data: CreateProductRequest) => Promise<boolean>;
  updateProduct: (id: string, data: UpdateProductRequest) => Promise<boolean>;
  deleteProduct: (id: string) => Promise<boolean>;
  saving: boolean;
  deleting: boolean;
}

/**
 * 서비스(메뉴) 목록을 조회하고 관리하는 Hook
 *
 * @param businessId - 조회할 업체 ID (UUID)
 * @returns 서비스 목록, 로딩 상태, 에러, CRUD 함수들
 *
 * @example
 * const { products, loading, error, refetch, createProduct, updateProduct, deleteProduct, saving, deleting } = useProducts('uuid-here');
 */
export function useProducts(businessId: string): UseProductsResult {
  const [products, setProducts] = useState<Product[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const fetchProducts = async () => {
    if (!businessId) {
      setError('업체 ID가 필요합니다.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/business/${businessId}/menu`);
      const result = await response.json();

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return;
      }

      if (!response.ok) {
        setError(result.message || '서비스 목록 조회에 실패했습니다.');
        setProducts(null);
        return;
      }

      if (result.success && result.data) {
        // 백엔드에서 menus 배열을 반환하므로 매핑
        const menuList = result.data.menus || [];

        // 백엔드 응답을 프론트엔드 Product 타입으로 변환
        const mappedProducts: Product[] = menuList.map((menu: any) => ({
          id: menu.menuId,
          businessId: menu.businessId,
          serviceName: menu.serviceName,
          businessCategoryId: menu.businessCategoryId,
          businessType: menu.businessType,
          categoryCode: menu.categoryCode,
          categoryName: menu.categoryName,
          price: menu.price,
          description: menu.description,
          orderType: menu.orderType,
          durationMinutes: menu.durationMinutes,
          imageUrl: menu.imageUrl,
          isActive: menu.isActive,
          createdAt: menu.createdAt,
          updatedAt: menu.updatedAt,
        }));

        setProducts(mappedProducts);
      } else {
        setError('서비스 목록을 찾을 수 없습니다.');
        setProducts(null);
      }
    } catch (err) {
      console.error('서비스 목록 조회 오류:', err);
      setError('서버 오류가 발생했습니다.');
      setProducts(null);
    } finally {
      setLoading(false);
    }
  };

  const createProduct = async (
    data: CreateProductRequest
  ): Promise<boolean> => {
    if (!businessId) {
      toast.error('업체 ID가 필요합니다.');
      return false;
    }

    setSaving(true);
    setError(null);

    try {
      const response = await fetch(`/api/business/${businessId}/menu`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      const result = await response.json();

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return false;
      }

      if (!response.ok) {
        toast.error(result.message || '서비스 생성에 실패했습니다.');
        return false;
      }

      if (result.success) {
        toast.success('서비스가 생성되었습니다.');
        // 생성 성공 후 목록 새로고침
        await fetchProducts();
        return true;
      } else {
        toast.error('서비스 생성에 실패했습니다.');
        return false;
      }
    } catch (err) {
      console.error('서비스 생성 오류:', err);
      toast.error('서버 오류가 발생했습니다.');
      return false;
    } finally {
      setSaving(false);
    }
  };

  const updateProduct = async (
    id: string,
    data: UpdateProductRequest
  ): Promise<boolean> => {
    if (!businessId) {
      toast.error('업체 ID가 필요합니다.');
      return false;
    }

    if (!id) {
      toast.error('서비스 ID가 필요합니다.');
      return false;
    }

    setSaving(true);
    setError(null);

    try {
      const response = await fetch(
        `/api/business/${businessId}/menu/${id}`,
        {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data),
        }
      );

      const result = await response.json();

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return false;
      }

      if (!response.ok) {
        toast.error(result.message || '서비스 수정에 실패했습니다.');
        return false;
      }

      if (result.success) {
        toast.success('서비스가 수정되었습니다.');
        // 수정 성공 후 목록 새로고침
        await fetchProducts();
        return true;
      } else {
        toast.error('서비스 수정에 실패했습니다.');
        return false;
      }
    } catch (err) {
      console.error('서비스 수정 오류:', err);
      toast.error('서버 오류가 발생했습니다.');
      return false;
    } finally {
      setSaving(false);
    }
  };

  const deleteProduct = async (id: string): Promise<boolean> => {
    if (!businessId) {
      toast.error('업체 ID가 필요합니다.');
      return false;
    }

    if (!id) {
      toast.error('서비스 ID가 필요합니다.');
      return false;
    }

    setDeleting(true);
    setError(null);

    try {
      const response = await fetch(
        `/api/business/${businessId}/menu/${id}`,
        {
          method: 'DELETE',
        }
      );

      const result = await response.json();

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return false;
      }

      if (!response.ok) {
        toast.error(result.message || '서비스 삭제에 실패했습니다.');
        return false;
      }

      if (result.success) {
        // 삭제 성공 후 목록 새로고침
        await fetchProducts();
        return true;
      } else {
        toast.error('서비스 삭제에 실패했습니다.');
        return false;
      }
    } catch (err) {
      console.error('서비스 삭제 오류:', err);
      toast.error('서버 오류가 발생했습니다.');
      return false;
    } finally {
      setDeleting(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [businessId]);

  return {
    products,
    loading,
    error,
    refetch: fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    saving,
    deleting,
  };
}
