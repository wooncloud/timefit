import type {
    CreateUpdateMenuRequest,
    CreateMenuHandlerResponse,
    UpdateMenuHandlerResponse,
    DeleteMenuHandlerResponse,
} from '@/types/menu/menu';

/**
 * 클라이언트 측 클래스: 메뉴 변경 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class MenuService {
    /**
     * 메뉴 생성 (API 라우트를 통한 클라이언트 측 호출)
     */
    async createMenu(
        businessId: string,
        data: CreateUpdateMenuRequest
    ): Promise<CreateMenuHandlerResponse> {
        const response = await fetch(`/api/business/${businessId}/menu`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        return response.json();
    }

    /**
     * 메뉴 수정 (API 라우트를 통한 클라이언트 측 호출)
     */
    async updateMenu(
        businessId: string,
        menuId: string,
        data: CreateUpdateMenuRequest
    ): Promise<UpdateMenuHandlerResponse> {
        const response = await fetch(
            `/api/business/${businessId}/menu/${menuId}`,
            {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            }
        );

        return response.json();
    }

    /**
     * 메뉴 삭제 (API 라우트를 통한 클라이언트 측 호출)
     */
    async deleteMenu(
        businessId: string,
        menuId: string
    ): Promise<DeleteMenuHandlerResponse> {
        const response = await fetch(
            `/api/business/${businessId}/menu/${menuId}`,
            {
                method: 'DELETE',
            }
        );

        return response.json();
    }

    /**
     * 메뉴 활성화 상태 전환 (API 라우트를 통한 클라이언트 측 호출)
     */
    async toggleMenu(
        businessId: string,
        menuId: string
    ): Promise<UpdateMenuHandlerResponse> {
        const response = await fetch(
            `/api/business/${businessId}/menu/${menuId}/toggle`,
            {
                method: 'PATCH',
            }
        );

        return response.json();
    }
}

export const menuService = new MenuService();
