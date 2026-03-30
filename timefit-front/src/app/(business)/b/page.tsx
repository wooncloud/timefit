import dayjs from 'dayjs';

import { getBusinessReservations, getBusinessReservationStats } from '@/services/reservation/reservation-business-service';
import { getBusinessCustomers } from '@/services/business/customer-business-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { DashboardClient } from './dashboard-client';

export default async function BusinessMainPage() {
    const businessId = await getBusinessId();
    const today = dayjs();

    // 날짜 범위 계산
    const todayStr = today.format('YYYY-MM-DD');
    const monthStart = today.startOf('month').format('YYYY-MM-DD');
    const monthEnd = today.endOf('month').format('YYYY-MM-DD');
    // 최근 8주
    const weekStart = today.subtract(7, 'week').startOf('week').format('YYYY-MM-DD');

    const [
        todayReservations,
        pendingReservations,
        monthStats,
        allCustomers,
        recentReservations,
    ] = await Promise.all([
        // 오늘 예약
        getBusinessReservations(businessId, { startDate: todayStr, endDate: todayStr, size: 100 }),
        // 승인 대기 (오늘 이후)
        getBusinessReservations(businessId, { status: 'PENDING', startDate: todayStr, endDate: today.add(30, 'day').format('YYYY-MM-DD'), size: 10 }),
        // 이번 달 통계
        getBusinessReservationStats(businessId, { startDate: monthStart, endDate: monthEnd }),
        // 고객 목록 (총 고객 수, 신규 고객)
        getBusinessCustomers(businessId, { sortBy: 'FIRST_VISIT', size: 100 }),
        // 최근 8주 예약 (차트용)
        getBusinessReservations(businessId, { startDate: weekStart, endDate: todayStr, size: 100 }),
    ]);

    // 오늘 예약 집계
    const todayConfirmed = todayReservations.reservations.filter(r => r.status === 'CONFIRMED').length;
    const todayPending = todayReservations.reservations.filter(r => r.status === 'PENDING').length;
    const todayTotal = todayReservations.reservations.length;

    // 총 고객 수
    const totalCustomers = allCustomers.pagination.totalElements;

    // 신규 고객 (이번 달 첫 방문)
    const newCustomers = allCustomers.customers.filter(c =>
        c.firstVisitDate >= monthStart && c.firstVisitDate <= monthEnd
    ).length;

    // 노쇼율
    const totalThisMonth = Object.values(monthStats).reduce((a, b) => a + Number(b), 0);
    const noShowRate = totalThisMonth > 0
        ? Math.round((Number(monthStats.noShow) / totalThisMonth) * 100)
        : 0;

    // 최근 8주 주별 예약 건수 집계
    const weeklyData = Array.from({ length: 8 }, (_, i) => {
        const weekStartDay = today.subtract(7 - i, 'week').startOf('week');
        const weekEndDay = weekStartDay.endOf('week');
        const count = recentReservations.reservations.filter(r => {
            const d = dayjs(r.reservationDate);
            return d.isAfter(weekStartDay.subtract(1, 'day')) && d.isBefore(weekEndDay.add(1, 'day'));
        }).length;
        return {
            label: `${weekStartDay.month() + 1}/${weekStartDay.date()}`,
            count,
        };
    });

    return (
        <DashboardClient
            businessId={businessId}
            todayTotal={todayTotal}
            todayConfirmed={todayConfirmed}
            todayPending={todayPending}
            pendingReservations={pendingReservations.reservations}
            totalCustomers={totalCustomers}
            newCustomers={newCustomers}
            noShowRate={noShowRate}
            weeklyData={weeklyData}
        />
    );
}