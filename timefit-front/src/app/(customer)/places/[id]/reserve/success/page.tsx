interface ReserveSuccessPageProps {
  params: Promise<{ id: string }>;
}

export default async function ReserveSuccessPage({ params }: ReserveSuccessPageProps) {
  const { id } = await params;

  return (
    <div>
      <h1>예약 완료</h1>
      <p>장소 ID: {id}</p>
      <p>확인 메시지 및 예약 번호</p>
    </div>
  );
}
