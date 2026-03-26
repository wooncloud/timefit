'use client';

import { useState } from 'react';
import { Search } from 'lucide-react';
import dayjs from 'dayjs';

import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

import { DatePicker } from './date-picker';

export interface FilterValues {
  status?: string;
  startDate?: string;  // YYYY-MM-DD
  endDate?: string;    // YYYY-MM-DD
  customerName?: string;
}

interface ReservationFilterToolbarProps {
  onSearch: (filters: FilterValues) => void;
}

export function ReservationFilterToolbar({ onSearch }: ReservationFilterToolbarProps) {
  const [startDate, setStartDate] = useState<Date>();
  const [endDate, setEndDate] = useState<Date>();
  const [status, setStatus] = useState<string>('all');
  const [customerName, setCustomerName] = useState('');

  const handleSearch = () => {
    onSearch({
      status: status === 'all' ? undefined : status,
      startDate: startDate ? dayjs(startDate).format('YYYY-MM-DD') : undefined,
      endDate: endDate ? dayjs(endDate).format('YYYY-MM-DD') : undefined,
      customerName: customerName.trim() || undefined,
    });
  };

  return (
    <Card>
      <CardContent className="pt-6">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <div className="md:col-span-1">
            <DatePicker
              date={startDate}
              onDateChange={setStartDate}
              placeholder="시작일"
            />
          </div>
          <div className="md:col-span-1">
            <DatePicker
              date={endDate}
              onDateChange={setEndDate}
              placeholder="종료일"
            />
          </div>
          <div className="md:col-span-1">
            <Select value={status} onValueChange={setStatus}>
              <SelectTrigger>
                <SelectValue placeholder="예약 상태" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">전체</SelectItem>
                <SelectItem value="PENDING">승인대기</SelectItem>
                <SelectItem value="CONFIRMED">예약확정</SelectItem>
                <SelectItem value="COMPLETED">완료</SelectItem>
                <SelectItem value="CANCELLED">취소</SelectItem>
                <SelectItem value="NO_SHOW">노쇼</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex gap-2 md:col-span-1">
            <Input
              value={customerName}
              onChange={e => setCustomerName(e.target.value)}
              placeholder="고객명 입력"
              className="flex-1"
              onKeyDown={e => e.key === 'Enter' && handleSearch()}
            />
            <Button size="icon" onClick={handleSearch}>
              <Search className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}