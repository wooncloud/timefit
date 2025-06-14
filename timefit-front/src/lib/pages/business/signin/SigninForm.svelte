<script lang="ts">
	import { goto } from '$app/navigation';
	import EmailPasswordForm from '$lib/components/auth/EmailPasswordForm.svelte';
	import GoogleLoginButton from '$lib/components/auth/GoogleLoginButton.svelte';
	import AuthFooter from '$lib/components/auth/AuthFooter.svelte';

	let email = '';
	let password = '';
	let loading = false;
	let googleLoading = false;

	async function handleBusinessSignin() {
		if (!email || !password) {
			alert('이메일과 비밀번호를 입력해주세요.');
			return;
		}

		loading = true;
		try {
			console.log('Business signing in with:', email, password);
			await new Promise((resolve) => setTimeout(resolve, 1000));
			goto('/business/dashboard');
		} catch (error) {
			console.error('Business login error:', error);
			alert('사업자 로그인에 실패했습니다.');
		} finally {
			loading = false;
		}
	}

	async function handleGoogleSignin() {
		googleLoading = true;
		try {
			console.log('Business signing in with Google');
			await new Promise((resolve) => setTimeout(resolve, 1000));
			goto('/business/dashboard');
		} catch (error) {
			console.error('Google business login error:', error);
			alert('Google 사업자 로그인에 실패했습니다.');
		} finally {
			googleLoading = false;
		}
	}
</script>

<div class="max-w-md w-full">
	<div class="text-center mb-8">
		<h1 class="text-3xl font-bold text-base-content mb-2">사업자 로그인</h1>
		<p class="text-base-content/70">비즈니스 계정을 생성하여 팀 관리를 시작하세요</p>
	</div>

	<EmailPasswordForm
		bind:email
		bind:password
		bind:loading
		emailLabel="사업자 이메일"
		submitButtonText="사업자 로그인"
		on:submit={handleBusinessSignin}
	/>

	<div class="divider my-6">또는</div>

	<GoogleLoginButton loading={googleLoading} on:click={handleGoogleSignin} />

	<AuthFooter pageType="signin" userType="business" />
</div> 