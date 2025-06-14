<script lang="ts">
	export let email = '';
	export let password = '';
	export let confirmPassword = '';
	export let name = '';
</script>

<div class="space-y-4">
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