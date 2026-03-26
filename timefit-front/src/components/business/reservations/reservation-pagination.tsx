'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';

import type { PaginationInfo } from '@/types/business/reservation';
import { Button } from '@/components/ui/button';

interface ReservationPaginationProps {
  pagination: PaginationInfo;
  isLoading?: boolean;
  onPageChange: (page: number) => void;
}

export function ReservationPagination({
                                        pagination,
                                        isLoading = false,
                                        onPageChange,
                                      }: ReservationPaginationProps) {
  if (pagination.totalPages <= 1) return null;

  const getPageNumbers = () => {
    const total = pagination.totalPages;
    const current = pagination.currentPage;
    const delta = 2;
    const start = Math.max(0, current - delta);
    const end = Math.min(total - 1, current + delta);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  };

  const pageNumbers = getPageNumbers();

  return (
    <div className="flex flex-col items-center gap-2">
      <div className="flex items-center gap-1">
        {/* 이전 */}
        <Button
          variant="outline"
          size="icon"
          onClick={() => onPageChange(pagination.currentPage - 1)}
          disabled={!pagination.hasPrevious || isLoading}
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>

        {/* 첫 페이지 */}
        {pageNumbers[0] > 0 && (
          <>
            <Button
              variant="outline"
              size="icon"
              onClick={() => onPageChange(0)}
              disabled={isLoading}
            >
              1
            </Button>
            {pageNumbers[0] > 1 && (
              <span className="px-1 text-muted-foreground">...</span>
            )}
          </>
        )}

        {/* 페이지 번호들 */}
        {pageNumbers.map(page => (
          <Button
            key={page}
            variant={page === pagination.currentPage ? 'default' : 'outline'}
            size="icon"
            onClick={() => onPageChange(page)}
            disabled={isLoading}
          >
            {page + 1}
          </Button>
        ))}

        {/* 마지막 페이지 */}
        {pageNumbers[pageNumbers.length - 1] < pagination.totalPages - 1 && (
          <>
            {pageNumbers[pageNumbers.length - 1] < pagination.totalPages - 2 && (
              <span className="px-1 text-muted-foreground">...</span>
            )}
            <Button
              variant="outline"
              size="icon"
              onClick={() => onPageChange(pagination.totalPages - 1)}
              disabled={isLoading}
            >
              {pagination.totalPages}
            </Button>
          </>
        )}

        {/* 다음 */}
        <Button
          variant="outline"
          size="icon"
          onClick={() => onPageChange(pagination.currentPage + 1)}
          disabled={!pagination.hasNext || isLoading}
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>

      {/* 페이지 정보 */}
      <p className="text-sm text-muted-foreground">
        전체 {pagination.totalElements}건 중{' '}
        {pagination.currentPage * 20 + 1}~
        {Math.min((pagination.currentPage + 1) * 20, pagination.totalElements)}건
      </p>
    </div>
  );
}