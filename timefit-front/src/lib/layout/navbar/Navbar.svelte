<script lang="ts">
    import BrandLogo from './BrandLogo.svelte';
    
    export let brandHref: string = "/m/v1";
    export let brandTitle: string = "Timefit";
</script>

<div class="navbar bg-base-100 shadow-sm">
    <div class="navbar-start">
        <BrandLogo href={brandHref} title={brandTitle} />
    </div>
    <div class="navbar-center"></div>
    <div class="navbar-end">
        <!-- MenuButton을 사용하지 않으므로 제거됨 -->
    </div>
</div> 