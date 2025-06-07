<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { navLeft, navCenter } from '$lib/stores/navbar';
    import { supabase } from '$lib/supabase/supabaseClient';
    import ProfileCard from '$lib/pages/profile/ProfileCard.svelte';
    import OAuthSection from '$lib/pages/profile/OAuthSection.svelte';

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
        goto('/m/mypage/edit');
    };

    const handleOAuthConnect = (event: CustomEvent<string>) => {
        console.log(`Connect ${event.detail}`);
    };

    const handleOAuthDisconnect = (event: CustomEvent<string>) => {
        console.log(`Disconnect ${event.detail}`);
    };

    const handleLogout = async () => {
        try {
            await supabase.auth.signOut();
            goto('/m', { replaceState: true });
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    onMount(() => {
        navLeft.set('Profile');
        navCenter.set('');
    });
</script>

<div class="bg-base-100 p-4">
    <div class="mx-auto max-w-md">
        <div class="mb-8">
            <ProfileCard
                name={userProfile.name}
                email={userProfile.email}
                phone={userProfile.phone}
                profileImage={userProfile.profileImage}
            />
        </div>

        <OAuthSection
            oauthProviders={userProfile.oauthProviders}
            on:connect={handleOAuthConnect}
            on:disconnect={handleOAuthDisconnect}
        />

        <div class="space-y-3">
            <button class="btn btn-primary w-full" on:click={handleProfileEdit}> 
                프로필 변경
            </button>
            
            <button class="btn btn-outline btn-error w-full" on:click={handleLogout}>
                로그아웃
            </button>
        </div>
    </div>
</div>
