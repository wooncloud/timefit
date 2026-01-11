import Link from 'next/link';
import { Calendar, Users, Clock, BarChart3, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/button';

export default function PCHomePage() {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      {/* Hero Section */}
      <section className="relative overflow-hidden px-6 pt-24 pb-20 lg:pt-32 lg:pb-32">
        {/* Decorative Background */}
        <div className="absolute inset-0 -z-10 bg-[radial-gradient(45%_45%_at_50%_50%,rgba(59,130,246,0.05)_0%,transparent_100%)]" />
        <div className="absolute top-0 left-1/2 -z-10 h-[600px] w-[600px] -translate-x-1/2 -translate-y-1/2 bg-primary/5 blur-[120px] rounded-full" />

        <div className="container mx-auto max-w-6xl text-center">
          <div className="inline-flex items-center rounded-full border border-primary/10 bg-primary/5 px-4 py-1.5 text-sm font-medium text-primary mb-8 animate-in fade-in slide-in-from-bottom-4 duration-1000">
            <span className="mr-2">✨</span> 차세대 비즈니스 예약 관리 솔루션
          </div>
          <h1 className="mb-6 text-5xl font-extrabold tracking-tight lg:text-7xl animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-200">
            비즈니스에 <span className="text-primary italic">TimeFit</span>을 더하세요
          </h1>
          <p className="mx-auto mb-10 max-w-2xl text-xl text-muted-foreground animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-400">
            복잡한 예약 관리와 고객 응대를 하나로 통합합니다. <br className="hidden sm:block" />
            당신의 시간을 가장 가치 있는 곳에 사용하세요.
          </p>
          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-600">
            <Button size="lg" className="h-12 px-8 text-base shadow-lg shadow-primary/20" asChild>
              <Link href="/business/signin">
                시작하기 <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="bg-muted/30 py-24 border-y border-border">
        <div className="container mx-auto max-w-6xl px-6">
          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
            <FeatureCard
              icon={<Calendar className="h-6 w-6" />}
              title="스마트 예약"
              description="실시간으로 연동되는 직관적인 예약 시스템을 제공합니다."
            />
            <FeatureCard
              icon={<Users className="h-6 w-6" />}
              title="구성원 관리"
              description="직원별 스케줄과 권한을 효율적으로 조율할 수 있습니다."
            />
            <FeatureCard
              icon={<Clock className="h-6 w-6" />}
              title="운영 자유도"
              description="유연한 영업 시간과 카테고리 설정으로 업종에 맞춤화합니다."
            />
            <FeatureCard
              icon={<BarChart3 className="h-6 w-6" />}
              title="데이터 분석"
              description="비즈니스 성장을 위한 핵심 지표와 통계를 제공합니다."
            />
          </div>
        </div>
      </section>

      {/* Spacing for Footer */}
      <div className="py-12" />
    </div>
  );
}

function FeatureCard({ icon, title, description }: { icon: React.ReactNode, title: string, description: string }) {
  return (
    <div className="group rounded-2xl border border-border bg-card p-8 transition-all hover:shadow-xl hover:-translate-y-1">
      <div className="mb-6 flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-foreground transition-all duration-300">
        {icon}
      </div>
      <h3 className="mb-3 text-xl font-bold">{title}</h3>
      <p className="text-muted-foreground leading-relaxed">{description}</p>
    </div>
  );
}

