'use client';

import { useState, useEffect } from 'react';
import { businessService } from '@/services/business/businessService';
import type { MyBusinessItem } from '@/types/business/myBusiness';

interface BusinessSettingsFormData {
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  representativeName: string;
  address: string;
  contactPhone: string;
  email: string;
  description: string;
}

export function useBusinessSettings() {
  const [business, setBusiness] = useState<MyBusinessItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState<BusinessSettingsFormData>({
    businessName: '',
    businessTypes: [],
    businessNumber: '',
    representativeName: '',
    address: '',
    contactPhone: '',
    email: '',
    description: '',
  });

  useEffect(() => {
    const fetchMyBusiness = async () => {
      try {
        const result = await businessService.getMyBusiness();
        if (result.success && result.data && result.data[0]) {
          const businessData = result.data[0];
          setBusiness(businessData);
          setFormData({
            businessName: businessData.businessName || '',
            businessTypes: businessData.businessTypes || [],
            businessNumber: '',
            representativeName: '',
            address: businessData.address || '',
            contactPhone: '',
            email: '',
            description: '',
          });
        }
      } catch (error) {
        console.error('사업자 목록 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMyBusiness();
  }, []);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleBusinessTypeChange = (value: string) => {
    setFormData((prev) => ({
      ...prev,
      businessTypes: [value],
    }));
  };

  const handleAddressChange = (address: string) => {
    setFormData((prev) => ({
      ...prev,
      address,
    }));
  };

  return {
    business,
    loading,
    formData,
    handleInputChange,
    handleBusinessTypeChange,
    handleAddressChange,
  };
}
