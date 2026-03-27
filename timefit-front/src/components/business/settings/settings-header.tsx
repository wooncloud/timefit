import { Button } from '@/components/ui/button';
import { CardHeader } from '@/components/ui/card';

interface SettingsHeaderProps {
  onSave?: () => void;
  disabled?: boolean;
}

export function SettingsHeader({ onSave, disabled }: SettingsHeaderProps) {
  return (
    <CardHeader className="flex flex-row items-center justify-end">
      <Button onClick={onSave} disabled={disabled}>저장</Button>
    </CardHeader>
  );
}