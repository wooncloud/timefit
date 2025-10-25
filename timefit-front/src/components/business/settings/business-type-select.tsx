import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface BusinessTypeSelectProps {
  value?: string;
  onValueChange?: (value: string) => void;
}

export function BusinessTypeSelect({
  value,
  onValueChange,
}: BusinessTypeSelectProps) {
  return (
    <Select value={value} onValueChange={onValueChange}>
      <SelectTrigger>
        <SelectValue placeholder="카페" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="cafe">카페</SelectItem>
        <SelectItem value="restaurant">식당</SelectItem>
        <SelectItem value="salon">미용실</SelectItem>
        <SelectItem value="fitness">헬스장</SelectItem>
      </SelectContent>
    </Select>
  );
}
