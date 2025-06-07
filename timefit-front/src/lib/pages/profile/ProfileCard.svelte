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
                        <svg
                            class="text-base-content/40 h-8 w-8"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.89 1 3 1.89 3 3V21A2 2 0 0 0 5 23H19A2 2 0 0 0 21 21V9M19 9H14V4H5V19L12 12L19 19V9Z"
                            />
                        </svg>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>
