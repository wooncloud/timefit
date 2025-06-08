<script lang="ts">
    // PC v1 레이아웃 스크립트
</script>

<div class="pc-layout-v1">
    <header>
        <p>PC V1 Header</p>
    </header>
    <main>
        <slot></slot>
    </main>
    <footer>
        <p>PC V1 Footer</p>
    </footer>
</div>

<style>
    .pc-layout-v1 {
        /* PC 레이아웃 스타일 */
        padding: 20px;
        border: 1px dashed green;
        max-width: 1200px;
        margin: 0 auto;
    }
    header,
    footer {
        background-color: #e0e0e0;
        padding: 10px;
        text-align: center;
    }
</style>
