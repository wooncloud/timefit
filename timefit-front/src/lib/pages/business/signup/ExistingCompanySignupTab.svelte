<script lang="ts">
	import type { Company } from '$lib/types/company';
	import TermsAgreement from '$lib/components/auth/TermsAgreement.svelte';
	import ExistingCompanyFields from './ExistingCompanyFields.svelte';

	export let searchQuery: string;
	export let searchResults: Company[];
	export let selectedCompany: Company | null;
	export let showSearchResults: boolean;
	export let onSearchInput: () => void;
	export let onSelectCompany: (company: Company) => void;

	export let agreeTerms: boolean;

	export let loading: boolean;
	export let onSignup: () => Promise<void>;
</script>

<div class="mb-6">
	<ExistingCompanyFields
		bind:searchQuery
		searchResults={searchResults}
		selectedCompany={selectedCompany}
		showSearchResults={showSearchResults}
		onSearchInput={onSearchInput}
		onSelectCompany={onSelectCompany}
	/>
</div>

<div class="mb-6">
	<TermsAgreement bind:checked={agreeTerms} />
</div>

<button
	type="button"
	on:click={onSignup}
	class="btn btn-primary w-full"
	class:loading
	disabled={loading}
>
	{#if !loading}기존 회사로 가입하기{/if}
</button> 