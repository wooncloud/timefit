'use client';

import { useState } from 'react';
import { Plus } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';

interface CategoryFormProps {
  onSubmit: (data: { name: string; notice: string; isActive: boolean }) => void;
}

export function CategoryForm({ onSubmit }: CategoryFormProps) {
  const [name, setName] = useState('');
  const [notice, setNotice] = useState('');
  const [isActive, setIsActive] = useState(true);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    onSubmit({
      name: name.trim(),
      notice: notice.trim(),
      isActive,
    });

    setName('');
    setNotice('');
    setIsActive(true);
  };

  return (
    <form onSubmit={handleSubmit} className="mb-6">
      <div className="flex items-start gap-3">
        <div className="flex-1">
          <Input
            placeholder="예: 커트"
            value={name}
            onChange={e => setName(e.target.value)}
            className="h-10"
          />
          <label className="mt-1 block text-xs text-muted-foreground">
            카테고리 이름
          </label>
        </div>
        <div className="flex-[2]">
          <Input
            placeholder="예: 남성 커트, 여성 커트, 앞머리 커트 등"
            value={notice}
            onChange={e => setNotice(e.target.value)}
            className="h-10"
          />
          <label className="mt-1 block text-xs text-muted-foreground">
            카테고리 설명
          </label>
        </div>
        <div className="flex items-center gap-2 pt-2">
          <Switch
            checked={isActive}
            onCheckedChange={setIsActive}
            id="active-new"
          />
          <label htmlFor="active-new" className="text-sm text-muted-foreground">
            활성화
          </label>
        </div>
        <Button type="submit" size="default" className="h-10">
          <Plus className="mr-1 h-4 w-4" />
          추가
        </Button>
      </div>
    </form>
  );
}
