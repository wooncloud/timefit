<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { navLeft, navCenter, navVisible } from '$lib/stores/navbar';

    let formData = {
        name: '김시간',
        phone: '+82-10-1234-5678',
        profileImage: null as string | null
    };

    let nameInput = '';
    let phoneInput = '';

    const handleSave = () => {
        console.log('Save profile:', { name: nameInput, phone: phoneInput });
        goto('/m/mypage');
    };

    const handleProfileImageClick = () => {
        console.log('Profile image click - open image picker');
    };

    onMount(() => {
        navLeft.set('/m/mypage');
        navCenter.set('내 정보 변경');
        navVisible.set(true);
        nameInput = formData.name;
        phoneInput = formData.phone;
    });
</script>

<div class="bg-base-100">
    <div class="mx-auto max-w-md px-4 py-6">
        <div class="my-8 flex flex-col items-center">
            <button class="avatar mb-8" on:click={handleProfileImageClick}>
                <div
                    class="bg-base-200 hover:bg-base-300 flex h-32 w-32 items-center justify-center rounded-full transition-colors"
                >
                    {#if formData.profileImage}
                        <img
                            src={formData.profileImage}
                            alt="Profile"
                            class="h-full w-full rounded-full object-cover"
                        />
                    {:else}
                        <img src="/profile.svg" alt="Profile" class="h-full w-full" />
                    {/if}
                </div>
            </button>
        </div>

        <div class="space-y-6">
            <div>
                <label
                    for="name-input"
                    class="text-base-content mx-1 mb-3 block text-lg font-semibold">이름</label
                >
                <input
                    id="name-input"
                    type="text"
                    bind:value={nameInput}
                    placeholder="이름을 입력하세요"
                    class="input input-bordered bg-base-100 h-14 w-full text-base"
                />
            </div>

            <div>
                <label
                    for="phone-input"
                    class="text-base-content mx-1 mb-3 block text-lg font-semibold">연락처</label
                >
                <div class="relative">
                    <input
                        id="phone-input"
                        type="tel"
                        bind:value={phoneInput}
                        placeholder="연락처를 입력하세요"
                        class="input input-bordered bg-base-100 h-14 w-full pr-12 text-base"
                    />
                </div>
            </div>
        </div>

        <div class="fixed right-0 bottom-24 left-0 px-4">
            <div class="mx-auto max-w-md">
                <button class="btn btn-primary h-14 w-full text-lg" on:click={handleSave}>
                    저장
                </button>
            </div>
        </div>
    </div>
</div>
