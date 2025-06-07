<script lang="ts">
    import { onMount } from 'svelte';
    import { navLeft, navCenter } from '$lib/stores/navbar';
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
        console.log('Profile edit clicked');
    };

    const handleOAuthConnect = (event: CustomEvent<string>) => {
        console.log(`Connect ${event.detail}`);
    };

    const handleOAuthDisconnect = (event: CustomEvent<string>) => {
        console.log(`Disconnect ${event.detail}`);
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

        <button class="btn btn-primary w-full" on:click={handleProfileEdit}> Profile Edit </button>
    </div>
</div>
