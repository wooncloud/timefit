<script lang="ts">
    import DockButton from './DockButton.svelte';

    export let isLoggedIn: boolean;

    const dockItems = [
        {
            href: '/m',
            iconPath:
                'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
            label: '홈',
            requiresAuth: false
        },
        {
            href: '/m/search',
            iconPath: 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z',
            label: '검색',
            requiresAuth: false
        },
        {
            href: '/m/reservations',
            iconPath:
                'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
            label: '내 예약',
            requiresAuth: true
        },
        {
            href: '/m/chat',
            iconPath:
                'M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z',
            label: '채팅',
            requiresAuth: true
        },
        {
            href: '/m/mypage',
            iconPath:
                'M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z',
            label: '내 정보',
            requiresAuth: true
        }
    ];
</script>

<div class="dock bg-base-300">
    {#each dockItems as item}
        <DockButton
            href={item.href}
            iconPath={item.iconPath}
            label={item.label}
            requiresAuth={item.requiresAuth}
            {isLoggedIn}
        />
    {/each}
</div>
