<script lang="ts">
    import { navLeft, navCenter, navVisible } from '$lib/stores/navbar';

    const handleBackClick = () => {
        history.back();
    };

    $: showBackButton = $navLeft.includes('/');
</script>

{#if $navVisible}
    <div class="navbar bg-base-100 px-4 shadow-sm">
        <div class="navbar-start">
            {#if showBackButton}
                <button class="btn btn-ghost btn-sm p-2" on:click={handleBackClick} aria-label="back">
                    <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                    </svg>
                </button>
            {:else}
                <span class="text-xl font-bold">{$navLeft}</span>
            {/if}
        </div>
        <div class="navbar-center">
            <span class="text-xl font-bold">{$navCenter}</span>
        </div>
        <div class="navbar-end">
            <!-- Right 영역 - 필요시 컨텐츠 추가 -->
        </div>
    </div>
{/if}
