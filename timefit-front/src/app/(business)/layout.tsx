import type { Metadata } from 'next';
import { AppSidebar } from '@/components/business/sidebar/app-sidebar';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import { BusinessHeader } from '@/components/business/business-header';
import { getCurrentUserFromSession } from '@/lib/session/server';

export const metadata: Metadata = {
  title: 'TimeFit - 예약 관리 시스템',
  description: '비즈니스를 위한 스마트한 예약 관리 솔루션',
};

export default async function BusinessLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const sessionUser = await getCurrentUserFromSession();

  return (
    <SidebarProvider>
      <AppSidebar
        user={
          sessionUser && {
            name: sessionUser.name,
            email: sessionUser.email,
            avatar: sessionUser.profileImageUrl ?? null,
          }
        }
      />
      <SidebarInset>
        <BusinessHeader />
        <div className="p-4">{children}</div>
      </SidebarInset>
    </SidebarProvider>
  );
}
