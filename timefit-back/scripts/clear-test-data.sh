#!/bin/bash
# ========================================
# Timefit Test Data Cleanup Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo -e "${YELLOW}Clean up Timefit test data...${NC}"
echo ""

# Docker container name (modify as needed)
CONTAINER_NAME=${POSTGRES_CONTAINER:-"timefit-postgres"}
DB_NAME=${POSTGRES_DB:-"postgres"}
DB_USER=${POSTGRES_USER:-"root"}

echo -e "${CYAN}Database connection info:${NC}"
echo "   Container: $CONTAINER_NAME"
echo "   Database: $DB_NAME"
echo "   User: $DB_USER"
echo ""

# Confirmation message
echo -e "${RED}WARNING: All test data will be deleted!${NC}"
echo ""
read -p "Continue? (y/N): " confirmation

if [[ ! "$confirmation" =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Cancelled.${NC}"
    echo ""
    exit 0
fi

echo ""

# Check Docker container
echo -e "${YELLOW}Checking Docker container...${NC}"
containerCheck=$(docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" 2>&1)

if [ $? -ne 0 ] || [ -z "$containerCheck" ]; then
    echo -e "${RED}Docker container not found: $CONTAINER_NAME${NC}"
    echo ""
    exit 1
fi

echo -e "${GREEN}Docker container is running: $containerCheck${NC}"
echo ""

# Check PostgreSQL connection
echo -e "${YELLOW}Checking PostgreSQL connection...${NC}"
connectionTest=$(docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1;" 2>&1)

if [ $? -ne 0 ]; then
    echo -e "${RED}PostgreSQL connection failed!${NC}"
    echo ""
    exit 1
fi

echo -e "${GREEN}PostgreSQL connection successful!${NC}"
echo ""

# Check data count before deletion
echo -e "${CYAN}Data count before deletion:${NC}"
echo ""

beforeCountQuery="SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'business', COUNT(*) FROM business
UNION ALL
SELECT 'menu', COUNT(*) FROM menu
UNION ALL
SELECT 'booking_slot', COUNT(*) FROM booking_slot
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
ORDER BY table_name;"

docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$beforeCountQuery"

echo ""
echo -e "${YELLOW}Deleting data...${NC}"
echo ""

# Delete with TRUNCATE CASCADE
deleteQuery="-- Delete in FK constraint order
TRUNCATE TABLE reservation CASCADE;
TRUNCATE TABLE booking_slot CASCADE;
TRUNCATE TABLE menu CASCADE;
TRUNCATE TABLE business_category CASCADE;
TRUNCATE TABLE business_hours CASCADE;
TRUNCATE TABLE operating_hours CASCADE;
TRUNCATE TABLE business_type CASCADE;
TRUNCATE TABLE user_business_role CASCADE;
TRUNCATE TABLE business CASCADE;
TRUNCATE TABLE users CASCADE;"

result=$(docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$deleteQuery" 2>&1)

if [ $? -ne 0 ]; then
    echo -e "${RED}Data deletion failed!${NC}"
    echo "$result"
    echo ""
    exit 1
fi

echo -e "${GREEN}Data deletion completed!${NC}"
echo ""

# Check data count after deletion
echo -e "${CYAN}Data count after deletion:${NC}"
echo ""

afterCountQuery="SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'business', COUNT(*) FROM business
UNION ALL
SELECT 'menu', COUNT(*) FROM menu
UNION ALL
SELECT 'booking_slot', COUNT(*) FROM booking_slot
UNION ALL
SELECT 'reservation', COUNT(*) FROM reservation
ORDER BY table_name;"

docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$afterCountQuery"

echo ""
echo -e "${GREEN}Complete! All test data has been deleted.${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "   Insert test data: ./scripts/init-test-data.sh"
echo ""