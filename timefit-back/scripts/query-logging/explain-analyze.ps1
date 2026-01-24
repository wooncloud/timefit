# ================================================================
# Timefit SQL Query Extractor
# 작성자: 세창
# 설명: config.json 설정에 따라 로그에서 SQL 추출
# ================================================================

# 스크립트 위치 기반 경로 설정
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Resolve-Path (Join-Path $scriptDir "..\..")

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "SQL Query Extraction" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Config 로드
$configFile = Join-Path $scriptDir "config.json"
if (-Not (Test-Path $configFile)) {
    Write-Host "ERROR: config.json not found" -ForegroundColor Red
    exit 1
}

$config = Get-Content $configFile -Raw -Encoding UTF8 | ConvertFrom-Json
Write-Host "Config loaded" -ForegroundColor Green
Write-Host ""

# 입력 파일 경로 (config에서 가져옴)
$inputFile = Join-Path $projectRoot $config.input.logFileRelativePath

if (-Not (Test-Path $inputFile)) {
    Write-Host "ERROR: Log file not found" -ForegroundColor Red
    Write-Host "Expected: $inputFile" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Run Integration Test first" -ForegroundColor Yellow
    exit 1
}

Write-Host "Reading: $($config.input.logFileRelativePath)" -ForegroundColor Gray

# 안전한 파일 읽기 (StreamReader 사용)
$queries = @()
$reader = [System.IO.StreamReader]::new($inputFile, [System.Text.Encoding]::UTF8)

try {
    while ($null -ne ($line = $reader.ReadLine())) {
        # config의 패턴으로 SQL 탐지
        foreach ($pattern in $config.extraction.sqlPatterns) {
            if (-not $pattern.enabled) { continue }

            if ($line -match $pattern.regex) {
                $cleanedLine = $line

                # config의 cleanup 규칙 적용
                foreach ($rule in $config.extraction.cleanupRules) {
                    $cleanedLine = $cleanedLine -replace $rule.pattern, $rule.replace
                }

                # SQL로 시작하는지 확인
                if ($cleanedLine -match "^(SELECT|INSERT|UPDATE|DELETE)") {
                    $queries += $cleanedLine
                    break
                }
            }
        }
    }
}
finally {
    $reader.Close()
}

Write-Host "Extracted $($queries.Count) queries" -ForegroundColor Green
Write-Host ""

# 중복 제거 (config 설정)
if ($config.processing.removeDuplicates) {
    $queries = $queries | Select-Object -Unique
    Write-Host "After deduplication: $($queries.Count) queries" -ForegroundColor Gray
}

if ($queries.Count -eq 0) {
    Write-Host "WARNING: No queries found" -ForegroundColor Yellow
    exit 1
}

# 출력 파일 경로 (config에서 가져옴)
$outputFile = Join-Path $scriptDir $config.output.fileRelativePath

# 헤더 생성 (config 설정)
$output = ""

if ($config.output.format.includeHeader) {
    $output += "========================================`n"
    $output += "Timefit Extracted SQL Queries`n"
    $output += "========================================`n"

    if ($config.output.format.includeTimestamp) {
        $output += "Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n"
    }

    $output += "Total Queries: $($queries.Count)`n"
    $output += "========================================`n`n"
}

# 쿼리 출력 (config 설정)
$queryNum = 0
foreach ($query in $queries) {
    $queryNum++

    $output += "`n"
    $output += "$($config.output.format.commentPrefix)========================================`n"

    if ($config.output.format.includeQueryNumber) {
        $output += "$($config.output.format.commentPrefix)Query #$queryNum"
    }

    if ($config.output.format.includeQueryType) {
        $queryType = "UNKNOWN"
        if ($query -match "^SELECT") { $queryType = "SELECT" }
        elseif ($query -match "^INSERT") { $queryType = "INSERT" }
        elseif ($query -match "^UPDATE") { $queryType = "UPDATE" }
        elseif ($query -match "^DELETE") { $queryType = "DELETE" }

        if ($config.output.format.includeQueryNumber) {
            $output += " ($queryType)"
        } else {
            $output += "$($config.output.format.commentPrefix)$queryType"
        }
    }

    $output += "`n"
    $output += "$($config.output.format.commentPrefix)========================================`n"
    $output += "$query`n"
}

# 파일 저장 (UTF-8 without BOM)
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($outputFile, $output, $utf8NoBom)

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Completed!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Output: $($config.output.fileRelativePath)" -ForegroundColor Green
Write-Host "Location: $scriptDir" -ForegroundColor Gray
Write-Host ""