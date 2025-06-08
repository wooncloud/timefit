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
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-full bg-white shadow-lg"
                    >
                        <img src="/google.svg" alt="Google" class="h-6 w-6" />
                    </div>
                {:else if config.iconComponent === 'kakao'}
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-full bg-white shadow-lg"
                    >
                        <img src="/kakao.svg" alt="Kakao" class="h-6 w-6" />
                    </div>
                {:else if config.iconComponent === 'apple'}
                    <div class="flex h-10 w-10 items-center justify-center rounded-full bg-white shadow-lg">
                        <img src="/apple.svg" alt="Apple" class="h-6 w-6" />
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
