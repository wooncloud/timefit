<script lang="ts">
	import type { Company } from '$lib/types/company';

	export let searchQuery = '';
	export let searchResults: Company[] = [];
	export let selectedCompany: Company | null = null;
	export let showSearchResults = false;

	export let onSearchInput: () => void;
	export let onSelectCompany: (company: Company) => void;
</script>

<div class="space-y-4">
	<div class="form-control">
		<label class="label" for="companySearch">
			<span class="label-text font-medium">회사 검색</span>
		</label>
		<div class="relative">
			<input
				id="companySearch"
				type="text"
				bind:value={searchQuery}
				on:input={onSearchInput}
				placeholder="회사명 또는 업종으로 검색"
				class="input input-bordered w-full focus:input-primary"
				required
			/>
			{#if showSearchResults && searchResults.length > 0}
				<div
					class="absolute z-10 w-full mt-1 bg-base-100 border border-base-300 rounded-lg shadow-lg max-h-48 overflow-y-auto"
				>
					{#each searchResults as company}
						<button
							type="button"
							class="w-full text-left p-3 hover:bg-base-200 border-b border-base-300 last:border-b-0"
							on:click={() => onSelectCompany(company)}
						>
							<div class="font-medium">{company.name}</div>
							<div class="text-sm text-base-content/70">{company.type} • {company.address}</div>
						</button>
					{/each}
				</div>
			{/if}
		</div>
	</div>

	{#if selectedCompany}
		<div class="alert alert-success">
			<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
				<path
					fill-rule="evenodd"
					d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
					clip-rule="evenodd"
				/>
			</svg>
			<div>
				<div class="font-medium">{selectedCompany.name} 선택됨</div>
				<div class="text-sm">{selectedCompany.type} • {selectedCompany.address}</div>
			</div>
		</div>
	{/if}
</div> 