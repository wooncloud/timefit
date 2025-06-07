<script lang="ts">
	import { onMount } from 'svelte';
	import { navLeft, navCenter } from '$lib/stores/navbar';

	let userProfile = {
		name: '김시간',
		email: 'timefit.user@gmail.com',
		phone: '+82-10-1234-5678',
		profileImage: null,
		oauthProviders: {
			google: { connected: false, email: null },
			kakao: { connected: true, nickname: '김시간' },
			apple: { connected: true, email: 'timefit.user@icloud.com' }
		}
	};

	const handleProfileEdit = () => {
		console.log('Profile edit clicked');
	};

	const handleOAuthConnect = (provider: string) => {
		console.log(`Connect ${provider}`);
	};

	const handleOAuthDisconnect = (provider: string) => {
		console.log(`Disconnect ${provider}`);
	};

	onMount(() => {
		navLeft.set('Profile');
		navCenter.set('');
	});
</script>

<div class=" bg-base-100 p-4">
	<div class="max-w-md mx-auto">
		<div class="mb-8">
			<div class="card bg-base-100 shadow-lg border border-base-200">
				<div class="card-body p-6">
					<div class="flex items-start justify-between mb-6">
						<div class="flex-1">
							<h2 class="text-xl font-semibold text-base-content mb-1">{userProfile.name}</h2>
							<p class="text-sm text-base-content/70 mb-1">{userProfile.email}</p>
							<p class="text-sm text-base-content/70">{userProfile.phone}</p>
						</div>
						
						<div class="avatar">
							<div class="w-16 h-16 rounded-full bg-base-200 flex items-center justify-center">
								{#if userProfile.profileImage}
									<img src={userProfile.profileImage} alt="Profile" class="w-full h-full rounded-full object-cover" />
								{:else}
									<svg class="w-8 h-8 text-base-content/40" fill="currentColor" viewBox="0 0 24 24">
										<path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.89 1 3 1.89 3 3V21A2 2 0 0 0 5 23H19A2 2 0 0 0 21 21V9M19 9H14V4H5V19L12 12L19 19V9Z"/>
									</svg>
								{/if}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="mb-8">
			<h3 class="text-lg font-semibold text-base-content mb-4">OAuth login</h3>
			
			<div class="space-y-3">
				<div class="card bg-base-100 shadow-sm border border-base-200">
					<div class="card-body p-4">
						<div class="flex items-center justify-between">
							<div class="flex items-center gap-3">
								<div class="w-10 h-10 rounded-full bg-white flex items-center justify-center shadow-sm">
									<svg class="w-6 h-6" viewBox="0 0 24 24">
										<path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
										<path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
										<path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
										<path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
									</svg>
								</div>
								<span class="font-medium text-base-content">Google</span>
							</div>
							
							{#if userProfile.oauthProviders.google.connected}
								<button 
									class="btn btn-sm btn-success btn-outline"
									on:click={() => handleOAuthDisconnect('google')}
								>
									Connected
								</button>
							{:else}
								<button 
									class="btn btn-sm btn-outline"
									on:click={() => handleOAuthConnect('google')}
								>
									Connect
								</button>
							{/if}
						</div>
					</div>
				</div>

				<div class="card bg-base-100 shadow-sm border border-base-200">
					<div class="card-body p-4">
						<div class="flex items-center justify-between">
							<div class="flex items-center gap-3">
								<div class="w-10 h-10 rounded-full bg-yellow-400 flex items-center justify-center">
									<span class="text-sm font-bold text-black">kakao</span>
								</div>
								<span class="font-medium text-base-content">Kakao</span>
							</div>
							
							{#if userProfile.oauthProviders.kakao.connected}
								<button 
									class="btn btn-sm btn-success btn-outline"
									on:click={() => handleOAuthDisconnect('kakao')}
								>
									Connected
								</button>
							{:else}
								<button 
									class="btn btn-sm btn-outline"
									on:click={() => handleOAuthConnect('kakao')}
								>
									Connect
								</button>
							{/if}
						</div>
					</div>
				</div>

				<div class="card bg-base-100 shadow-sm border border-base-200">
					<div class="card-body p-4">
						<div class="flex items-center justify-between">
							<div class="flex items-center gap-3">
								<div class="w-10 h-10 rounded-full bg-black flex items-center justify-center">
									<svg class="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
										<path d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.81-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M13 3.5c.73-.83 1.94-1.46 2.94-1.5.13 1.17-.34 2.35-1.04 3.19-.69.85-1.83 1.51-2.95 1.42-.15-1.15.41-2.35 1.05-3.11z"/>
									</svg>
								</div>
								<span class="font-medium text-base-content">Apple</span>
							</div>
							
							{#if userProfile.oauthProviders.apple.connected}
								<button 
									class="btn btn-sm btn-success btn-outline"
									on:click={() => handleOAuthDisconnect('apple')}
								>
									Connected
								</button>
							{:else}
								<button 
									class="btn btn-sm btn-outline"
									on:click={() => handleOAuthConnect('apple')}
								>
									Connect
								</button>
							{/if}
						</div>
					</div>
				</div>
			</div>
		</div>

		<button 
			class="btn btn-primary w-full"
			on:click={handleProfileEdit}
		>
			Profile Edit
		</button>
	</div>
</div> 