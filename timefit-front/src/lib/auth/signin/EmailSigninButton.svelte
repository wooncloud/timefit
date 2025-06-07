<script lang="ts">
    export let href: string = '/m/signin/email';
    export let text: string = '이메일로 로그인';
</script>

<button class="btn btn-ghost btn-block" on:click={() => (window.location.href = href)}>
    {text}
</button>
