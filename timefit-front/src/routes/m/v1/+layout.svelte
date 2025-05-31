<script lang="ts">
    import '../../../style.css';
    import Navbar from '$lib/layout/navbar/Navbar.svelte';
    import Dock from '$lib/layout/dock/Dock.svelte';
</script>

<div class="drawer drawer-end">
    <input id="my-drawer" type="checkbox" class="drawer-toggle" />
    <div class="drawer-content flex h-screen flex-col">
        <Navbar />

        <main class="flex-grow overflow-y-auto">
            <slot></slot>
        </main>

        <Dock />
    </div>

    <!-- 안써서 주석 -->
    <!-- <div class="drawer-side">
		<label for="my-drawer" aria-label="close sidebar" class="drawer-overlay"></label>
		<ul class="menu p-4 w-80 min-h-full bg-base-200 text-base-content">
		</ul>
	</div> -->
</div>
