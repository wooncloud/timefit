<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { supabase } from '$lib/supabase/supabaseClient';

	let isLoading = true;
	let isLoggedIn = false;

	onMount(async () => {
		try {
			const { data: { session } } = await supabase.auth.getSession();
			
			if (session && session.user) {
				isLoggedIn = true;
				console.log('User is logged in:', session.user.email);
			} else {
				console.log('User is not logged in, redirecting to signin');
				goto('/m/v1/signin', { replaceState: true });
			}
		} catch (error) {
			console.error('Error checking auth status:', error);
			goto('/m/v1/signin', { replaceState: true });
		} finally {
			isLoading = false;
		}
	});
</script>

{#if isLoading}
	<div class="hero min-h-screen">
		<div class="hero-content text-center">
			<div class="max-w-md">
				<span class="loading loading-spinner loading-lg"></span>
				<p class="py-6">로그인 상태 확인 중...</p>
			</div>
		</div>
	</div>
{:else if isLoggedIn}
	<div class="hero min-h-screen">
		<div class="hero-content text-center">
			<div class="max-w-md">
				<h1 class="text-5xl font-bold">환영합니다!</h1>
				<p class="py-6">TimeFit에 로그인되었습니다.</p>
				<button class="btn btn-primary" on:click={() => supabase.auth.signOut()}>
					로그아웃
				</button>
			</div>
		</div>
	</div>
{/if}
