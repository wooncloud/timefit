'use client';

import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Search } from 'lucide-react';

interface CustomerFilterToolbarProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  sortBy: string;
  onSortChange: (value: string) => void;
}

export function CustomerFilterToolbar({
  searchQuery,
  onSearchChange,
  sortBy,
  onSortChange,
}: CustomerFilterToolbarProps) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div className="relative flex-1 max-w-sm">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="이름, 전화번호로 검색"
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          className="pl-9"
        />
      </div>

      <Select value={sortBy} onValueChange={onSortChange}>
        <SelectTrigger className="w-full sm:w-[180px]">
          <SelectValue placeholder="정렬 기준" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="lastVisit">최근 방문일</SelectItem>
          <SelectItem value="totalVisits">총 방문 횟수</SelectItem>
          <SelectItem value="name">이름순</SelectItem>
          <SelectItem value="firstVisit">첫 방문일</SelectItem>
        </SelectContent>
      </Select>
    </div>
  );
}
