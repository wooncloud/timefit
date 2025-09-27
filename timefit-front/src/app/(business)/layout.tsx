import type { Metadata } from 'next';
import { AppSidebar } from '@/components/business/sidebar/app-sidebar';
import { Separator } from '@/components/ui/separator';
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from '@/components/ui/sidebar';
import { getCurrentUserFromSession } from '@/lib/session/server';

export const metadata: Metadata = {
  title: '%s | TimeFit',
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
        user={sessionUser && {
          name: sessionUser.name,
          email: sessionUser.email,
          avatar: sessionUser.profileImageUrl ?? null,
        }}
      />
      <SidebarInset>
        <header className="group-has-data-[collapsible=icon]/sidebar-wrapper:h-12 flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator
              orientation="vertical"
              className="mr-2 data-[orientation=vertical]:h-4"
            />
          </div>
        </header>
        {children}
      </SidebarInset>
    </SidebarProvider>
  );
}
