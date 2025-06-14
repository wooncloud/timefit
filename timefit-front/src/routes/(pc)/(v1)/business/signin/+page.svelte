<script lang="ts">
	import SigninHero from '$lib/pages/business/signin/SigninHero.svelte';
	import SigninForm from '$lib/pages/business/signin/SigninForm.svelte';
</script>

<svelte:head>
	<title>사업자 로그인 - TimeFit</title>
	<meta name="description" content="TimeFit 사업자 서비스에 로그인하여 팀과 조직의 시간 관리를 최적화하세요." />
</svelte:head>

<div class="min-h-screen flex">
	<SigninHero />
	<div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
		<SigninForm />
	</div>
</div> 