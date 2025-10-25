import { CardHeader, CardTitle } from '@/components/ui/card';

interface TeamHeaderProps {
  title: string;
}

export function TeamHeader({ title }: TeamHeaderProps) {
  return (
    <CardHeader>
      <CardTitle>{title}</CardTitle>
    </CardHeader>
  );
}

