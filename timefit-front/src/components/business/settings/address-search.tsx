import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Search } from 'lucide-react';

interface AddressSearchProps {
  value?: string;
  onChange?: (value: string) => void;
  onSearch?: () => void;
  placeholder?: string;
}

export function AddressSearch({
  value,
  onChange,
  onSearch,
  placeholder = '서울시 강남구 테헤란로 123',
}: AddressSearchProps) {
  return (
    <div className="relative">
      <Input
        value={value}
        onChange={(e) => onChange?.(e.target.value)}
        placeholder={placeholder}
      />
      <Button
        variant="ghost"
        size="sm"
        className="absolute right-1 top-1/2 -translate-y-1/2"
        onClick={onSearch}
        type="button"
      >
        <Search className="h-4 w-4 mr-1" />
        주소 검색
      </Button>
    </div>
  );
}

