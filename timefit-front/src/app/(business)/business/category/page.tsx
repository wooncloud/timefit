'use client';

import { useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';

import type { Category } from '@/types/category/category';
import { useCategoryList } from '@/hooks/category/use-category-list';
import { CategoryEditDialog } from '@/components/business/category/category-edit-dialog';
import { CategoryForm } from '@/components/business/category/category-form';
import { CategoryTable } from '@/components/business/category/category-table';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

export default function Page() {
  const {
    categories,
    loading,
    createCategory,
    updateCategory,
    deleteCategory,
  } = useCategoryList();

  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [categoryToDelete, setCategoryToDelete] = useState<string | null>(null);

  const handleCreate = async (
    categoryName: string,
    categoryNotice: string
  ) => {
    const result = await createCategory(categoryName, categoryNotice);
    if (result) {
      toast.success('카테고리가 생성되었습니다.');
    } else {
      toast.error('카테고리 생성에 실패했습니다.');
    }
  };

  const handleEdit = (category: Category) => {
    setEditingCategory(category);
    setEditDialogOpen(true);
  };

  const handleUpdate = async (
    id: string,
    categoryName: string,
    categoryNotice: string,
    isActive: boolean
  ) => {
    const success = await updateCategory(id, categoryName, categoryNotice, isActive);
    if (success) {
      toast.success('카테고리가 수정되었습니다.');
      setEditDialogOpen(false);
      setEditingCategory(null);
    } else {
      toast.error('카테고리 수정에 실패했습니다.');
    }
  };

  const handleDeleteClick = (id: string) => {
    setCategoryToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!categoryToDelete) return;

    const success = await deleteCategory(categoryToDelete);
    if (success) {
      toast.success('카테고리가 삭제되었습니다.');
    } else {
      toast.error('카테고리 삭제에 실패했습니다.');
    }
    setDeleteDialogOpen(false);
    setCategoryToDelete(null);
  };

  const handleToggle = async (id: string, currentStatus: boolean) => {
    const category = categories.find(c => c.categoryId === id);
    if (!category) return;

    const success = await updateCategory(
      id,
      category.categoryName,
      category.categoryNotice,
      !currentStatus
    );

    if (success) {
      toast.success(
        `카테고리가 ${!currentStatus ? '활성화' : '비활성화'}되었습니다.`
      );
    } else {
      toast.error('카테고리 상태 변경에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <div className="flex h-[calc(100vh-6rem)] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="container mx-auto px-6 py-6">
      <CategoryForm onSubmit={handleCreate} />
      <CategoryTable
        categories={categories}
        onToggle={handleToggle}
        onEdit={handleEdit}
        onDelete={handleDeleteClick}
      />

      <CategoryEditDialog
        category={editingCategory}
        open={editDialogOpen}
        onOpenChange={open => {
          setEditDialogOpen(open);
          if (!open) setEditingCategory(null);
        }}
        onSubmit={handleUpdate}
      />

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>정말 삭제하시겠습니까?</AlertDialogTitle>
            <AlertDialogDescription>
              이 작업은 취소할 수 없습니다. 카테고리가 영구적으로 삭제됩니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>취소</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteConfirm}>
              삭제
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
