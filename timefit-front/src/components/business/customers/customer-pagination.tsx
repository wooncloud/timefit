'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';

import { Button } from '@/components/ui/button';

interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

interface CustomerPaginationProps {
  pagination: PaginationInfo;
  isLoading?: boolean;
  onPageChange: (page: number) => void;
}

export function CustomerPagination({
                                     pagination,
                                     isLoading = false,
                                     onPageChange,
                                   }: CustomerPaginationProps) {
  if (pagination.totalPages <= 1) return null;

  const getPageNumbers = () => {
    const total = pagination.totalPages;
    const current = pagination.currentPage;
    const delta = 2;
    const start = Math.max(0, current - delta);
    const end = Math.min(total - 1, current + delta);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  };

  const handleClick = (page: number) => {
    if (isLoading || page === pagination.currentPage) return;
    onPageChange(page);
  };

  const pageNumbers = getPageNumbers();

  return (
    <div className="flex items-center gap-1">
      <Button
        variant="outline"
        size="icon"
        onClick={() => handleClick(pagination.currentPage - 1)}
        disabled={!pagination.hasPrevious || isLoading}
      >
        <ChevronLeft className="h-4 w-4" />
      </Button>

      {pageNumbers[0] > 0 && (
        <>
          <Button variant="outline" size="icon" onClick={() => handleClick(0)} disabled={isLoading}>1</Button>
          {pageNumbers[0] > 1 && <span className="px-1 text-muted-foreground">...</span>}
        </>
      )}

      {pageNumbers.map(page => (
        <Button
          key={page}
          variant={page === pagination.currentPage ? 'default' : 'outline'}
          size="icon"
          onClick={() => handleClick(page)}
          disabled={isLoading || page === pagination.currentPage}
        >
          {page + 1}
        </Button>
      ))}

      {pageNumbers[pageNumbers.length - 1] < pagination.totalPages - 1 && (
        <>
          {pageNumbers[pageNumbers.length - 1] < pagination.totalPages - 2 && (
            <span className="px-1 text-muted-foreground">...</span>
          )}
          <Button variant="outline" size="icon" onClick={() => handleClick(pagination.totalPages - 1)} disabled={isLoading}>
            {pagination.totalPages}
          </Button>
        </>
      )}

      <Button
        variant="outline"
        size="icon"
        onClick={() => handleClick(pagination.currentPage + 1)}
        disabled={!pagination.hasNext || isLoading}
      >
        <ChevronRight className="h-4 w-4" />
      </Button>
    </div>
  );
}