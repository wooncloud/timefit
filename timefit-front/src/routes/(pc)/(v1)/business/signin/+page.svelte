<script lang="ts">
    import { goto } from '$app/navigation';
    
    let email = '';
    let password = '';
    let businessNumber = '';
    let loading = false;

    async function handleBusinessSignin() {
        if (!email || !password) {
            alert('이메일과 비밀번호를 입력해주세요.');
            return;
        }
        
        loading = true;
        try {
            console.log('Business signing in with:', email, password, businessNumber);
            await new Promise(resolve => setTimeout(resolve, 1000));
            goto('/business/dashboard');
        } catch (error) {
            console.error('Business login error:', error);
            alert('사업자 로그인에 실패했습니다.');
        } finally {
            loading = false;
        }
    }

    async function handleGoogleSignin() {
        loading = true;
        try {
            console.log('Business signing in with Google');
            await new Promise(resolve => setTimeout(resolve, 1000));
            goto('/business/dashboard');
        } catch (error) {
            console.error('Google business login error:', error);
            alert('Google 사업자 로그인에 실패했습니다.');
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>사업자 로그인 - TimeFit</title>
    <meta name="description" content="TimeFit 사업자 서비스에 로그인하여 팀과 조직의 시간 관리를 최적화하세요." />
</svelte:head>

<div class="min-h-screen flex">
    <!-- Left Hero Section -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-success to-info p-12 items-center justify-center">
        <div class="max-w-md text-white">
            <div class="mb-8">
                <div class="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center mb-6">
                    <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        <path fill-rule="evenodd" d="M4 4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2H4zm0 2v2h12V6H4zm0 4v4h12v-4H4z" clip-rule="evenodd" />
                    </svg>
                </div>
                <h2 class="text-3xl font-bold mb-4">비즈니스 성장의 파트너</h2>
                <p class="text-xl opacity-90 mb-8">팀과 조직을 위한 통합 시간 관리 솔루션으로 비즈니스 효율성을 극대화하세요.</p>
            </div>
            
            <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6">
                <div class="space-y-4">
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3z"/>
                                <path d="M6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z"/>
                            </svg>
                        </div>
                        <span class="text-lg">팀 협업 관리</span>
                    </div>
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M3 3a1 1 0 000 2v8a2 2 0 002 2h2.586l-1.293 1.293a1 1 0 101.414 1.414L10 15.414l2.293 2.293a1 1 0 001.414-1.414L12.414 15H15a2 2 0 002-2V5a1 1 0 100-2H3zm11.707 4.707a1 1 0 00-1.414-1.414L10 9.586 8.707 8.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                        </div>
                        <span class="text-lg">프로젝트 추적</span>
                    </div>
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd" />
                            </svg>
                        </div>
                        <span class="text-lg">고급 분석 리포트</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Form Section -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
        <div class="max-w-md w-full">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold text-base-content mb-2">사업자 로그인</h1>
                <p class="text-base-content/70">비즈니스 계정으로 로그인하여 팀 관리를 시작하세요</p>
            </div>

            <form on:submit|preventDefault={handleBusinessSignin} class="space-y-4">
                <div class="form-control">
                    <label class="label" for="email">
                        <span class="label-text font-medium">사업자 이메일</span>
                    </label>
                    <input 
                        id="email"
                        type="email" 
                        bind:value={email}
                        placeholder="business@company.com" 
                        class="input input-bordered w-full focus:input-primary" 
                        required
                    />
                </div>

                <div class="form-control">
                    <label class="label" for="password">
                        <span class="label-text font-medium">비밀번호</span>
                    </label>
                    <input 
                        id="password"
                        type="password" 
                        bind:value={password}
                        placeholder="비밀번호를 입력하세요" 
                        class="input input-bordered w-full focus:input-primary" 
                        required
                    />
                </div>

                <div class="flex items-center justify-between">
                    <label class="label cursor-pointer">
                        <input type="checkbox" class="checkbox checkbox-primary checkbox-sm" />
                        <span class="label-text ml-2">로그인 상태 유지</span>
                    </label>
                    <a href="/business/forgot-password" class="link link-primary text-sm">비밀번호 찾기</a>
                </div>

                <button 
                    type="submit" 
                    class="btn btn-primary w-full"
                    class:loading
                    disabled={loading}
                >
                    {loading ? '' : '사업자 로그인'}
                </button>
            </form>

            <div class="divider my-6">또는</div>

            <button 
                type="button"
                on:click={handleGoogleSignin}
                class="btn btn-outline w-full"
                class:loading
                disabled={loading}
            >
                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                    <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                    <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                    <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
                Google로 계속하기
            </button>

            <div class="text-center mt-6">
                <p class="text-base-content/70 text-sm">
                    사업자 계정이 없으신가요? 
                    <a href="/business/signup" class="link link-primary font-medium">사업자 회원가입</a>
                </p>
            </div>

            <div class="text-center mt-4">
                <p class="text-xs text-base-content/50">
                    로그인을 계속하면 
                    <a href="/terms" class="link">이용약관</a> 및 
                    <a href="/privacy" class="link">개인정보처리방침</a>에 동의하는 것으로 간주됩니다.
                </p>
            </div>

            <div class="text-center mt-6">
                <div class="divider">개인 계정으로 로그인</div>
                <a href="/signin" class="link link-secondary text-sm">개인 사용자 로그인 ></a>
            </div>
        </div>
    </div>
</div> 