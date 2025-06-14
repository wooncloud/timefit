<script lang="ts">
    // PC v1 페이지 스크립트
    const features = [
        {
            title: "스마트 스케줄링",
            description: "라이프스타일과 선호도에 맞춰 적응하는 AI 기반 스케줄링",
            icon: "🤖"
        },
        {
            title: "실시간 동기화",
            description: "모든 기기에서 실시간 업데이트로 동기화 유지",
            icon: "⚡"
        },
        {
            title: "고급 분석",
            description: "생산성 패턴과 최적화 기회에 대한 인사이트 제공",
            icon: "📊"
        }
    ];

    const stats = [
        {
            number: "14M+",
            label: "활성 사용자",
            icon: "👥"
        },
        {
            number: "128.5%",
            label: "성장률",
            icon: "📈"
        },
        {
            number: "2.8M+",
            label: "완료된 작업",
            icon: "✅"
        },
        {
            number: "2,800+",
            label: "5점 만점 리뷰",
            icon: "⭐"
        }
    ];

    const categories = [
        { name: "개인 계획", icon: "📝", count: "255,555명" },
        { name: "팀 협업", icon: "👥", count: "386,504명" },
        { name: "피트니스 트래킹", icon: "💪", count: "690,199명" },
        { name: "건강 모니터링", icon: "🏥", count: "486,061명" },
        { name: "학습 및 공부", icon: "📚", count: "325,142명" },
        { name: "비즈니스 분석", icon: "💼", count: "198,776명" },
        { name: "프로젝트 관리", icon: "📊", count: "442,889명" },
        { name: "시간 추적", icon: "⏰", count: "567,334명" }
    ];
</script>

<svelte:head>
    <title>타임핏 - 스마트한 예약 관리 플랫폼</title>
    <meta name="description" content="타임핏과 함께 여정을 강화하세요. 성능의 힘을 손바닥 안에 담은 차세대 앱입니다" />
</svelte:head>

<!-- Hero Section -->
<div class="hero min-h-screen bg-gradient-to-br from-primary/10 via-secondary/5 to-accent/10">
    <div class="hero-content text-center">
        <div class="max-w-4xl">
            <div class="mockup-phone mb-8">
                <div class="camera"></div>
                <div class="display">
                    <div class="artboard artboard-demo phone-1 bg-gradient-to-br from-primary to-secondary p-4">
                        <div class="text-white text-xs">
                            <div class="flex justify-between items-center mb-2">
                                <span>타임핏</span>
                                <span>100%</span>
                            </div>
                            <div class="bg-white/20 backdrop-blur rounded-lg p-3 mb-2">
                                <div class="text-left">
                                    <p class="font-semibold">오늘의 진행상황</p>
                                    <p class="text-xs opacity-80">8개 작업 완료</p>
                                </div>
                            </div>
                            <div class="grid grid-cols-2 gap-2">
                                <div class="bg-white/20 backdrop-blur rounded p-2">
                                    <p class="text-xs">집중 시간</p>
                                    <p class="font-bold">4시간 32분</p>
                                </div>
                                <div class="bg-white/20 backdrop-blur rounded p-2">
                                    <p class="text-xs">효율성</p>
                                    <p class="font-bold">94%</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <h1 class="text-5xl md:text-6xl font-bold text-primary mb-4">
                타임핏 - <span class="text-secondary">당신을 발전시킵니다</span>
            </h1>
            <h2 class="text-3xl md:text-4xl font-bold mb-6">
                효율성, 편의성, 혁신을 향해
            </h2>
            <p class="text-lg md:text-xl mb-8 max-w-2xl mx-auto text-base-content/80">
                타임핏과 함께 여정을 강화하세요. 성능의 힘을 손바닥 안에 담은 차세대 앱입니다
            </p>
            <div class="flex flex-col sm:flex-row gap-4 justify-center">
                <div class="btn btn-outline btn-lg">
                    <img src="/imgs/logo/apple.svg" alt="App Store" class="w-6 h-6 mr-2" />
                    App Store
                </div>
                <div class="btn btn-outline btn-lg">
                    <img src="/imgs/logo/google.svg" alt="Google Play" class="w-6 h-6 mr-2" />
                    Google Play
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Stats Section -->
<div class="py-16 bg-base-200">
    <div class="container mx-auto px-4">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8">
            {#each stats as stat}
                <div class="text-center">
                    <div class="text-4xl mb-2">{stat.icon}</div>
                    <div class="text-3xl md:text-4xl font-bold text-primary mb-2">{stat.number}</div>
                    <div class="text-base-content/60">{stat.label}</div>
                </div>
            {/each}
        </div>
    </div>
</div>

<!-- Features Section -->
<div class="py-16">
    <div class="container mx-auto px-4">
        <div class="text-center mb-12">
            <h2 class="text-4xl font-bold mb-4">왜 타임핏을 선택해야 할까요?</h2>
            <p class="text-lg text-base-content/70 max-w-2xl mx-auto">
                당신의 AI 동반자는 자연어 이해, 개인화된 경험, 예측 분석을 제공하며, 음성 명령과 스마트 자동화를 원활하게 통합하여 향상된 사용자 상호작용을 제공합니다.
            </p>
        </div>
        
        <div class="grid md:grid-cols-3 gap-8">
            {#each features as feature}
                <div class="card bg-base-100 shadow-lg hover:shadow-xl transition-shadow">
                    <div class="card-body text-center">
                        <div class="text-4xl mb-4">{feature.icon}</div>
                        <h3 class="card-title justify-center text-xl mb-3">{feature.title}</h3>
                        <p class="text-base-content/70">{feature.description}</p>
                    </div>
                </div>
            {/each}
        </div>
    </div>
</div>

<!-- Call to Action -->
<div class="py-16 bg-gradient-to-r from-primary to-secondary">
    <div class="container mx-auto px-4 text-center">
        <h2 class="text-4xl font-bold text-primary-content mb-4">더 나은 생활을 위한 변화</h2>
        <p class="text-xl text-primary-content/80 mb-8 max-w-2xl mx-auto">
            안전하게 고수 만나는 법 7가지
        </p>
        <div class="mockup-browser bg-base-300 max-w-md mx-auto">
            <div class="mockup-browser-toolbar">
                <div class="input">https://timefit.app</div>
            </div>
            <div class="flex justify-center px-4 py-16 bg-base-200">
                <div class="text-center">
                    <div class="text-6xl mb-4">🚀</div>
                    <p class="font-semibold">지금 시작하기</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Services Section -->
<div class="py-16 bg-base-100">
    <div class="container mx-auto px-4">
        <div class="text-center mb-12">
            <h2 class="text-4xl font-bold mb-4">인기 서비스</h2>
            <div class="form-control max-w-md mx-auto">
                <div class="input-group">
                    <input type="text" placeholder="어떤 서비스가 필요하세요?" class="input input-bordered flex-1" />
                    <button class="btn btn-primary">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                        </svg>
                    </button>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-6 mb-12">
            {#each categories as category}
                <div class="text-center group cursor-pointer">
                    <div class="w-16 h-16 mx-auto mb-3 bg-primary/10 rounded-2xl flex items-center justify-center text-2xl group-hover:bg-primary/20 transition-colors">
                        {category.icon}
                    </div>
                    <p class="text-sm font-medium mb-1">{category.name}</p>
                    <p class="text-xs text-base-content/60">{category.count}</p>
                </div>
            {/each}
        </div>

        <div class="grid md:grid-cols-4 gap-6">
            <div class="card bg-base-100 shadow-lg image-full">
                <figure><div class="bg-gradient-to-br from-purple-500 to-pink-500 h-full w-full"></div></figure>
                <div class="card-body">
                    <h3 class="card-title text-white">피아노/키보드 레슨</h3>
                    <p class="text-white/80">255,555명 요청</p>
                </div>
            </div>
            <div class="card bg-base-100 shadow-lg image-full">
                <figure><div class="bg-gradient-to-br from-blue-500 to-cyan-500 h-full w-full"></div></figure>
                <div class="card-body">
                    <h3 class="card-title text-white">골프 레슨</h3>
                    <p class="text-white/80">386,504명 요청</p>
                </div>
            </div>
            <div class="card bg-base-100 shadow-lg image-full">
                <figure><div class="bg-gradient-to-br from-orange-500 to-red-500 h-full w-full"></div></figure>
                <div class="card-body">
                    <h3 class="card-title text-white">피트니스 트레이닝(PT)</h3>
                    <p class="text-white/80">690,199명 요청</p>
                </div>
            </div>
            <div class="card bg-base-100 shadow-lg image-full">
                <figure><div class="bg-gradient-to-br from-green-500 to-teal-500 h-full w-full"></div></figure>
                <div class="card-body">
                    <h3 class="card-title text-white">보컬 레슨</h3>
                    <p class="text-white/80">486,061명 요청</p>
                </div>
            </div>
        </div>
    </div>
</div>
