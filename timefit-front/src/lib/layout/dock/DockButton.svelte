<script lang="ts">
    import { goto } from '$app/navigation';

    export let href: string;
    export let iconPath: string;
    export let label: string;
    export let requiresAuth: boolean = false;
    export let isLoggedIn: boolean = false;

    function handleClick(event: Event) {
        if (requiresAuth && !isLoggedIn) {
            event.preventDefault();
            goto('/m/signin');
        }
    }
</script>

<a {href} class="" on:click={handleClick}>
    <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-5 w-5"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        ><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d={iconPath} /></svg
    >
    <span class="btm-nav-label">{label}</span>
</a>
