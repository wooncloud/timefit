<script lang="ts">
    import '../../../style.css';
    import Navbar from '$lib/layout/navbar/Navbar.svelte';
    import Dock from '$lib/layout/dock/Dock.svelte';
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { page } from '$app/stores';
    import { supabase } from '$lib/supabase/supabaseClient';

    let isLoading = true;
    let isLoggedIn = false;

    // 로그인 없이도 접근 가능한 페이지들
    const publicPages = ['/m', '/m/search', '/m/signin'];

    onMount(async () => {
        try {
            const { data: { session } } = await supabase.auth.getSession();
            
            if (session && session.user) {
                isLoggedIn = true;
                console.log('User is logged in:', session.user.email);
            } else {
                console.log('User is not logged in');
                // 로그인이 필요한 페이지에만 리다이렉트
                if (!publicPages.includes($page.url.pathname)) {
                    goto('/m/signin', { replaceState: true });
                }
            }
        } catch (error) {
            console.error('Error checking auth status:', error);
            if (!publicPages.includes($page.url.pathname)) {
                goto('/m/signin', { replaceState: true });
            }
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
{:else}
    <div class="drawer drawer-end">
        <input id="my-drawer" type="checkbox" class="drawer-toggle" />
        <div class="drawer-content flex h-screen flex-col">
            <Navbar />

            <main class="flex-grow overflow-y-auto">
                <slot></slot>
            </main>

            <Dock {isLoggedIn} />
        </div>

        <!-- 안써서 주석 -->
        <!-- <div class="drawer-side">
            <label for="my-drawer" aria-label="close sidebar" class="drawer-overlay"></label>
            <ul class="menu p-4 w-80 min-h-full bg-base-200 text-base-content">
            </ul>
        </div> -->
    </div>
{/if}
