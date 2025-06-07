<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { navLeft, navCenter } from '$lib/stores/navbar';

	let formData = {
		name: '김시간',
		phone: '+82-10-1234-5678',
		profileImage: null as string | null
	};

	let nameInput = '';
	let phoneInput = '';

	const handleBack = () => {
		goto('/m/mypage');
	};

	const handleSave = () => {
		console.log('Save profile:', { name: nameInput, phone: phoneInput });
		goto('/m/mypage');
	};

	const handleProfileImageClick = () => {
		console.log('Profile image click - open image picker');
	};

	onMount(() => {
		navLeft.set('');
		navCenter.set('프로필 변경');
		
		nameInput = formData.name;
		phoneInput = formData.phone;
	});
</script>

<div class="bg-base-100">
	<div class="mx-auto max-w-md px-4 py-6">
		<div class="flex flex-col items-center my-8">
			<button class="avatar mb-8" on:click={handleProfileImageClick}>
				<div class="w-32 h-32 rounded-full bg-base-200 hover:bg-base-300 transition-colors flex items-center justify-center">
					{#if formData.profileImage}
						<img
							src={formData.profileImage}
							alt="Profile"
							class="w-full h-full rounded-full object-cover"
						/>
					{:else}
						<svg class="w-12 h-12 text-base-content/40" fill="currentColor" viewBox="0 0 24 24">
							<path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.89 1 3 1.89 3 3V21A2 2 0 0 0 5 23H19A2 2 0 0 0 21 21V9M19 9H14V4H5V19L12 12L19 19V9Z"/>
						</svg>
					{/if}
				</div>
			</button>
		</div>

		<div class="space-y-6">
			<div>
				<label for="name-input" class="block text-lg font-semibold text-base-content mb-3 mx-1">이름</label>
				<input
					id="name-input"
					type="text"
					bind:value={nameInput}
					placeholder="이름을 입력하세요"
					class="input input-bordered w-full bg-base-100 text-base h-14"
				/>
			</div>

			<div>
				<label for="phone-input" class="block text-lg font-semibold text-base-content mb-3 mx-1">연락처</label>
				<div class="relative">
					<input
						id="phone-input"
						type="tel"
						bind:value={phoneInput}
						placeholder="연락처를 입력하세요"
						class="input input-bordered w-full bg-base-100 text-base h-14 pr-12"
					/>
				</div>
			</div>
		</div>

		<div class="fixed bottom-24 left-0 right-0 px-4">
			<div class="max-w-md mx-auto">
				<button
					class="btn btn-primary w-full h-14 text-lg"
					on:click={handleSave}
				>
					저장
				</button>
			</div>
		</div>
	</div>
</div> 