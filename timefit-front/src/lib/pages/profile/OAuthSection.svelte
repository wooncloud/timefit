<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import OAuthProviderCard from './OAuthProviderCard.svelte';

    export let oauthProviders: {
        google: { connected: boolean; email: string | null };
        kakao: { connected: boolean; nickname: string };
        apple: { connected: boolean; email: string };
    };

    const dispatch = createEventDispatcher<{
        connect: string;
        disconnect: string;
    }>();

    const handleConnect = (event: CustomEvent<string>) => {
        dispatch('connect', event.detail);
    };

    const handleDisconnect = (event: CustomEvent<string>) => {
        dispatch('disconnect', event.detail);
    };
</script>

<div class="mb-8">
    <h3 class="text-base-content mb-4 text-lg font-semibold">로그인 연동</h3>

    <div class="space-y-3">
        <OAuthProviderCard
            provider="google"
            connected={oauthProviders.google.connected}
            on:connect={handleConnect}
            on:disconnect={handleDisconnect}
        />

        <OAuthProviderCard
            provider="kakao"
            connected={oauthProviders.kakao.connected}
            on:connect={handleConnect}
            on:disconnect={handleDisconnect}
        />

        <OAuthProviderCard
            provider="apple"
            connected={oauthProviders.apple.connected}
            on:connect={handleConnect}
            on:disconnect={handleDisconnect}
        />
    </div>
</div>
