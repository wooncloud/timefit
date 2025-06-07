<script lang="ts">
    export let termsUrl: string = '/terms';
    export let privacyUrl: string = '/privacy';
    export let text: string = '계속 진행하면';
    export let termsText: string = '이용약관';
    export let privacyText: string = '개인정보처리방침';
    export let agreementText: string = '에 동의한 것으로 간주됩니다.';
</script>

<div class="text-base-content/60 mt-6 text-xs leading-relaxed">
    {text}
    <a href={termsUrl} class="link link-primary">{termsText}</a>과
    <a href={privacyUrl} class="link link-primary">{privacyText}</a>{agreementText}
</div>
