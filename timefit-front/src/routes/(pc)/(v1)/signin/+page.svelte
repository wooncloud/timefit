<script lang="ts">
    import { supabase } from '$lib/supabase/supabaseClient';
    import OAuthProviders from '$lib/pages/signin/OAuthProviders.svelte';
    import TermsAgreement from '$lib/pages/signin/TermsAgreement.svelte';

    const handleOAuthLogin = async (event: CustomEvent<{ provider: string }>) => {
        const { provider } = event.detail;

        try {
            if (provider === 'google') {
                const { data, error } = await supabase.auth.signInWithOAuth({
                    provider: 'google',
                    options: {
                        redirectTo: `${window.location.origin}/`
                    }
                });

                if (error) {
                    console.error('Google OAuth error:', error);
                    alert('Google 로그인 중 오류가 발생했습니다.');
                }
            } else if (provider === 'kakao') {
                const { data, error } = await supabase.auth.signInWithOAuth({
                    provider: 'kakao',
                    options: {
                        redirectTo: `${window.location.origin}/`
                    }
                });

                if (error) {
                    console.error('Kakao OAuth error:', error);
                    alert('카카오 로그인 중 오류가 발생했습니다.');
                }
            } else if (provider === 'apple') {
                console.log('Apple OAuth not implemented yet');
                alert('Apple 로그인은 준비 중입니다.');
            } else {
                console.log(`${provider} OAuth not implemented yet`);
                alert(`${provider} 로그인은 준비 중입니다.`);
            }
        } catch (error) {
            console.error('OAuth error:', error);
            alert('로그인 중 오류가 발생했습니다.');
        }
    };
</script>

<svelte:head>
    <title>로그인 - TimeFit</title>
    <meta name="description" content="TimeFit에 로그인하여 스마트한 시간 관리를 시작하세요." />
</svelte:head>

<div class="min-h-screen flex">
    <!-- Left Hero Section -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary to-secondary p-12 items-center justify-center">
        <div class="max-w-md text-white">
            <div class="mb-8">
                <div class="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center mb-6">
                    <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                </div>
                <h2 class="text-3xl font-bold mb-4">효율적인 시간 관리의 시작</h2>
                <p class="text-xl opacity-90 mb-8">TimeFit으로 더 스마트하게 시간을 관리하고 생산성을 높여보세요.</p>
            </div>
            
            <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6">
                <div class="flex items-start space-x-4">
                    <div class="w-12 h-12 rounded-full bg-white/20 flex-shrink-0 overflow-hidden">
                        <div class="w-full h-full bg-gradient-to-br from-white/40 to-white/10"></div>
                    </div>
                    <div>
                        <p class="text-lg leading-relaxed mb-3">"TimeFit 덕분에 업무 효율성이 300% 향상되었습니다. 정말 혁신적인 서비스예요!"</p>
                        <p class="font-semibold">김민수</p>
                        <p class="text-sm opacity-75">스타트업 CEO</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Form Section -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
        <div class="max-w-md w-full">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold text-base-content mb-2">로그인</h1>
                <p class="text-base-content/70">계정에 로그인하여 TimeFit을 시작하세요</p>
            </div>

            <OAuthProviders on:oauth-login={handleOAuthLogin} />

            <TermsAgreement />
        </div>
    </div>
</div> 