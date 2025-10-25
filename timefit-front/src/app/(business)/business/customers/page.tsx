'use client';

import { useState, useMemo } from 'react';
import { CustomerFilterToolbar } from '@/components/business/customers/customer-filter-toolbar';
import { CustomerTableHeader } from '@/components/business/customers/customer-table-header';
import { CustomerTableRow } from '@/components/business/customers/customer-table-row';
import { CustomerTableEmpty } from '@/components/business/customers/customer-table-empty';
import { CustomerCountDisplay } from '@/components/business/customers/customer-count-display';
import { Table, TableBody } from '@/components/ui/table';
import { mockCustomers } from '@/lib/mock';

export default function Page() {
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('lastVisit');

  // 검색 및 정렬
  const filteredAndSortedCustomers = useMemo(() => {
    let result = [...mockCustomers];

    // 검색 필터
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(
        (customer) =>
          customer.name.toLowerCase().includes(query) ||
          customer.phone.includes(query)
      );
    }

    // 정렬
    result.sort((a, b) => {
      switch (sortBy) {
        case 'lastVisit':
          return (
            new Date(b.lastVisitDate).getTime() -
            new Date(a.lastVisitDate).getTime()
          );
        case 'totalVisits':
          return b.totalVisits - a.totalVisits;
        case 'name':
          return a.name.localeCompare(b.name, 'ko');
        case 'firstVisit':
          return (
            new Date(b.firstVisitDate).getTime() -
            new Date(a.firstVisitDate).getTime()
          );
        default:
          return 0;
      }
    });

    return result;
  }, [searchQuery, sortBy]);

  return (
    <div className="space-y-6">
      <CustomerFilterToolbar
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        sortBy={sortBy}
        onSortChange={setSortBy}
      />

      <div className="rounded-md border">
        <Table>
          <CustomerTableHeader />
          <TableBody>
            {filteredAndSortedCustomers.length === 0 ? (
              <CustomerTableEmpty />
            ) : (
              filteredAndSortedCustomers.map((customer) => (
                <CustomerTableRow key={customer.id} customer={customer} />
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <CustomerCountDisplay count={filteredAndSortedCustomers.length} />
    </div>
  );
}
