import { NextRequest, NextResponse } from 'next/server';

import { getServerSession } from '@/lib/session/server';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ businessId: string }> }
) {
  const session = await getServerSession();
  const { businessId } = await params;

  if (!session.user?.accessToken) {
    return NextResponse.json({ message: 'Unauthorized' }, { status: 401 });
  }

  try {
    const body = await request.json();

    const response = await fetch(
      `${BACKEND_URL}/api/business/${businessId}/operating-hours`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${session.user.accessToken}`,
        },
        body: JSON.stringify(body),
      }
    );

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(data, { status: response.status });
    }

    return NextResponse.json(data);
  } catch (error) {
    console.error('Failed to update operating hours:', error);
    return NextResponse.json(
      { message: 'Internal Server Error' },
      { status: 500 }
    );
  }
}
