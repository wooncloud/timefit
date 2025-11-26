interface CustomerCountDisplayProps {
  count: number;
}

export function CustomerCountDisplay({ count }: CustomerCountDisplayProps) {
  return (
    <div className="text-sm text-muted-foreground">총 {count}명의 고객</div>
  );
}
