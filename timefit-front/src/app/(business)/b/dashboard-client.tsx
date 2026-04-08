'use client';

import Link from 'next/link';
import dayjs from 'dayjs';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { RESERVATION_STATUS_LABEL } from '@/lib/constants/reservation-status';

interface WeeklyData {
  label: string;
  count: number;
}

interface DashboardClientProps {
  businessId: string;
  todayTotal: number;
  todayConfirmed: number;
  todayPending: number;
  pendingReservations: BusinessReservationItem[];
  totalCustomers: number;
  newCustomers: number;
  noShowRate: number;
  weeklyData: WeeklyData[];
}

// 통계 카드
function StatCard({
                    label,
                    value,
                    sub,
                    valueColor,
                  }: {
  label: string;
  value: string | number;
  sub?: string;
  valueColor?: string;
}) {
  return (
    <div className="bg-muted/50 rounded-lg px-4 py-3">
      <p className="text-xs text-muted-foreground mb-2">{label}</p>
      <p className={`text-2xl font-medium ${valueColor ?? ''}`}>{value}</p>
      {sub && <p className="text-xs text-muted-foreground mt-1">{sub}</p>}
    </div>
  );
}

// 바 차트
function BarChart({ data }: { data: WeeklyData[] }) {
  const max = Math.max(...data.map(d => d.count), 1);

  return (
    <div className="flex items-end gap-1.5 h-44">
      {data.map((d, i) => (
        <div key={i} className="flex-1 flex flex-col items-center gap-1">
          <span className="text-[10px] text-muted-foreground">{d.count}</span>
          <div
            className="w-full rounded-t bg-teal-400/75 hover:bg-teal-400 transition-opacity min-h-[2px]"
            style={{ height: `${Math.round((d.count / max) * 140)}px` }}
          />
          <span className="text-[10px] text-muted-foreground whitespace-nowrap">{d.label}</span>
        </div>
      ))}
    </div>
  );
}

export function DashboardClient({
                                  businessId,
                                  todayTotal,
                                  todayConfirmed,
                                  todayPending,
                                  pendingReservations,
                                  totalCustomers,
                                  newCustomers,
                                  noShowRate,
                                  weeklyData,
                                }: DashboardClientProps) {
  return (
    <div className="space-y-4">
      {/* 통계 카드 5개 */}
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
        <StatCard
          label="오늘 예약"
          value={todayTotal}
          sub={`확정 ${todayConfirmed} · 대기 ${todayPending}`}
          valueColor="text-teal-500"
        />
        <StatCard
          label="승인 대기"
          value={pendingReservations.length}
          sub="즉시 처리 필요"
          valueColor={pendingReservations.length > 0 ? 'text-amber-600' : undefined}
        />
        <StatCard
          label="총 고객"
          value={totalCustomers}
          sub="누적"
        />
        <StatCard
          label="신규 고객"
          value={newCustomers}
          sub="이번 달"
          valueColor="text-teal-500"
        />
        <StatCard
          label="노쇼율"
          value={`${noShowRate}%`}
          sub="이번 달 기준"
          valueColor={noShowRate >= 10 ? 'text-destructive' : undefined}
        />
      </div>

      {/* 하단 2열 */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-[1fr_300px]">
        {/* 예약 추이 차트 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-muted-foreground font-normal">
              예약 추이 — 최근 8주
            </CardTitle>
          </CardHeader>
          <CardContent>
            <BarChart data={weeklyData} />
          </CardContent>
        </Card>

        {/* 승인 대기 예약 */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm text-muted-foreground font-normal">
              승인 대기 예약
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            {pendingReservations.length === 0 ? (
              <p className="text-sm text-muted-foreground py-4 text-center">
                대기 중인 예약이 없습니다
              </p>
            ) : (
              <div className="divide-y divide-border">
                {pendingReservations.slice(0, 5).map(r => (
                  <div key={r.reservationId} className="flex items-center gap-2.5 py-2.5">
                    <div className="w-1.5 h-1.5 rounded-full bg-orange-400 shrink-0" />
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-1.5">
                        <span className="text-sm font-medium truncate">{r.customerName}</span>
                        <Badge variant="secondary">{RESERVATION_STATUS_LABEL['PENDING']}</Badge>
                      </div>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        {r.reservationDuration}분
                      </p>
                    </div>
                    <span className="text-xs text-muted-foreground whitespace-nowrap">
                      {dayjs(r.reservationDate).isSame(dayjs(), 'day') ? '오늘' : r.reservationDate.slice(5)}
                      {' '}{r.reservationTime.slice(0, 5)}
                    </span>
                  </div>
                ))}
              </div>
            )}
            <Button
              variant="outline"
              size="sm"
              className="w-full mt-3 text-xs text-muted-foreground"
              asChild
            >
              <Link href={`/b/reservations`}>
                예약 현황 바로가기 →
              </Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}