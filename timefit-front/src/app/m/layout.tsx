import { MobileNavbar } from '@/components/layout/mobile-navbar';
import { MobileDock } from '@/components/layout/mobile-dock';

export default function MobileLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen bg-background">
      <MobileNavbar />
      <main className="pb-16">{children}</main>
      <MobileDock />
    </div>
  );
}
