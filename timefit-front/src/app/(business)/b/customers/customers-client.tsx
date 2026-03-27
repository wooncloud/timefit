'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
  BusinessCustomerDetail,
  BusinessCustomerItem,
  CustomerSortType,
} from '@/types/business/customer-business';
import { businessCustomerService } from '@/services/business/customer-business-service.client';
import { CustomerCountDisplay } from '@/components/business/customers/customer-count-display';
import { CustomerDetailModal } from '@/components/business/customers/customer-detail-modal';
import { CustomerFilterToolbar } from '@/components/business/customers/customer-filter-toolbar';
import { CustomerMemoDialog } from '@/components/business/customers/customer-memo-dialog';
import { CustomerTableEmpty } from '@/components/business/customers/customer-table-empty';
import { CustomerTableHeader } from '@/components/business/customers/customer-table-header';
import { CustomerTableRow } from '@/components/business/customers/customer-table-row';
import { CustomerPagination } from '@/components/business/customers/customer-pagination';
import { Table, TableBody } from '@/components/ui/table';

interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

interface CustomersClientProps {
  initialCustomers: BusinessCustomerItem[];
  initialPagination: PaginationInfo;
  businessId: string;
}

export function CustomersClient({
                                  initialCustomers,
                                  initialPagination,
                                  businessId,
                                }: CustomersClientProps) {
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

  // 고객 목록 fetch
  const fetchCustomers = async (
    keyword: string,
    sortBy: CustomerSortType,
    page: number
  ) => {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    params.append('sortBy', sortBy);
    params.append('page', page.toString());
    params.append('size', '20');

    const response = await fetch(
      `/api/business/${businessId}/customers?${params.toString()}`
    );
    return response.json();
  };

  // 검색/정렬 변경
  const handleSearch = async (keyword: string, sortBy: CustomerSortType) => {
    try {
      setIsLoading(true);
      const result = await fetchCustomers(keyword, sortBy, 0);
      if (!result.success) { toast.error('조회에 실패했습니다.'); return; }
      setCurrentKeyword(keyword);
      setCurrentSortBy(sortBy);
      setCustomers(result.data.customers);
      setPagination(result.data.pagination);
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
      const result = await fetchCustomers(currentKeyword, currentSortBy, page);
      if (!result.success) { toast.error('페이지 로드에 실패했습니다.'); return; }
      setCustomers(result.data.customers);
      setPagination(result.data.pagination);
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

  // 메모 편집 열기
  const handleMemo = (customerId: string, currentMemo: string | null) => {
    setMemoDialog({ isOpen: true, customerId, currentMemo });
  };

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
    setMemoDialog({ isOpen: false, customerId: '', currentMemo: null });
  };

  return (
    <div className="space-y-6">
      <CustomerFilterToolbar
        onSearch={handleSearch}
        isLoading={isLoading}
      />

      <div className="rounded-md border">
        <Table>
          <CustomerTableHeader />
          <TableBody>
            {customers.length === 0 ? (
              <CustomerTableEmpty />
            ) : (
              customers.map(customer => (
                <CustomerTableRow
                  key={customer.customerId}
                  customer={customer}
                  onDetail={handleDetail}
                  onMemo={handleMemo}
                />
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <div className="flex items-center justify-between">
        <CustomerCountDisplay count={pagination.totalElements} />
        <CustomerPagination
          pagination={pagination}
          isLoading={isLoading}
          onPageChange={handlePageChange}
        />
      </div>

      <CustomerDetailModal
        detail={detailModal.detail}
        isOpen={detailModal.isOpen}
        onClose={() => setDetailModal({ isOpen: false, detail: null })}
        onMemo={handleMemo}
      />

      <CustomerMemoDialog
        isOpen={memoDialog.isOpen}
        currentMemo={memoDialog.currentMemo}
        onClose={() => setMemoDialog({ isOpen: false, customerId: '', currentMemo: null })}
        onSave={handleMemoSave}
      />
    </div>
  );
}