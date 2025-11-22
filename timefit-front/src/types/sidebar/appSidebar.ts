import type { ComponentProps } from 'react';
import type { Sidebar } from '@/components/ui/sidebar';

/**
 * 앱 사이드바 Props
 */
export interface AppSidebarProps extends ComponentProps<typeof Sidebar> {
  user?: {
    name: string;
    email: string;
    avatar?: string | null;
  } | null;
  teams?: {
    name: string;
    plan: string;
  }[];
}
