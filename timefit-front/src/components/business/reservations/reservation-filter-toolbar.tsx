'use client';

import { useEffect, useState } from 'react';
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

import { DatePicker } from '../../ui/date-picker';

export interface FilterValues {
  status?: string;
  startDate?: string;  // YYYY-MM-DD
  endDate?: string;    // YYYY-MM-DD
  customerName?: string;
}

interface ReservationFilterToolbarProps {
  onSearch: (filters: FilterValues) => void;
}

const MAX_RANGE_DAYS = 365;

export function ReservationFilterToolbar({ onSearch }: ReservationFilterToolbarProps) {
  const today = dayjs();

  // 기본값: 오늘 기준 ±15일
  const [startDate, setStartDate] = useState<Date>(
    today.subtract(15, 'day').toDate()
  );
  const [endDate, setEndDate] = useState<Date>(
    today.add(15, 'day').toDate()
  );
  const [status, setStatus] = useState<string>('all');
  const [customerName, setCustomerName] = useState('');
  const [dateError, setDateError] = useState<string>('');

  const validateDateRange = (start: Date | undefined, end: Date | undefined): boolean => {
    if (!start || !end) {
      setDateError('');
      return true;
    }

    const diffDays = dayjs(end).diff(dayjs(start), 'day');

    if (diffDays < 0) {
      setDateError('종료일은 시작일 이후여야 합니다.');
      return false;
    }

    if (diffDays > MAX_RANGE_DAYS) {
      setDateError(`조회 기간은 최대 ${MAX_RANGE_DAYS}일(1년)입니다.`);
      return false;
    }

    setDateError('');
    return true;
  };

  const handleStartDateChange = (date: Date | undefined) => {
    setStartDate(date!);
    validateDateRange(date, endDate);
  };

  const handleEndDateChange = (date: Date | undefined) => {
    setEndDate(date!);
    validateDateRange(startDate, date);
  };

  const handleSearch = () => {
    if (!validateDateRange(startDate, endDate)) return;

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
        <div className="space-y-3">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
            <div className="md:col-span-1">
              <DatePicker
                date={startDate}
                onDateChange={handleStartDateChange}
                placeholder="시작일"
              />
            </div>
            <div className="md:col-span-1">
              <DatePicker
                date={endDate}
                onDateChange={handleEndDateChange}
                placeholder="종료일"
                // 시작일 선택 시 1년 이후 날짜 선택 불가
                disabled={startDate
                  ? { before: startDate, after: dayjs(startDate).add(MAX_RANGE_DAYS, 'day').toDate() }
                  : undefined
                }
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
              <Button
                size="icon"
                onClick={handleSearch}
                disabled={!!dateError}
              >
                <Search className="h-4 w-4" />
              </Button>
            </div>
          </div>

          {/* 날짜 에러 메시지 */}
          {dateError && (
            <p className="text-sm text-destructive">{dateError}</p>
          )}
        </div>
      </CardContent>
    </Card>
  );
}