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
  className?: string;
}

export function BusinessTypeSelect({
  value,
  onValueChange,
  className,
}: BusinessTypeSelectProps) {
  return (
    <Select value={value} onValueChange={onValueChange}>
      <SelectTrigger className={className}>
        <SelectValue placeholder="업종 선택" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="BD000">음식점업</SelectItem>
        <SelectItem value="BD001">숙박업</SelectItem>
        <SelectItem value="BD002">소매/유통업</SelectItem>
        <SelectItem value="BD003">미용/뷰티업</SelectItem>
        <SelectItem value="BD004">의료업</SelectItem>
        <SelectItem value="BD005">피트니스/스포츠업</SelectItem>
        <SelectItem value="BD006">교육/문화업</SelectItem>
        <SelectItem value="BD007">전문서비스업</SelectItem>
        <SelectItem value="BD008">생활서비스업</SelectItem>
        <SelectItem value="BD009">제조/생산업</SelectItem>
      </SelectContent>
    </Select>
  );
}
