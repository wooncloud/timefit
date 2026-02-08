import { cn } from '@/lib/utils';

interface LogoProps {
  className?: string;
  size?: number;
}

/**
 * TimeFit 로고 컴포넌트
 * 외부에서 className을 통해 색상(text-color) 및 크기를 조절할 수 있습니다.
 */
export function Logo({ className, size = 24 }: LogoProps) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      className={cn('fill-current', className)}
    >
      <path d="M7 1v2H3a1 1 0 0 0-1 1v16a1 1 0 0 0 1 1h7.755A8 8 0 0 1 22 9.755V4a1 1 0 0 0-1-1h-4V1h-2v2H9V1zm16 15a6 6 0 1 1-12 0a6 6 0 0 1 12 0m-7-4v4.414l2.293 2.293l1.414-1.414L18 15.586V12z" />
    </svg>
  );
}
