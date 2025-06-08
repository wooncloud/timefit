<script lang="ts">
    import '../../../style.css';
</script>

<div class="min-h-screen bg-base-100">
    <div class="navbar bg-base-100 shadow-lg sticky top-0 z-50 px-2 sm:px-4">
        <div class="navbar-start">
            <div class="dropdown">
                <div tabindex="0" role="button" class="btn btn-ghost lg:hidden">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h8m-8 6h16" /></svg>
                </div>
                <ul class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52">
                    <li><a href="#">홈</a></li>
                    <li><a href="#">기능</a></li>
                    <li><a href="#">서비스</a></li>
                    <li><a href="#">문의</a></li>
                    <li class="divider"></li>
                    <li><a href="/signin">로그인</a></li>
                    <li><a href="/signup">회원가입</a></li>
                    <li><a href="/business/signin">사업자 로그인</a></li>
                </ul>
            </div>
            <a href="/" class="btn btn-ghost text-lg sm:text-xl font-bold text-primary">TimeFit</a>
        </div>
        <div class="navbar-center hidden lg:flex">
            <ul class="menu menu-horizontal px-1">
                <li><a href="#" class="hover:text-primary">홈</a></li>
                <li><a href="#" class="hover:text-primary">기능</a></li>
                <li><a href="#" class="hover:text-primary">서비스</a></li>
                <li><a href="#" class="hover:text-primary">문의</a></li>
            </ul>
        </div>
        <div class="navbar-end hidden md:flex">
            <a href="/signin" class="btn btn-primary btn-sm lg:btn-md mr-1 lg:mr-2">로그인</a>
            <a href="/signup" class="btn btn-outline btn-primary btn-sm lg:btn-md mr-1 lg:mr-2">회원가입</a>
            <a href="/business/signin" class="btn btn-secondary btn-sm lg:btn-md">사업자 로그인</a>
        </div>
        
        <!-- 모바일용 로그인 버튼 -->
        <div class="navbar-end md:hidden">
            <a href="/signin" class="btn btn-primary btn-sm">로그인</a>
        </div>
    </div>

    <main>
        <slot></slot>
    </main>

    <footer class="p-6 lg:p-10 bg-base-200 text-base-content">
        <div class="flex flex-col items-center space-y-4 md:space-y-6">
            <nav class="flex flex-col md:flex-row md:space-x-6 space-y-2 md:space-y-0 text-center">
                <a href="#" class="link link-hover text-sm">회사 소개</a>
                <a href="#" class="link link-hover text-sm">문의하기</a>
                <a href="#" class="link link-hover text-sm">채용</a>
                <a href="#" class="link link-hover text-sm">보도자료</a>
            </nav>
            <nav>
                <div class="flex flex-col md:flex-row space-y-2 md:space-y-0 md:space-x-4">
                </div>
            </nav>
            <aside class="text-center">
                <p class="text-xs sm:text-sm">Copyright © 2025 - All right reserved by Timefit</p>
            </aside>
        </div>
    </footer>
</div>

<style>
    footer {
        background-color: #e0e0e0;
        padding: 10px;
        text-align: center;
    }
</style>
