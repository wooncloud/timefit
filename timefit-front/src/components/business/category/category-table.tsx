'use client';

import { Pencil, Trash2 } from 'lucide-react';

import type { Category } from '@/types/category/category';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

interface CategoryTableProps {
  categories: Category[];
  onToggle: (id: string, currentStatus: boolean) => void;
  onEdit: (category: Category) => void;
  onDelete: (id: string) => void;
}

export function CategoryTable({
  categories,
  onToggle,
  onEdit,
  onDelete,
}: CategoryTableProps) {
  return (
    <div className="rounded-lg border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[250px]">카테고리 이름</TableHead>
            <TableHead className="w-[200px]">상태</TableHead>
            <TableHead>카테고리 설명</TableHead>
            <TableHead className="w-[120px] text-right">작업</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {categories.length === 0 ? (
            <TableRow>
              <TableCell
                colSpan={4}
                className="py-8 text-center text-muted-foreground"
              >
                아직 카테고리가 없습니다. 새로운 카테고리를 추가해보세요.
              </TableCell>
            </TableRow>
          ) : (
            categories.map(category => (
              <TableRow key={category.id}>
                <TableCell className="font-medium">{category.name}</TableCell>
                <TableCell>
                  <Switch
                    checked={category.isActive}
                    onCheckedChange={() =>
                      onToggle(category.id, category.isActive)
                    }
                  />
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {category.notice || '-'}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => onEdit(category)}
                      className="h-8 w-8 text-blue-600 hover:bg-blue-50 hover:text-blue-700"
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => onDelete(category.id)}
                      className="h-8 w-8 text-red-600 hover:bg-red-50 hover:text-red-700"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
}
