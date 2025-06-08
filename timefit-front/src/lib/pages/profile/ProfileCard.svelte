<script lang="ts">
    export let name: string;
    export let email: string;
    export let phone: string;
    export let profileImage: string | null = null;
</script>

<div class="card bg-base-100 border-base-200 border shadow-lg">
    <div class="card-body p-6">
        <div class="mb-6 flex items-start justify-between">
            <div class="flex-1">
                <h2 class="text-base-content mb-1 text-xl font-semibold">
                    {name}
                </h2>
                <p class="text-base-content/70 mb-1 text-sm">{email}</p>
                <p class="text-base-content/70 text-sm">{phone}</p>
            </div>

            <div class="avatar">
                <div class="bg-base-200 flex h-16 w-16 items-center justify-center rounded-full">
                    {#if profileImage}
                        <img
                            src={profileImage}
                            alt="Profile"
                            class="h-full w-full rounded-full object-cover"
                        />
                    {:else}
                        <img src="/profile.svg" alt="Profile" class="h-full w-full" />
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>
