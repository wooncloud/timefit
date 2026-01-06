'use client';

import { useEffect, useState } from 'react';

import type { Category } from '@/types/category/category';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';

interface CategoryEditDialogProps {
  category: Category | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (
    id: string,
    categoryName: string,
    categoryNotice: string,
    isActive: boolean
  ) => void;
}

export function CategoryEditDialog({
  category,
  open,
  onOpenChange,
  onSubmit,
}: CategoryEditDialogProps) {
  const [categoryName, setCategoryName] = useState('');
  const [categoryNotice, setCategoryNotice] = useState('');
  const [isActive, setIsActive] = useState(true);

  useEffect(() => {
    if (category) {
      setCategoryName(category.categoryName);
      setCategoryNotice(category.categoryNotice ?? '');
      setIsActive(category.isActive);
    }
  }, [category]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!category || !categoryName.trim()) return;

    onSubmit(
      category.categoryId,
      categoryName.trim(),
      categoryNotice.trim(),
      isActive
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[525px]">
        <DialogHeader>
          <DialogTitle>카테고리 수정</DialogTitle>
          <DialogDescription>카테고리 정보를 수정합니다.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="edit-name">카테고리 이름</Label>
              <Input
                id="edit-name"
                value={categoryName}
                onChange={e => setCategoryName(e.target.value)}
                placeholder="예: 신발"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="edit-notice">카테고리 설명</Label>
              <Input
                id="edit-notice"
                value={categoryNotice}
                onChange={e => setCategoryNotice(e.target.value)}
                placeholder="예: 운동화 및 샌들"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Switch
                id="edit-active"
                checked={isActive}
                onCheckedChange={setIsActive}
              />
              <Label htmlFor="edit-active">활성화</Label>
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              취소
            </Button>
            <Button type="submit">저장</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
