<script lang="ts">
    import { supabase } from '$lib/supabase/supabaseClient';
</script>

<div class="hero">
    <div class="hero-content text-center">
        <div class="max-w-md">
            <h1 class="text-5xl font-bold">환영합니다!</h1>
            <p class="py-6">Timefit 예약 관리 플랫폼입니다</p>
            <button class="btn btn-primary" on:click={() => supabase.auth.signOut()}>
                로그아웃
            </button>
        </div>
    </div>
</div>
