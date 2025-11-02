export interface Customer {
    id: string;
    name: string;
    phone: string;
    email?: string;
    firstVisitDate: string;
    lastVisitDate: string;
    totalVisits: number;
    memo?: string;
    createdAt: string;
  }