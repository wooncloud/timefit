# ========================================
# Timefit Test Data Cleanup Script
# ========================================

Write-Host ""
Write-Host "Clean up Timefit test data..." -ForegroundColor Yellow
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

# Confirmation message
Write-Host "WARNING: All test data will be deleted!" -ForegroundColor Red
Write-Host ""
$confirmation = Read-Host "Continue? (y/N)"

if ($confirmation -ne "y" -and $confirmation -ne "Y") {
    Write-Host "Cancelled." -ForegroundColor Yellow
    Write-Host ""
    exit 0
}

Write-Host ""

# Check Docker container
Write-Host "Checking Docker container..." -ForegroundColor Yellow
$containerCheck = docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" 2>&1

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($containerCheck)) {
    Write-Host "Docker container not found: $CONTAINER_NAME" -ForegroundColor Red
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
    Write-Host ""
    exit 1
}

Write-Host "PostgreSQL connection successful!" -ForegroundColor Green
Write-Host ""

# Check data count before deletion
Write-Host "Data count before deletion:" -ForegroundColor Cyan
Write-Host ""

$beforeCountQuery = @"
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'business', COUNT(*) FROM business
UNION ALL
SELECT 'menu', COUNT(*) FROM menu
UNION ALL
SELECT 'booking_slot', COUNT(*) FROM booking_slot
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
ORDER BY table_name;
"@

docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $beforeCountQuery

Write-Host ""
Write-Host "Deleting data..." -ForegroundColor Yellow
Write-Host ""

# Delete with TRUNCATE CASCADE
$deleteQuery = @"
-- Delete in FK constraint order
TRUNCATE TABLE reservation CASCADE;
TRUNCATE TABLE booking_slot CASCADE;
TRUNCATE TABLE menu CASCADE;
TRUNCATE TABLE business_category CASCADE;
TRUNCATE TABLE business_hours CASCADE;
TRUNCATE TABLE operating_hours CASCADE;
TRUNCATE TABLE business_type CASCADE;
TRUNCATE TABLE user_business_role CASCADE;
TRUNCATE TABLE business CASCADE;
TRUNCATE TABLE users CASCADE;
"@

$result = docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $deleteQuery 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "Data deletion failed!" -ForegroundColor Red
    Write-Host $result
    Write-Host ""
    exit 1
}

Write-Host "Data deletion completed!" -ForegroundColor Green
Write-Host ""

# Check data count after deletion
Write-Host "Data count after deletion:" -ForegroundColor Cyan
Write-Host ""

$afterCountQuery = @"
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'business', COUNT(*) FROM business
UNION ALL
SELECT 'menu', COUNT(*) FROM menu
UNION ALL
SELECT 'booking_slot', COUNT(*) FROM booking_slot
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
ORDER BY table_name;
"@

docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c $afterCountQuery

Write-Host ""
Write-Host "Complete! All test data has been deleted." -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "   Insert test data: .\scripts\init-test-data.ps1"
Write-Host ""