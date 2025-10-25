import { Settings2, Store, User, House } from 'lucide-react';

export const navItems = [
  {
    title: '메인',
    url: '#',
    icon: House,
    isActive: true,
    items: [
      // { title: "대시보드", url: "#" },
      { title: '캘린더', url: '#' },
    ],
  },
  {
    title: '업체 관리',
    url: '#',
    icon: Store,
    isActive: true,
    items: [
      { title: '영업 일정', url: '#' },
      { title: '서비스/상품', url: '#' },
      { title: '예약 현황', url: '#' },
    ],
  },
  {
    title: '고객 관리',
    url: '#',
    icon: User,
    isActive: true,
    items: [
      { title: '고객 목록', url: '#' },
      { title: '고객 채팅', url: '#' },
      { title: '공지', url: '#' },
    ],
  },
  {
    title: '설정',
    url: '#',
    icon: Settings2,
    isActive: true,
    items: [
      { title: '업체 정보', url: '#' },
      { title: '팀 관리', url: '#' },
    ],
  },
];
