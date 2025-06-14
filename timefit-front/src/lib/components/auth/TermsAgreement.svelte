<script lang="ts">
	export let checked = false;
</script>

<div class="form-control">
	<label class="label cursor-pointer">
		<input type="checkbox" bind:checked class="checkbox checkbox-primary checkbox-sm" />
		<span class="label-text ml-2">
			<a href="/terms" class="link link-primary">이용약관</a> 및
			<a href="/privacy" class="link link-primary">개인정보처리방침</a>에 동의합니다
		</span>
	</label>
</div> 