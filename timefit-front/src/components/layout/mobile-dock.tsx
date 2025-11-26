'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Calendar, Home, MessageCircle, Search, User } from 'lucide-react';

import { cn } from '@/lib/utils';

const dockItems = [
  {
    href: '/mobile',
    icon: Home,
    label: '홈',
  },
  {
    href: '/mobile/search',
    icon: Search,
    label: '검색',
  },
  {
    href: '/mobile/reservations',
    icon: Calendar,
    label: '예약',
  },
  {
    href: '/mobile/chat',
    icon: MessageCircle,
    label: '채팅',
  },
  {
    href: '/mobile/mypage',
    icon: User,
    label: '내정보',
  },
];

export function MobileDock() {
  const pathname = usePathname();

  return (
    <div className="fixed bottom-0 left-0 right-0 border-t bg-background">
      <div className="flex items-center justify-around py-2">
        {dockItems.map(item => {
          const Icon = item.icon;
          const isActive =
            pathname === item.href || pathname.startsWith(item.href + '/');

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex min-w-0 flex-col items-center justify-center px-2 py-1 text-xs',
                isActive
                  ? 'text-primary'
                  : 'text-muted-foreground hover:text-foreground'
              )}
            >
              <Icon size={20} className="mb-1" />
              <span className="truncate">{item.label}</span>
            </Link>
          );
        })}
      </div>
    </div>
  );
}
