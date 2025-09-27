import Link from "next/link";
import { Button } from "@/components/ui/button"

export function Footer() {
  return (
    <footer className="bg-muted px-6 py-10 text-muted-foreground lg:px-10">
      <div className="mx-auto max-w-7xl">
        <div className="flex flex-col items-center space-y-4 md:space-y-6">
          <div className="text-center">
            <div>
              <Link href={'#'}><Button variant="link">입점 문의</Button></Link>
              <Link href={'#'}><Button variant="link">채용</Button></Link>
              <Link href={'#'}><Button variant="link">광고 문의</Button></Link>
              <Link href={'#'}><Button variant="link">공지사항</Button></Link>
              <Link href={'#'}><Button variant="link">Q&A</Button></Link>
            </div>
            <div>
              <Link href={'/policy/service'}>
                <Button variant="link">서비스 이용약관</Button>
              </Link>
              <Link href={'/policy/privacy'}>
                <Button variant="link">개인정보 처리방침</Button>
              </Link>
            </div>
            <br />
            <div>
              <p className="text-xs sm:text-sm">회사명: </p>
              <p className="text-xs sm:text-sm">대표자: </p>
              <p className="text-xs sm:text-sm">주소: </p>
              <p className="text-xs sm:text-sm">이메일: </p>
              <p className="text-xs sm:text-sm">대표전화: </p>
              <p className="text-xs sm:text-sm">사업자등록: </p>
              <p className="text-xs sm:text-sm">통신판매업 신고번호: </p>
            </div>
            <br />
            <div>
              <p className="text-xs sm:text-sm">
                Copyright © 2025 - All rights reserved by Timefit
              </p>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
