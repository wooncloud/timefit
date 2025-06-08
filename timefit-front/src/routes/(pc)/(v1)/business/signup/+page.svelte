<script lang="ts">
    import { goto } from '$app/navigation';
    
    type Company = {
        id: number;
        name: string;
        type: string;
        address: string;
    };
    
    let signupType = 'existing';
    let loading = false;
    
    let email = '';
    let password = '';
    let confirmPassword = '';
    let name = '';
    let phone = '';
    let agreeTerms = false;
    
    let companyName = '';
    let businessType = '';
    let businessNumber = '';
    let businessAddress = '';
    let companyPhone = '';
    let companyDescription = '';
    
    let searchQuery = '';
    let searchResults: Company[] = [];
    let selectedCompany: Company | null = null;
    let showSearchResults = false;

    const mockCompanies = [
        { id: 1, name: '카카오', type: 'IT/소프트웨어', address: '제주시 첨단로 242' },
        { id: 2, name: '네이버', type: 'IT/소프트웨어', address: '성남시 분당구 정자일로 95' },
        { id: 3, name: '삼성전자', type: '제조업', address: '수원시 영통구 삼성로 129' },
        { id: 4, name: 'LG전자', type: '제조업', address: '서울시 영등포구 여의대로 128' },
        { id: 5, name: '현대자동차', type: '자동차', address: '서울시 서초구 헌릉로 12' }
    ];

    function searchCompanies() {
        if (searchQuery.trim()) {
            searchResults = mockCompanies.filter(company => 
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
        if (!email || !password || !confirmPassword || !name || !phone || 
            !companyName || !businessType || !businessNumber || !businessAddress || !companyPhone) {
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
                email, name, phone, companyName, businessType, 
                businessNumber, businessAddress, companyPhone, companyDescription 
            });
            await new Promise(resolve => setTimeout(resolve, 1500));
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
                email, name, selectedCompany 
            });
            await new Promise(resolve => setTimeout(resolve, 1500));
            goto('/business/signin?message=회원가입이 완료되었습니다');
        } catch (error) {
            console.error('Existing company signup error:', error);
            alert('회원가입에 실패했습니다.');
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>사업자 회원가입 - TimeFit</title>
    <meta name="description" content="TimeFit 사업자 서비스에 가입하여 팀과 조직의 시간 관리를 최적화하세요." />
</svelte:head>

<div class="min-h-screen flex">
    <!-- Left Hero Section -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-info to-success p-12 items-center justify-center">
        <div class="max-w-md text-white">
            <div class="mb-8">
                <div class="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center mb-6">
                    <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        <path fill-rule="evenodd" d="M4 4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2H4zm0 2v2h12V6H4zm0 4v4h12v-4H4z" clip-rule="evenodd" />
                    </svg>
                </div>
                <h2 class="text-3xl font-bold mb-4">비즈니스 혁신의 시작</h2>
                <p class="text-xl opacity-90 mb-8">팀과 조직을 위한 스마트한 시간 관리 솔루션으로 업무 효율성을 극대화하세요.</p>
            </div>
            
            <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6">
                <div class="space-y-4">
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3z"/>
                                <path d="M6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z"/>
                            </svg>
                        </div>
                        <span class="text-lg">통합 팀 관리 시스템</span>
                    </div>
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M3 3a1 1 0 000 2v8a2 2 0 002 2h2.586l-1.293 1.293a1 1 0 101.414 1.414L10 15.414l2.293 2.293a1 1 0 001.414-1.414L12.414 15H15a2 2 0 002-2V5a1 1 0 100-2H3zm11.707 4.707a1 1 0 00-1.414-1.414L10 9.586 8.707 8.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                        </div>
                        <span class="text-lg">실시간 프로젝트 추적</span>
                    </div>
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd" />
                            </svg>
                        </div>
                        <span class="text-lg">고급 분석 대시보드</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Form Section -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
        <div class="max-w-md w-full">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold text-base-content mb-2">사업자 회원가입</h1>
                <p class="text-base-content/70">비즈니스 계정을 생성하여 팀 관리를 시작하세요</p>
            </div>

            <!-- Signup Type Tabs -->
            <div class="tabs tabs-boxed mb-6">
                <button 
                    class="tab flex-1"
                    class:tab-active={signupType === 'existing'}
                    on:click={() => signupType = 'existing'}
                >
                    기존 회사 가입
                </button>
                <button 
                    class="tab flex-1"
                    class:tab-active={signupType === 'new'}
                    on:click={() => signupType = 'new'}
                >
                    새 회사 등록
                </button>
            </div>

            <!-- Common Fields -->
            <div class="space-y-4 mb-6">
                <div class="form-control">
                    <label class="label" for="email">
                        <span class="label-text font-medium">이메일</span>
                    </label>
                    <input 
                        id="email"
                        type="email" 
                        bind:value={email}
                        placeholder="business@company.com" 
                        class="input input-bordered w-full focus:input-primary" 
                        required
                    />
                </div>

                <div class="form-control">
                    <label class="label" for="password">
                        <span class="label-text font-medium">비밀번호</span>
                    </label>
                    <input 
                        id="password"
                        type="password" 
                        bind:value={password}
                        placeholder="최소 8자 이상" 
                        class="input input-bordered w-full focus:input-primary" 
                        minlength="8"
                        required
                    />
                </div>

                <div class="form-control">
                    <label class="label" for="confirmPassword">
                        <span class="label-text font-medium">비밀번호 확인</span>
                    </label>
                    <input 
                        id="confirmPassword"
                        type="password" 
                        bind:value={confirmPassword}
                        placeholder="비밀번호를 다시 입력하세요" 
                        class="input input-bordered w-full focus:input-primary" 
                        required
                    />
                </div>

                <div class="form-control">
                    <label class="label" for="name">
                        <span class="label-text font-medium">이름</span>
                    </label>
                    <input 
                        id="name"
                        type="text" 
                        bind:value={name}
                        placeholder="홍길동" 
                        class="input input-bordered w-full focus:input-primary" 
                        required
                    />
                </div>
            </div>

            {#if signupType === 'existing'}
                <!-- Existing Company Signup -->
                <div class="space-y-4 mb-6">
                    <div class="form-control">
                        <label class="label" for="companySearch">
                            <span class="label-text font-medium">회사 검색</span>
                        </label>
                        <div class="relative">
                            <input 
                                id="companySearch"
                                type="text" 
                                bind:value={searchQuery}
                                on:input={searchCompanies}
                                placeholder="회사명 또는 업종으로 검색" 
                                class="input input-bordered w-full focus:input-primary" 
                                required
                            />
                            {#if showSearchResults && searchResults.length > 0}
                                <div class="absolute z-10 w-full mt-1 bg-base-100 border border-base-300 rounded-lg shadow-lg max-h-48 overflow-y-auto">
                                    {#each searchResults as company}
                                        <button 
                                            type="button"
                                            class="w-full text-left p-3 hover:bg-base-200 border-b border-base-300 last:border-b-0"
                                            on:click={() => selectCompany(company)}
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
                                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                            </svg>
                            <div>
                                <div class="font-medium">{selectedCompany.name} 선택됨</div>
                                <div class="text-sm">{selectedCompany.type} • {selectedCompany.address}</div>
                            </div>
                        </div>
                    {/if}
                </div>

                <div class="form-control mb-6">
                    <label class="label cursor-pointer">
                        <input 
                            type="checkbox" 
                            bind:checked={agreeTerms}
                            class="checkbox checkbox-primary checkbox-sm" 
                        />
                        <span class="label-text ml-2">
                            <a href="/terms" class="link link-primary">이용약관</a> 및 
                            <a href="/privacy" class="link link-primary">개인정보처리방침</a>에 동의합니다
                        </span>
                    </label>
                </div>

                <button 
                    type="button"
                    on:click={handleExistingCompanySignup}
                    class="btn btn-primary w-full"
                    class:loading
                    disabled={loading}
                >
                    {loading ? '' : '기존 회사로 가입하기'}
                </button>

            {:else}
                <!-- New Company Signup -->
                <div class="space-y-4 mb-6">
                    <div class="form-control">
                        <label class="label" for="phone">
                            <span class="label-text font-medium">연락처</span>
                        </label>
                        <input 
                            id="phone"
                            type="tel" 
                            bind:value={phone}
                            placeholder="010-0000-0000" 
                            class="input input-bordered w-full focus:input-primary" 
                            required
                        />
                    </div>

                    <div class="form-control">
                        <label class="label" for="companyName">
                            <span class="label-text font-medium">상호명</span>
                        </label>
                        <input 
                            id="companyName"
                            type="text" 
                            bind:value={companyName}
                            placeholder="회사명을 입력하세요" 
                            class="input input-bordered w-full focus:input-primary" 
                            required
                        />
                    </div>

                    <div class="form-control">
                        <label class="label" for="businessType">
                            <span class="label-text font-medium">업종</span>
                        </label>
                        <select 
                            id="businessType"
                            bind:value={businessType}
                            class="select select-bordered w-full focus:select-primary" 
                            required
                        >
                            <option value="">업종을 선택하세요</option>
                            <option value="IT/소프트웨어">IT/소프트웨어</option>
                            <option value="제조업">제조업</option>
                            <option value="서비스업">서비스업</option>
                            <option value="유통/판매">유통/판매</option>
                            <option value="건설업">건설업</option>
                            <option value="금융업">금융업</option>
                            <option value="교육">교육</option>
                            <option value="의료/보건">의료/보건</option>
                            <option value="기타">기타</option>
                        </select>
                    </div>

                    <div class="form-control">
                        <label class="label" for="businessNumber">
                            <span class="label-text font-medium">사업자등록번호</span>
                        </label>
                        <input 
                            id="businessNumber"
                            type="text" 
                            bind:value={businessNumber}
                            placeholder="000-00-00000" 
                            class="input input-bordered w-full focus:input-primary" 
                            pattern="[0-9]{3}-[0-9]{2}-[0-9]{5}"
                            required
                        />
                    </div>

                    <div class="form-control">
                        <label class="label" for="businessAddress">
                            <span class="label-text font-medium">사업장 주소</span>
                        </label>
                        <input 
                            id="businessAddress"
                            type="text" 
                            bind:value={businessAddress}
                            placeholder="사업장 주소를 입력하세요" 
                            class="input input-bordered w-full focus:input-primary" 
                            required
                        />
                    </div>

                    <div class="form-control">
                        <label class="label" for="companyPhone">
                            <span class="label-text font-medium">업체 대표 연락처</span>
                        </label>
                        <input 
                            id="companyPhone"
                            type="tel" 
                            bind:value={companyPhone}
                            placeholder="02-0000-0000" 
                            class="input input-bordered w-full focus:input-primary" 
                            required
                        />
                    </div>

                    <div class="form-control">
                        <label class="label" for="companyDescription">
                            <span class="label-text font-medium">업체 설명 (선택)</span>
                        </label>
                        <textarea 
                            id="companyDescription"
                            bind:value={companyDescription}
                            placeholder="업체에 대한 간단한 설명을 입력하세요" 
                            class="textarea textarea-bordered w-full focus:textarea-primary h-20" 
                        ></textarea>
                    </div>
                </div>

                <div class="form-control mb-6">
                    <label class="label cursor-pointer">
                        <input 
                            type="checkbox" 
                            bind:checked={agreeTerms}
                            class="checkbox checkbox-primary checkbox-sm" 
                        />
                        <span class="label-text ml-2">
                            <a href="/terms" class="link link-primary">이용약관</a> 및 
                            <a href="/privacy" class="link link-primary">개인정보처리방침</a>에 동의합니다
                        </span>
                    </label>
                </div>

                <button 
                    type="button"
                    on:click={handleNewCompanySignup}
                    class="btn btn-primary w-full"
                    class:loading
                    disabled={loading}
                >
                    {loading ? '' : '새 회사로 가입하기'}
                </button>
            {/if}

            <div class="text-center mt-6">
                <p class="text-base-content/70 text-sm">
                    이미 사업자 계정이 있으신가요? 
                    <a href="/business/signin" class="link link-primary font-medium">사업자 로그인</a>
                </p>
            </div>

            <div class="text-center mt-4">
                <div class="divider">개인 계정으로 가입</div>
                <a href="/signup" class="link link-secondary text-sm">개인 사용자 회원가입 ></a>
            </div>
        </div>
    </div>
</div> 