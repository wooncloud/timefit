import type { Reservation } from '@/components/business/reservations/reservation-table-row';

export const mockReservations: Reservation[] = [
  {
    id: '1',
    reservationNumber: '#R20250920001',
    dateTime: '2025-09-20T10:30:00',
    customerName: '김철수',
    customerPhone: '010-1234-5678',
    service: '이발+가르마',
    status: 'confirmed',
  },
  {
    id: '2',
    reservationNumber: '#R20250920002',
    dateTime: '2025-09-20T14:00:00',
    customerName: '이영희',
    customerPhone: '010-9876-5432',
    service: '커팅만해',
    status: 'pending',
  },
  {
    id: '3',
    reservationNumber: '#R20250919005',
    dateTime: '2025-09-19T11:00:00',
    customerName: '박민수',
    customerPhone: '010-5555-7777',
    service: '에스프레쏘',
    status: 'completed',
  },
  {
    id: '4',
    reservationNumber: '#R20250918003',
    dateTime: '2025-09-18T15:00:00',
    customerName: '최지원',
    customerPhone: '010-1111-2222',
    service: '바닐라라떼',
    status: 'noshow',
  },
  {
    id: '5',
    reservationNumber: '#R20250917001',
    dateTime: '2025-09-17T16:00:00',
    customerName: '정수연',
    customerPhone: '010-3333-4444',
    service: '모카라떼',
    status: 'cancelled',
  },
];
