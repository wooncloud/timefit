<script lang="ts">
    import { navLeft, navCenter } from '$lib/stores/navbar';

    export let brandTitle: string = 'Timefit';
</script>

<div class="navbar bg-base-100 px-4 shadow-sm">
    <div class="navbar-start">
        <span class="text-xl font-bold">{$navLeft}</span>
    </div>
    <div class="navbar-center">
        <span class="text-xl font-bold">{$navCenter}</span>
    </div>
    <div class="navbar-end">
        <!-- Right 영역 - 필요시 컨텐츠 추가 -->
    </div>
</div>
