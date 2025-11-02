'use client';

import { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Search } from 'lucide-react';
import { DatePicker } from './date-picker';

export function ReservationFilterToolbar() {
  const [startDate, setStartDate] = useState<Date>();
  const [endDate, setEndDate] = useState<Date>();

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
            <Select defaultValue="all">
              <SelectTrigger>
                <SelectValue placeholder="예약 상태" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">전체</SelectItem>
                <SelectItem value="pending">승인대기</SelectItem>
                <SelectItem value="confirmed">예약확정</SelectItem>
                <SelectItem value="completed">완료</SelectItem>
                <SelectItem value="cancelled">취소</SelectItem>
                <SelectItem value="noshow">노쇼</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex gap-2 md:col-span-1">
            <Input placeholder="고객명 입력" className="flex-1" />
            <Button size="icon">
              <Search className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
