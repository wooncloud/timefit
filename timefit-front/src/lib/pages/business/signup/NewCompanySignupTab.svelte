<script lang="ts">
	import TermsAgreement from '$lib/components/auth/TermsAgreement.svelte';
	import NewCompanyFields from './NewCompanyFields.svelte';

	export let phone: string;
	export let companyName: string;
	export let businessType: string;
	export let businessNumber: string;
	export let businessAddress: string;
	export let companyPhone: string;
	export let companyDescription: string;

	export let agreeTerms: boolean;

	export let loading: boolean;
	export let onSignup: () => Promise<void>;
</script>

<div class="space-y-4 mb-6">
	<NewCompanyFields
		bind:phone
		bind:companyName
		bind:businessType
		bind:businessNumber
		bind:businessAddress
		bind:companyPhone
		bind:companyDescription
	/>
</div>

<div class="form-control mb-6">
	<TermsAgreement bind:checked={agreeTerms} />
</div>

<button
	type="button"
	on:click={onSignup}
	class="btn btn-primary w-full"
	class:loading
	disabled={loading}
>
	{#if !loading}새 회사로 가입하기{/if}
</button> 