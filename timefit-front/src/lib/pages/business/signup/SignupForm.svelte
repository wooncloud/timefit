<script lang="ts">
	import { goto } from '$app/navigation';
	import type { Company } from '$lib/types/company';

	import UserAccountFields from '$lib/components/auth/UserAccountFields.svelte';
	import ExistingCompanySignupTab from './ExistingCompanySignupTab.svelte';
	import NewCompanySignupTab from './NewCompanySignupTab.svelte';
	import AuthFooter from '$lib/components/auth/AuthFooter.svelte';

	let signupType: 'existing' | 'new' = 'existing';
	let loading = false;

	// User Account
	let email = '';
	let password = '';
	let confirmPassword = '';
	let name = '';
	let agreeTerms = false;

	// New Company
	let phone = '';
	let companyName = '';
	let businessType = '';
	let businessNumber = '';
	let businessAddress = '';
	let companyPhone = '';
	let companyDescription = '';

	// Existing Company
	let searchQuery = '';
	let searchResults: Company[] = [];
	let selectedCompany: Company | null = null;
	let showSearchResults = false;

	const mockCompanies: Company[] = [
		{ id: 1, name: '카카오', type: 'IT/소프트웨어', address: '제주시 첨단로 242' },
		{ id: 2, name: '네이버', type: 'IT/소프트웨어', address: '성남시 분당구 정자일로 95' },
		{ id: 3, name: '삼성전자', type: '제조업', address: '수원시 영통구 삼성로 129' },
		{ id: 4, name: 'LG전자', type: '제조업', address: '서울시 영등포구 여의대로 128' },
		{ id: 5, name: '현대자동차', type: '자동차', address: '서울시 서초구 헌릉로 12' }
	];

	function searchCompanies() {
		if (searchQuery.trim()) {
			searchResults = mockCompanies.filter(
				(company) =>
					company.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
					company.type.toLowerCase().includes(searchQuery.toLowerCase())
			);
			showSearchResults = true;
		} else {
			searchResults = [];
			showSearchResults = false;
		}
	}

	function selectCompany(company: Company) {
		selectedCompany = company;
		searchQuery = company.name;
		showSearchResults = false;
	}

	async function handleNewCompanySignup() {
		if (
			!email ||
			!password ||
			!confirmPassword ||
			!name ||
			!phone ||
			!companyName ||
			!businessType ||
			!businessNumber ||
			!businessAddress ||
			!companyPhone
		) {
			alert('모든 필드를 입력해주세요.');
			return;
		}

		if (password !== confirmPassword) {
			alert('비밀번호가 일치하지 않습니다.');
			return;
		}

		if (!agreeTerms) {
			alert('이용약관에 동의해주세요.');
			return;
		}

		loading = true;
		try {
			console.log('New company signup:', {
				email,
				name,
				phone,
				companyName,
				businessType,
				businessNumber,
				businessAddress,
				companyPhone,
				companyDescription
			});
			await new Promise((resolve) => setTimeout(resolve, 1500));
			goto('/business/signin?message=회원가입이 완료되었습니다');
		} catch (error) {
			console.error('New company signup error:', error);
			alert('회원가입에 실패했습니다.');
		} finally {
			loading = false;
		}
	}

	async function handleExistingCompanySignup() {
		if (!email || !password || !confirmPassword || !name || !selectedCompany) {
			alert('모든 필드를 입력하고 회사를 선택해주세요.');
			return;
		}

		if (password !== confirmPassword) {
			alert('비밀번호가 일치하지 않습니다.');
			return;
		}

		if (!agreeTerms) {
			alert('이용약관에 동의해주세요.');
			return;
		}

		loading = true;
		try {
			console.log('Existing company signup:', {
				email,
				name,
				selectedCompany
			});
			await new Promise((resolve) => setTimeout(resolve, 1500));
			goto('/business/signin?message=회원가입이 완료되었습니다');
		} catch (error) {
			console.error('Existing company signup error:', error);
			alert('회원가입에 실패했습니다.');
		} finally {
			loading = false;
		}
	}
</script>

<div class="text-center mb-8">
	<h1 class="text-3xl font-bold text-base-content mb-2">사업자 회원가입</h1>
	<p class="text-base-content/70">비즈니스 계정을 생성하여 팀 관리를 시작하세요</p>
</div>

<!-- Signup Type Tabs -->
<div role="tablist" class="tabs tabs-box mb-6">
	<input
		type="radio"
		name="signup_type"
		role="tab"
		class="tab flex-1"
		aria-label="기존 회사 가입"
		value="existing"
		bind:group={signupType}
	/>
	<input
		type="radio"
		name="signup_type"
		role="tab"
		class="tab flex-1"
		aria-label="새 회사 등록"
		value="new"
		bind:group={signupType}
	/>
</div>

<!-- Common Fields -->
<div class="mb-6">
	<UserAccountFields bind:email bind:password bind:confirmPassword bind:name />
</div>

{#if signupType === 'existing'}
	<ExistingCompanySignupTab
		bind:searchQuery
		bind:agreeTerms
		bind:loading
		searchResults={searchResults}
		selectedCompany={selectedCompany}
		showSearchResults={showSearchResults}
		onSearchInput={searchCompanies}
		onSelectCompany={selectCompany}
		onSignup={handleExistingCompanySignup}
	/>
{:else}
	<NewCompanySignupTab
		bind:phone
		bind:companyName
		bind:businessType
		bind:businessNumber
		bind:businessAddress
		bind:companyPhone
		bind:companyDescription
		bind:agreeTerms
		bind:loading
		onSignup={handleNewCompanySignup}
	/>
{/if}

<AuthFooter pageType="signup" userType="business" /> 