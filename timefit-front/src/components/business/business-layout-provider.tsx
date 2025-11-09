'use client';

import { useBusinessData } from '@/hooks/business/useBusinessData';

export function BusinessLayoutProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  useBusinessData();

  return <>{children}</>;
}
