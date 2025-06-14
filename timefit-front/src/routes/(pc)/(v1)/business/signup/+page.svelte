<script lang="ts">
	import SignupHero from '$lib/pages/business/signup/SignupHero.svelte';
	import SignupForm from '$lib/pages/business/signup/SignupForm.svelte';
</script>

<svelte:head>
	<title>사업자 회원가입 - TimeFit</title>
	<meta name="description" content="TimeFit 사업자 서비스에 가입하여 팀과 조직의 시간 관리를 최적화하세요." />
</svelte:head>

<div class="min-h-screen flex">
	<SignupHero />

	<!-- Right Form Section -->
	<div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
		<div class="max-w-md w-full">
			<SignupForm />
		</div>
	</div>
</div> 