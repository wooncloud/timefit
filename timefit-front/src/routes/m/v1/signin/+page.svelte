<script lang="ts">
	import { onMount } from 'svelte';
	import { supabase } from '$lib/supabase/supabaseClient';
	import OAuthProviders from '$lib/auth/signin/OAuthProviders.svelte';
	import EmailSigninButton from '$lib/auth/signin/EmailSigninButton.svelte';
	import TermsAgreement from '$lib/auth/signin/TermsAgreement.svelte';

	onMount(() => {
		console.log('Mobile OAuth signin page loaded');
	});

	const handleOAuthLogin = async (event: CustomEvent<{ provider: string }>) => {
		const { provider } = event.detail;
		
		try {
			if (provider === 'google') {
				const { data, error } = await supabase.auth.signInWithOAuth({
					provider: 'google',
					options: {
						redirectTo: `${window.location.origin}/m/v1`
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
						redirectTo: `${window.location.origin}/m/v1`
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

<div class="flex items-center justify-center min-h-full px-4">
	<div class="card bg-base-100 w-full max-w-sm">
		<div class="card-body">
			<OAuthProviders on:oauth-login={handleOAuthLogin} />

			<div class="divider my-6">또는</div>

			<EmailSigninButton />

			<TermsAgreement />
		</div>
	</div>
</div>