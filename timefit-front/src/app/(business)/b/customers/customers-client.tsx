'use client';

import type {
  BusinessCustomerItem,
} from '@/types/business/customer-business';
import type { PaginationInfo } from '@/types/business/reservation';
import { useCustomers } from '@/hooks/business/use-customers';
import { CustomerCountDisplay } from '@/components/business/customers/customer-count-display';
import { CustomerDetailModal } from '@/components/business/customers/customer-detail-modal';
import { CustomerFilterToolbar } from '@/components/business/customers/customer-filter-toolbar';
import { CustomerMemoDialog } from '@/components/business/customers/customer-memo-dialog';
import { CustomerTableEmpty } from '@/components/business/customers/customer-table-empty';
import { CustomerTableHeader } from '@/components/business/customers/customer-table-header';
import { CustomerTableRow } from '@/components/business/customers/customer-table-row';
import { CustomerPagination } from '@/components/business/customers/customer-pagination';
import { Card, CardContent } from '@/components/ui/card';
import { Table, TableBody } from '@/components/ui/table';

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
  const {
    customers,
    pagination,
    isLoading,
    detailModal,
    memoDialog,
    handleSearch,
    handlePageChange,
    handleDetail,
    closeDetailModal,
    handleMemo,
    closeMemoDialog,
    handleMemoSave,
  } = useCustomers({ initialCustomers, initialPagination, businessId });

  return (
    <div className="space-y-6">
      <CustomerFilterToolbar
        onSearch={handleSearch}
        isLoading={isLoading}
      />

      <Card>
        <CardContent className="p-4">
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
        </CardContent>
      </Card>

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
        onClose={closeDetailModal}
        onMemo={handleMemo}
      />

      <CustomerMemoDialog
        isOpen={memoDialog.isOpen}
        currentMemo={memoDialog.currentMemo}
        onClose={closeMemoDialog}
        onSave={handleMemoSave}
      />
    </div>
  );
}