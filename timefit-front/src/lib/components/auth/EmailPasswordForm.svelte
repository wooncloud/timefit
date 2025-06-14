<script lang="ts">
	export let email = '';
	export let password = '';
	export let loading = false;
	export let emailLabel = '이메일';
	export let submitButtonText = '로그인';
</script>

<form on:submit|preventDefault class="space-y-4">
	<div class="form-control">
		<label class="label" for="email">
			<span class="label-text font-medium">{emailLabel}</span>
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
			placeholder="비밀번호를 입력하세요"
			class="input input-bordered w-full focus:input-primary"
			required
		/>
	</div>

	<div class="flex items-center justify-between">
		<label class="label cursor-pointer">
			<input type="checkbox" class="checkbox checkbox-primary checkbox-sm" />
			<span class="label-text ml-2">로그인 상태 유지</span>
		</label>
		<a href="/business/forgot-password" class="link link-primary text-sm">비밀번호 찾기</a>
	</div>

	<button type="submit" class="btn btn-primary w-full" class:loading disabled={loading}>
		{#if !loading}{submitButtonText}{/if}
	</button>
</form> 