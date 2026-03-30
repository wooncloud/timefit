'use client';

import { useState } from 'react';
import { Search } from 'lucide-react';

import type { CustomerSortType } from '@/types/business/customer-business';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface CustomerFilterToolbarProps {
  onSearch: (keyword: string, sortBy: CustomerSortType) => void;
  isLoading?: boolean;
}

export function CustomerFilterToolbar({
                                        onSearch,
                                        isLoading = false,
                                      }: CustomerFilterToolbarProps) {
  const [keyword, setKeyword] = useState('');
  const [sortBy, setSortBy] = useState<CustomerSortType>('LAST_VISIT');

  const handleSearch = () => {
    onSearch(keyword.trim(), sortBy);
  };

  const handleSortChange = (value: CustomerSortType) => {
    setSortBy(value);
    onSearch(keyword.trim(), value);
  };

  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div className="relative max-w-sm flex-1 flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="이름, 전화번호로 검색"
            value={keyword}
            onChange={e => setKeyword(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSearch()}
            className="pl-9"
            disabled={isLoading}
          />
        </div>
        <Button onClick={handleSearch} disabled={isLoading} size="sm">
          검색
        </Button>
      </div>

      <Select value={sortBy} onValueChange={handleSortChange}>
        <SelectTrigger className="w-full sm:w-[180px]">
          <SelectValue placeholder="정렬 기준" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="LAST_VISIT">최근 방문일</SelectItem>
          <SelectItem value="TOTAL_VISITS">총 방문 횟수</SelectItem>
          <SelectItem value="NAME">이름순</SelectItem>
          <SelectItem value="FIRST_VISIT">첫 방문일</SelectItem>
        </SelectContent>
      </Select>
    </div>
  );
}