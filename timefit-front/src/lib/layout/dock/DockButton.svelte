<script lang="ts">
    export let href: string;
    export let iconPath: string;
    export let label: string;
</script>

<a {href} class="">
    <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-5 w-5"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        ><path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d={iconPath}
        /></svg
    >
    <span class="btm-nav-label">{label}</span>
</a> 