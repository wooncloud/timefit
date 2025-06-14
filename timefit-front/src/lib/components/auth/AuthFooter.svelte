<script lang="ts">
	export let pageType: 'signin' | 'signup';
	export let userType: 'business' | 'personal';

	const isBusiness = userType === 'business';
	const isSignin = pageType === 'signin';

	const peerUserType = isBusiness ? 'personal' : 'business';
	const peerUserTypeText = isBusiness ? '개인' : '사업자';
	const userTypeText = isBusiness ? '사업자' : '개인';
</script>

<div class="text-center mt-6">
	<p class="text-base-content/70 text-sm">
		{#if isSignin}
			{userTypeText} 계정이 없으신가요?
			<a href={`/${userType}/signup`} class="link link-primary font-medium">{userTypeText} 회원가입</a>
		{:else}
			이미 {userTypeText} 계정이 있으신가요?
			<a href={`/${userType}/signin`} class="link link-primary font-medium">{userTypeText} 로그인</a>
		{/if}
	</p>
</div>

{#if isSignin}
	<div class="text-center mt-4">
		<p class="text-xs text-base-content/50">
			로그인을 계속하면
			<a href="/terms" class="link">이용약관</a> 및
			<a href="/privacy" class="link">개인정보처리방침</a>에 동의하는 것으로 간주됩니다.
		</p>
	</div>
{/if}

<div class="text-center mt-6">
	<div class="divider">{peerUserTypeText} 계정으로 {isSignin ? '로그인' : '가입'}</div>
	<a href={`/${peerUserType}/${pageType}`} class="link link-secondary text-sm">
		{peerUserTypeText} 사용자 {isSignin ? '로그인' : '회원가입'} >
	</a>
</div> 