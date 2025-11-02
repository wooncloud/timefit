import { Card, CardContent } from '@/components/ui/card';
import { LucideIcon } from 'lucide-react';
import { cn } from '@/lib/utils';

interface ReservationStatCardProps {
  icon: LucideIcon;
  count: number;
  label: string;
  colorClass: {
    bg: string;
    icon: string;
  };
}

export function ReservationStatCard({
  icon: Icon,
  count,
  label,
  colorClass,
}: ReservationStatCardProps) {
  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex flex-col items-center justify-center space-y-2">
          <div
            className={cn(
              'flex h-12 w-12 items-center justify-center rounded-full',
              colorClass.bg
            )}
          >
            <Icon className={cn('h-6 w-6', colorClass.icon)} />
          </div>
          <div className="text-3xl font-bold">{count}</div>
          <p className="text-sm text-muted-foreground">{label}</p>
        </div>
      </CardContent>
    </Card>
  );
}
