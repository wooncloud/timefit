'use client';

import { useState } from 'react';
import { Plus } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

interface CategoryFormProps {
  onSubmit: (categoryName: string, categoryNotice: string) => void;
}

export function CategoryForm({ onSubmit }: CategoryFormProps) {
  const [categoryName, setCategoryName] = useState('');
  const [categoryNotice, setCategoryNotice] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!categoryName.trim()) return;

    onSubmit(categoryName.trim(), categoryNotice.trim());

    setCategoryName('');
    setCategoryNotice('');
  };

  return (
    <form onSubmit={handleSubmit} className="mb-6">
      <div className="flex items-start gap-3">
        <div className="flex-1">
          <Input
            placeholder="예: 커트"
            value={categoryName}
            onChange={e => setCategoryName(e.target.value)}
            className="h-10"
          />
          <label className="mt-1 block text-xs text-muted-foreground">
            카테고리 이름
          </label>
        </div>
        <div className="flex-[2]">
          <Input
            placeholder="예: 남성 커트, 여성 커트, 앞머리 커트 등"
            value={categoryNotice}
            onChange={e => setCategoryNotice(e.target.value)}
            className="h-10"
          />
          <label className="mt-1 block text-xs text-muted-foreground">
            카테고리 설명
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
