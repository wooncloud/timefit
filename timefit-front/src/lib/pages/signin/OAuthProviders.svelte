<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import OAuthButton from './OAuthButton.svelte';

    const dispatch = createEventDispatcher();

    const providers = [
        {
            provider: 'google',
            text: 'Google로 계속하기',
            iconSrc: '/google.svg'
        },
        {
            provider: 'kakao',
            text: '카카오로 계속하기',
            iconSrc: '/kakao.svg'
        },
        {
            provider: 'apple',
            text: 'Apple로 계속하기',
            iconSrc: '/apple.svg'
        }
    ];

    const handleOAuthLogin = (event: CustomEvent<{ provider: string }>) => {
        dispatch('oauth-login', event.detail);
    };
</script>

<div class="space-y-3">
    {#each providers as provider}
        <OAuthButton
            provider={provider.provider}
            text={provider.text}
            iconSrc={provider.iconSrc}
            on:login={handleOAuthLogin}
        />
    {/each}
</div>
