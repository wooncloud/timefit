<script lang="ts">
	export let phone = '';
	export let companyName = '';
	export let businessType = '';
	export let businessNumber = '';
	export let businessAddress = '';
	export let companyPhone = '';
	export let companyDescription = '';
</script>

<div class="space-y-4">
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