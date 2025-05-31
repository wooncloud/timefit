<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    
    export let provider: string;
    export let text: string;
    export let iconSvg: string;
    
    const dispatch = createEventDispatcher();
    
    const handleClick = () => {
        dispatch('login', { provider });
    };
</script>

<button 
    class="btn btn-primary btn-block btn-lg"
    on:click={handleClick}
>
    <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24">
        {@html iconSvg}
    </svg>
    {text}
</button> 