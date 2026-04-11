import { useState } from 'react';
import { toast } from 'sonner';

import type {
  BusinessCustomerDetail,
  BusinessCustomerItem,
  CustomerSortType,
} from '@/types/business/customer-business';
import type { PaginationInfo } from '@/types/business/reservation';
import { businessCustomerService } from '@/services/business/customer-business-service.client';

interface UseCustomersProps {
  initialCustomers: BusinessCustomerItem[];
  initialPagination: PaginationInfo;
  businessId: string;
}

export function useCustomers({
                               initialCustomers,
                               initialPagination,
                               businessId,
                             }: UseCustomersProps) {
  const [customers, setCustomers] = useState<BusinessCustomerItem[]>(initialCustomers);
  const [pagination, setPagination] = useState<PaginationInfo>(initialPagination);
  const [isLoading, setIsLoading] = useState(false);
  const [currentKeyword, setCurrentKeyword] = useState('');
  const [currentSortBy, setCurrentSortBy] = useState<CustomerSortType>('LAST_VISIT');

  // 상세 모달
  const [detailModal, setDetailModal] = useState<{
    isOpen: boolean;
    detail: BusinessCustomerDetail | null;
  }>({ isOpen: false, detail: null });

  // 메모 다이얼로그
  const [memoDialog, setMemoDialog] = useState<{
    isOpen: boolean;
    customerId: string;
    currentMemo: string | null;
  }>({ isOpen: false, customerId: '', currentMemo: null });

  // 검색/정렬 변경
  const handleSearch = async (keyword: string, sortBy: CustomerSortType) => {
    try {
      setIsLoading(true);
      const result = await businessCustomerService.getCustomers(businessId, {
        keyword, sortBy, page: 0, size: 20,
      });
      if (!result.success) { toast.error('조회에 실패했습니다.'); return; }
      setCurrentKeyword(keyword);
      setCurrentSortBy(sortBy);
      setCustomers(result.data!.customers);
      setPagination(result.data!.pagination);
    } catch {
      toast.error('조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 페이지 이동
  const handlePageChange = async (page: number) => {
    try {
      setIsLoading(true);
      const result = await businessCustomerService.getCustomers(businessId, {
        keyword: currentKeyword, sortBy: currentSortBy, page, size: 20,
      });
      if (!result.success) { toast.error('페이지 로드에 실패했습니다.'); return; }
      setCustomers(result.data!.customers);
      setPagination(result.data!.pagination);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch {
      toast.error('페이지 로드 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 고객 상세
  const handleDetail = async (customerId: string) => {
    const result = await businessCustomerService.getCustomerDetail(businessId, customerId);
    if (!result.success || !result.data) {
      toast.error(result.message || '상세 정보를 불러오는 데 실패했습니다.');
      return;
    }
    setDetailModal({ isOpen: true, detail: result.data });
  };

  const closeDetailModal = () => setDetailModal({ isOpen: false, detail: null });

  // 메모 편집 열기
  const handleMemo = (customerId: string, currentMemo: string | null) => {
    setMemoDialog({ isOpen: true, customerId, currentMemo });
  };

  const closeMemoDialog = () =>
    setMemoDialog({ isOpen: false, customerId: '', currentMemo: null });

  // 메모 저장
  const handleMemoSave = async (memo: string | null) => {
    const result = await businessCustomerService.upsertCustomerMemo(
      businessId,
      memoDialog.customerId,
      memo
    );
    if (!result.success) {
      toast.error(result.message || '메모 저장에 실패했습니다.');
      return;
    }
    setCustomers(prev => prev.map(c =>
      c.customerId === memoDialog.customerId ? { ...c, memo } : c
    ));
    toast.success('메모가 저장되었습니다.');
    closeMemoDialog();
  };

  return {
    // 상태
    customers,
    pagination,
    isLoading,
    detailModal,
    memoDialog,
    // 핸들러
    handleSearch,
    handlePageChange,
    handleDetail,
    closeDetailModal,
    handleMemo,
    closeMemoDialog,
    handleMemoSave,
  };
}