'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
    Category,
    CategoryListResponse,
} from '@/types/category/category';
import { useCreateCategory } from '@/hooks/category/mutations/use-create-category';
import { useUpdateCategory } from '@/hooks/category/mutations/use-update-category';
import { useDeleteCategory } from '@/hooks/category/mutations/use-delete-category';
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

interface CategoryClientProps {
    initialCategories: CategoryListResponse;
    businessId: string;
    businessType: string;
}

export function CategoryClient({
    initialCategories,
    businessId,
    businessType,
}: CategoryClientProps) {
    const { createCategory } = useCreateCategory(businessId);
    const { updateCategory } = useUpdateCategory(businessId);
    const { deleteCategory } = useDeleteCategory(businessId);

    const [editingCategory, setEditingCategory] = useState<Category | null>(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [categoryToDelete, setCategoryToDelete] = useState<string | null>(null);

    // 서버에서 받은 초기 데이터 사용
    const categories = initialCategories.categories;

    const refreshPage = () => {
        window.location.reload();
    };

    const handleCreate = async (categoryName: string, categoryNotice: string) => {
        const result = await createCategory({
            businessType,
            categoryName,
            categoryNotice,
        });

        if (result) {
            refreshPage();
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
        const success = await updateCategory(id, {
            categoryName,
            categoryNotice,
            isActive,
        });

        if (success) {
            setEditDialogOpen(false);
            setEditingCategory(null);
            refreshPage();
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
            refreshPage();
        }
        setDeleteDialogOpen(false);
        setCategoryToDelete(null);
    };

    const handleToggle = async (id: string, currentStatus: boolean) => {
        const category = categories.find(c => c.categoryId === id);
        if (!category) return;

        const success = await updateCategory(id, {
            categoryName: category.categoryName,
            categoryNotice: category.categoryNotice ?? '',
            isActive: !currentStatus,
        });

        if (success) {
            toast.success(
                `카테고리가 ${!currentStatus ? '활성화' : '비활성화'}되었습니다.`
            );
            refreshPage();
        }
    };

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
