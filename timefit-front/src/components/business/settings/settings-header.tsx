import { Button } from '@/components/ui/button';
import { CardHeader } from '@/components/ui/card';

interface SettingsHeaderProps {
  onSave?: () => void;
}

export function SettingsHeader({ onSave }: SettingsHeaderProps) {
  return (
    <CardHeader className="flex flex-row items-center justify-end">
      <Button onClick={onSave}>저장</Button>
    </CardHeader>
  );
}

