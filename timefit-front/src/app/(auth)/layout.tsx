import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'TimeFit | Timefit에 오신 것을 환영합니다.',
  description: '비즈니스를 위한 스마트한 예약 관리 솔루션',
};

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
