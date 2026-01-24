interface ReservePageProps {
  params: Promise<{ id: string }>;
}

export default async function ReservePage({ params }: ReservePageProps) {
  const { id } = await params;

  return (
    <div>
      <h1>예약 설정</h1>
      <p>장소 ID: {id}</p>
      <p>날짜, 시간, 인원 등 선택</p>
    </div>
  );
}
