import type {
  CreateBookingSlotsHandlerResponse,
  CreateBookingSlotsRequest,
  DeleteBookingSlotHandlerResponse,
  DeletePastSlotsHandlerResponse,
  GetBookingSlotsHandlerResponse,
  ToggleBookingSlotHandlerResponse,
} from '@/types/booking-slot/booking-slot';

class BookingSlotService {
  async createSlots(
    businessId: string,
    data: CreateBookingSlotsRequest
  ): Promise<CreateBookingSlotsHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }
    );
    return response.json();
  }

  async getSlotsByDate(
    businessId: string,
    date: string
  ): Promise<GetBookingSlotsHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot?date=${date}`
    );
    return response.json();
  }

  async getSlotsByDateRange(
    businessId: string,
    startDate: string,
    endDate: string
  ): Promise<GetBookingSlotsHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot/range?startDate=${startDate}&endDate=${endDate}`
    );
    return response.json();
  }

  async deleteSlot(
    businessId: string,
    slotId: string
  ): Promise<DeleteBookingSlotHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot/${slotId}`,
      { method: 'DELETE' }
    );
    return response.json();
  }

  async activateSlot(
    businessId: string,
    slotId: string
  ): Promise<ToggleBookingSlotHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot/${slotId}/activate`,
      { method: 'PATCH' }
    );
    return response.json();
  }

  async deactivateSlot(
    businessId: string,
    slotId: string
  ): Promise<ToggleBookingSlotHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot/${slotId}/deactivate`,
      { method: 'PATCH' }
    );
    return response.json();
  }

  async deletePastSlots(
    businessId: string
  ): Promise<DeletePastSlotsHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/booking-slot/past`,
      { method: 'DELETE' }
    );
    return response.json();
  }
}

export const bookingSlotService = new BookingSlotService();