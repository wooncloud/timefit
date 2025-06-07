<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	export let provider: 'google' | 'kakao' | 'apple';
	export let connected: boolean = false;

	const dispatch = createEventDispatcher<{
		connect: string;
		disconnect: string;
	}>();

	const handleClick = () => {
		if (connected) {
			dispatch('disconnect', provider);
		} else {
			dispatch('connect', provider);
		}
	};

	const getProviderConfig = (provider: string) => {
		switch (provider) {
			case 'google':
				return {
					name: '구글',
					iconComponent: 'google'
				};
			case 'kakao':
				return {
					name: '카카오',
					iconComponent: 'kakao'
				};
			case 'apple':
				return {
					name: '애플',
					iconComponent: 'apple'
				};
			default:
				return { name: '', iconComponent: '' };
		}
	};

	$: config = getProviderConfig(provider);
</script>

<div class="card bg-base-100 border-base-200 border shadow-sm">
	<div class="card-body p-4">
		<div class="flex items-center justify-between">
			<div class="flex items-center gap-3">
				{#if config.iconComponent === 'google'}
					<div class="flex h-10 w-10 items-center justify-center rounded-full bg-white shadow-sm">
						<svg class="h-6 w-6" viewBox="0 0 24 24">
							<path
								fill="#4285F4"
								d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
							/>
							<path
								fill="#34A853"
								d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
							/>
							<path
								fill="#FBBC05"
								d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
							/>
							<path
								fill="#EA4335"
								d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
							/>
						</svg>
					</div>
				{:else if config.iconComponent === 'kakao'}
					<div class="flex h-10 w-10 items-center justify-center rounded-full bg-yellow-400">
						<span class="text-sm font-bold text-black">kakao</span>
					</div>
				{:else if config.iconComponent === 'apple'}
					<div class="flex h-10 w-10 items-center justify-center rounded-full bg-black">
						<svg class="h-6 w-6 text-white" fill="currentColor" viewBox="0 0 24 24">
							<path
								d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.81-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M13 3.5c.73-.83 1.94-1.46 2.94-1.5.13 1.17-.34 2.35-1.04 3.19-.69.85-1.83 1.51-2.95 1.42-.15-1.15.41-2.35 1.05-3.11z"
							/>
						</svg>
					</div>
				{/if}
				<span class="text-base-content font-medium">{config.name}</span>
			</div>

			{#if connected}
				<button class="btn btn-sm btn-success btn-outline" on:click={handleClick}>
					Connected
				</button>
			{:else}
				<button class="btn btn-sm btn-outline" on:click={handleClick}> Connect </button>
			{/if}
		</div>
	</div>
</div> 