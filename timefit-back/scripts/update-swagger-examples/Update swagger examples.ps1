# ================================================================
# Swagger Example 자동 수정 스크립트 (PowerShell)
# 작성자: 세창
# 버전: 1.0
# 설명: mappings.json 기반으로 Swagger annotation의 example 값 자동 치환
# ================================================================

# 실행 디렉토리 확인
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$scriptsDir = Split-Path -Parent $scriptDir
$projectRoot = Split-Path -Parent $scriptsDir
Set-Location $projectRoot

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Swagger Example 자동 수정 시작" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 1. 매핑 파일 로드
$mappingsPath = Join-Path $scriptDir "mappings.json"
if (-Not (Test-Path $mappingsPath)) {
    Write-Host "❌ 오류: mappings.json 파일을 찾을 수 없습니다." -ForegroundColor Red
    Write-Host "경로: $mappingsPath" -ForegroundColor Yellow
    exit 1
}

Write-Host "📋 매핑 파일 로드 중..." -ForegroundColor Green
$mappings = Get-Content $mappingsPath -Raw -Encoding UTF8 | ConvertFrom-Json

# 2. 대상 디렉토리 확인 (전체 timefit 패키지)
$targetDir = "web\src\main\java\timefit"
if (-Not (Test-Path $targetDir)) {
    Write-Host "❌ 오류: timefit 디렉토리를 찾을 수 없습니다." -ForegroundColor Red
    Write-Host "경로: $targetDir" -ForegroundColor Yellow
    exit 1
}

# 3. 백업 생성
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupDir = "swagger-backup\$timestamp"
Write-Host "💾 백업 생성 중: $backupDir" -ForegroundColor Yellow

New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
Copy-Item -Path $swaggerDir -Destination $backupDir -Recurse -Force
Write-Host "✅ 백업 완료!" -ForegroundColor Green
Write-Host ""

# 4. Java 파일 검색
Write-Host "🔍 Java 파일 검색 중..." -ForegroundColor Green
$javaFiles = Get-ChildItem -Path $targetDir -Recurse -Filter "*.java"
$totalFiles = $javaFiles.Count
Write-Host "📁 찾은 파일 개수: $totalFiles" -ForegroundColor Cyan
Write-Host ""

# 5. 치환 실행
Write-Host "🔧 파일 수정 중..." -ForegroundColor Green
$modifiedCount = 0

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    $fileModified = $false

    # 5.1 이메일 치환
    foreach ($key in $mappings.emails.PSObject.Properties.Name) {
        $value = $mappings.emails.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.2 비밀번호 치환
    foreach ($key in $mappings.passwords.PSObject.Properties.Name) {
        $value = $mappings.passwords.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.3 UUID 치환 (KEEP_AS_IS 제외)
    foreach ($key in $mappings.uuids.PSObject.Properties.Name) {
        $value = $mappings.uuids.$key
        if ($value -ne "KEEP_AS_IS" -and $content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.4 전화번호 치환
    foreach ($key in $mappings.phones.PSObject.Properties.Name) {
        $value = $mappings.phones.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.5 이름 치환
    foreach ($key in $mappings.names.PSObject.Properties.Name) {
        $value = $mappings.names.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.6 주소 치환
    foreach ($key in $mappings.addresses.PSObject.Properties.Name) {
        $value = $mappings.addresses.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.7 날짜 치환
    foreach ($key in $mappings.dates.PSObject.Properties.Name) {
        $value = $mappings.dates.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.8 시간 치환
    foreach ($key in $mappings.times.PSObject.Properties.Name) {
        $value = $mappings.times.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 5.9 사업자번호 치환
    foreach ($key in $mappings.businessNumbers.PSObject.Properties.Name) {
        $value = $mappings.businessNumbers.$key
        if ($content -match [regex]::Escape($key)) {
            $content = $content -replace [regex]::Escape($key), $value
            $fileModified = $true
        }
    }

    # 수정된 경우 파일 저장
    if ($fileModified) {
        # UTF-8 BOM 제거하여 저장
        $utf8NoBom = New-Object System.Text.UTF8Encoding $false
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        $modifiedCount++
        Write-Host "  ✓ $($file.Name)" -ForegroundColor Gray
    }
}

# 6. 완료 메시지
Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "✅ 완료!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 통계:" -ForegroundColor Yellow
Write-Host "  - 전체 파일: $totalFiles" -ForegroundColor White
Write-Host "  - 수정된 파일: $modifiedCount" -ForegroundColor Green
Write-Host "  - 백업 위치: $backupDir" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔍 다음 단계:" -ForegroundColor Yellow
Write-Host "  1. git diff로 변경 사항 확인" -ForegroundColor White
Write-Host "     git diff web/src/main/java/timefit" -ForegroundColor Gray
Write-Host ""
Write-Host "  2. 서버 재시작 후 Swagger UI 확인" -ForegroundColor White
Write-Host "     http://localhost:8080/swagger-ui/index.html" -ForegroundColor Gray
Write-Host ""
Write-Host "  3. Postman 재 import (필요시)" -ForegroundColor White
Write-Host "     http://localhost:8080/v3/api-docs" -ForegroundColor Gray
Write-Host ""