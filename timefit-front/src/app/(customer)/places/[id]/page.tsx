interface PlaceDetailPageProps {
  params: Promise<{ id: string }>;
}

export default async function PlaceDetailPage({ params }: PlaceDetailPageProps) {
  const { id } = await params;

  return (
    <div>
      <h1>장소 상세</h1>
      <p>장소 ID: {id}</p>
      <p>식당/호텔/피트니스 상세 정보 및 메뉴/서비스 리스트</p>
    </div>
  );
}
