import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'TimeFit - 로그인',
  description: 'TimeFit 로그인 페이지',
};

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
