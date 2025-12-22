# ========================================
# Timefit Test Data Initialization Script
# ========================================
# Always inserts BULK data (10,000 reservations)
# Date Range: TODAY ~ +60 DAYS (No past dates!)
# Sufficient for Level 1, 2, and 3 testing
# ========================================

Write-Host ""
Write-Host "Insert Timefit BULK test data..." -ForegroundColor Green
Write-Host "(10,000 reservations - sufficient for all tests)" -ForegroundColor Cyan
Write-Host ""

# Docker container name (modify as needed)
$CONTAINER_NAME = if ($env:POSTGRES_CONTAINER) { $env:POSTGRES_CONTAINER } else { "timefit-postgres" }
$DB_NAME = if ($env:POSTGRES_DB) { $env:POSTGRES_DB } else { "postgres" }
$DB_USER = if ($env:POSTGRES_USER) { $env:POSTGRES_USER } else { "root" }

Write-Host "Database connection info:" -ForegroundColor Cyan
Write-Host "   Container: $CONTAINER_NAME"
Write-Host "   Database: $DB_NAME"
Write-Host "   User: $DB_USER"
Write-Host ""

# Check Docker container
Write-Host "Checking Docker container..." -ForegroundColor Yellow
$containerCheck = docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" 2>&1

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($containerCheck)) {
    Write-Host "Docker container not found: $CONTAINER_NAME" -ForegroundColor Red
    Write-Host ""
    Write-Host "Solutions:" -ForegroundColor Yellow
    Write-Host "   1. Check container name: docker ps" -ForegroundColor Yellow
    Write-Host "   2. Set env variable: `$env:POSTGRES_CONTAINER='actual_container_name'" -ForegroundColor Yellow
    Write-Host "   3. Start PostgreSQL: docker-compose up -d" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host "Docker container is running: $containerCheck" -ForegroundColor Green
Write-Host ""

# Check PostgreSQL connection
Write-Host "Checking PostgreSQL connection..." -ForegroundColor Yellow
$connectionTest = docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "SELECT 1;" 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "PostgreSQL connection failed!" -ForegroundColor Red
    Write-Host "   - Check PostgreSQL status: docker exec $CONTAINER_NAME pg_isready" -ForegroundColor Red
    Write-Host ""
    exit 1
}

Write-Host "PostgreSQL connection successful!" -ForegroundColor Green
Write-Host ""

# Check existing tables
Write-Host "Checking existing tables..." -ForegroundColor Cyan
$tableCheck = docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "\dt" 2>&1
Write-Host $tableCheck
Write-Host ""

if ($tableCheck -match "Did not find any relations") {
    Write-Host "Tables do not exist!" -ForegroundColor Yellow
    Write-Host "   Run Spring Boot first to create tables." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "   1. cd .. (go up from scripts)" -ForegroundColor Yellow
    Write-Host "   2. .\gradlew bootRun" -ForegroundColor Yellow
    Write-Host "   3. Stop server with Ctrl+C after startup" -ForegroundColor Yellow
    Write-Host "   4. Run .\scripts\init-test-data.ps1 again" -ForegroundColor Yellow
    Write-Host ""

    $continue = Read-Host "Continue anyway? (y/N)"
    if ($continue -ne "y" -and $continue -ne "Y") {
        exit 0
    }
}

Write-Host ""

# SQL file path (always use bulk)
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptPath "db\seed-bulk.sql"

if (-not (Test-Path $sqlFile)) {
    Write-Host "SQL file not found: $sqlFile" -ForegroundColor Red
    Write-Host ""
    exit 1
}

# Insert test data
Write-Host "Inserting test data..." -ForegroundColor Yellow
Write-Host "   File: $sqlFile"
Write-Host ""
Write-Host "This may take 30-60 seconds..." -ForegroundColor Yellow
Write-Host ""

# Read SQL file as UTF-8 and execute
Write-Host "Executing SQL... (detailed logs)" -ForegroundColor Cyan
$result = Get-Content $sqlFile -Encoding UTF8 -Raw | docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME 2>&1

# Output result (for debugging)
Write-Host ""
Write-Host "SQL execution result:" -ForegroundColor Cyan
Write-Host $result
Write-Host ""

if ($LASTEXITCODE -ne 0) {
    Write-Host "Test data insertion failed!" -ForegroundColor Red
    Write-Host ""
    exit 1
}

Write-Host "Test data insertion completed!" -ForegroundColor Green
Write-Host ""

# Check results
Write-Host "Created data count:" -ForegroundColor Cyan
Write-Host ""

$countQuery = @"
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'business', COUNT(*) FROM business
UNION ALL
SELECT 'operating_hours', COUNT(*) FROM operating_hours
UNION ALL
SELECT 'business_category', COUNT(*) FROM business_category
UNION ALL
SELECT 'menu', COUNT(*) FROM menu
UNION ALL
SELECT 'booking_slot', COUNT(*) FROM booking_slot
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
ORDER BY table_name;
"@

docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $countQuery

Write-Host ""
Write-Host "Created users:" -ForegroundColor Cyan
Write-Host ""

$userQuery = @"
SELECT
    email,
    name,
    role
FROM users
ORDER BY role DESC
LIMIT 10;
"@

docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $userQuery

Write-Host ""
Write-Host "Created businesses:" -ForegroundColor Cyan
Write-Host ""

$businessQuery = @"
SELECT
    business_name,
    contact_phone,
    address
FROM business;
"@

docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $businessQuery

Write-Host ""
Write-Host "Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Test account info:" -ForegroundColor Cyan
Write-Host "   Owner: owner1@timefit.com / password123"
Write-Host "   Customer: customer1@timefit.com / password123"
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "   1. Run backend: .\gradlew bootRun"
Write-Host "   2. Run tests:"
Write-Host "      - Level 1: npm run test:pattern:l1:load"
Write-Host "      - Level 2: npm run test:pattern:l2:load"
Write-Host "      - Level 3: npm run test:pattern:l3:load"
Write-Host ""