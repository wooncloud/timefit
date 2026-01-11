import { House, Settings2, Store, User } from 'lucide-react';

export const navItems = [
  {
    title: '메인',
    icon: House,
    isActive: true,
    items: [
      { title: '대시보드', url: '/b' },
      { title: '캘린더', url: '/b/calendar' },
    ],
  },
  {
    title: '업체 관리',
    icon: Store,
    isActive: true,
    items: [
      { title: '영업 일정', url: '/b/schedule' },
      { title: '카테고리', url: '/b/category' },
      { title: '서비스/상품', url: '/b/product' },
      { title: '예약 현황', url: '/b/reservations' },
    ],
  },
  {
    title: '고객 관리',
    icon: User,
    isActive: true,
    items: [
      { title: '고객 목록', url: '/b/customers' },
      // { title: '고객 채팅', url: '/b/customers/chat' },
      // { title: '공지', url: '/b/customers/notice' },
    ],
  },
  {
    title: '설정',
    icon: Settings2,
    isActive: true,
    items: [
      { title: '업체 정보', url: '/b/settings' },
      { title: '팀 관리', url: '/b/settings/team' },
    ],
  },
];
