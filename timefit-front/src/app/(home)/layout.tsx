import type { Metadata } from 'next';

import { Footer } from '@/components/layout/footer';
import { Navbar } from '@/components/layout/navbar';

export const metadata: Metadata = {
    title: 'TimeFit - 예약 관리 시스템',
    description: '비즈니스를 위한 스마트한 예약 관리 솔루션',
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <>
            <main>{children}</main>
            <Footer />
        </>
    );
}
