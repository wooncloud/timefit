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
    let fileInput: HTMLInputElement;

    const handleSave = () => {
        console.log('Save profile:', {
            name: nameInput,
            phone: phoneInput,
            profileImage: formData.profileImage
        });
        goto('/m/mypage');
    };

    const handleProfileImageClick = () => {
        fileInput?.click();
    };

    const handleFileChange = (event: Event) => {
        const target = event.target as HTMLInputElement;
        const file = target.files?.[0];

        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => {
                formData.profileImage = e.target?.result as string;
            };
            reader.readAsDataURL(file);
        }
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
        <!-- 숨겨진 파일 input -->
        <input
            bind:this={fileInput}
            type="file"
            accept="image/*"
            on:change={handleFileChange}
            class="hidden"
            aria-label="프로필 이미지 선택"
        />

        <div class="my-8 flex flex-col items-center">
            <button class="avatar mb-8" on:click={handleProfileImageClick}>
                <div
                    class="bg-base-200 hover:bg-base-300 border-base-300 hover:border-primary flex h-32 w-32 items-center justify-center rounded-full border-2 border-dashed transition-colors"
                >
                    {#if formData.profileImage}
                        <img
                            src={formData.profileImage}
                            alt="Profile"
                            class="h-full w-full rounded-full object-cover"
                        />
                    {:else}
                        <div class="text-base-content/60 flex flex-col items-center">
                            <img src="/profile.svg" alt="Profile" class="mb-2 h-16 w-16" />
                            <span class="text-center text-xs">이미지 선택</span>
                        </div>
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
