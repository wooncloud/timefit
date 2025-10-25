import { Button } from '@/components/ui/button';
import { CardHeader, CardTitle } from '@/components/ui/card';

interface SettingsHeaderProps {
  title: string;
  onSave?: () => void;
}

export function SettingsHeader({ title, onSave }: SettingsHeaderProps) {
  return (
    <CardHeader className="flex flex-row items-center justify-between">
      <CardTitle>{title}</CardTitle>
      <Button onClick={onSave}>저장</Button>
    </CardHeader>
  );
}

