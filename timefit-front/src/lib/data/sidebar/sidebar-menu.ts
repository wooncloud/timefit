import { House, Settings2, Store, User } from 'lucide-react';

export const navItems = [
  {
    title: '메인',
    icon: House,
    isActive: true,
    items: [
      { title: '대시보드', url: '/business' },
      { title: '캘린더', url: '/business/calendar' },
    ],
  },
  {
    title: '업체 관리',
    icon: Store,
    isActive: true,
    items: [
      { title: '영업 일정', url: '/business/schedule' },
      { title: '카테고리', url: '/business/category' },
      { title: '서비스/상품', url: '/business/product' },
      { title: '예약 현황', url: '/business/reservations' },
    ],
  },
  {
    title: '고객 관리',
    icon: User,
    isActive: true,
    items: [
      { title: '고객 목록', url: '/business/customers' },
      // { title: '고객 채팅', url: '/business/customers/chat' },
      // { title: '공지', url: '/business/customers/notice' },
    ],
  },
  {
    title: '설정',
    icon: Settings2,
    isActive: true,
    items: [
      { title: '업체 정보', url: '/business/settings' },
      { title: '팀 관리', url: '/business/settings/team' },
    ],
  },
];
