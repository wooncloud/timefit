#!/bin/bash
# ========================================
# Timefit Test Data Initialization Script
# ========================================
# Always inserts BULK data (10,000 reservations)
# Date Range: TODAY ~ +60 DAYS (No past dates!)
# Sufficient for Level 1, 2, and 3 testing
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo -e "${GREEN}Insert Timefit BULK test data...${NC}"
echo -e "${CYAN}(10,000 reservations - sufficient for all tests)${NC}"
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

# Check Docker container
echo -e "${YELLOW}Checking Docker container...${NC}"
containerCheck=$(docker ps --filter "name=$CONTAINER_NAME" --format "{{.Names}}" 2>&1)

if [ $? -ne 0 ] || [ -z "$containerCheck" ]; then
    echo -e "${RED}Docker container not found: $CONTAINER_NAME${NC}"
    echo ""
    echo -e "${YELLOW}Solutions:${NC}"
    echo -e "   1. Check container name: ${YELLOW}docker ps${NC}"
    echo -e "   2. Set env variable: ${YELLOW}export POSTGRES_CONTAINER='actual_container_name'${NC}"
    echo -e "   3. Start PostgreSQL: ${YELLOW}docker-compose up -d${NC}"
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
    echo -e "   - Check PostgreSQL status: ${RED}docker exec $CONTAINER_NAME pg_isready${NC}"
    echo ""
    exit 1
fi

echo -e "${GREEN}PostgreSQL connection successful!${NC}"
echo ""

# Check existing tables
echo -e "${CYAN}Checking existing tables...${NC}"
tableCheck=$(docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "\dt" 2>&1)
echo "$tableCheck"
echo ""

if echo "$tableCheck" | grep -q "Did not find any relations"; then
    echo -e "${YELLOW}Tables do not exist!${NC}"
    echo -e "   Run Spring Boot first to create tables."
    echo ""
    echo -e "${YELLOW}   1. cd .. (go up from scripts)${NC}"
    echo -e "${YELLOW}   2. ./gradlew bootRun${NC}"
    echo -e "${YELLOW}   3. Stop server with Ctrl+C after startup${NC}"
    echo -e "${YELLOW}   4. Run ./scripts/init-test-data.sh again${NC}"
    echo ""

    read -p "Continue anyway? (y/N): " continue
    if [[ ! "$continue" =~ ^[Yy]$ ]]; then
        exit 0
    fi
fi

echo ""

# SQL file path (always use bulk)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="$SCRIPT_DIR/db/seed-bulk.sql"

if [ ! -f "$SQL_FILE" ]; then
    echo -e "${RED}SQL file not found: $SQL_FILE${NC}"
    echo ""
    exit 1
fi

# Insert test data
echo -e "${YELLOW}Inserting test data...${NC}"
echo "   File: $SQL_FILE"
echo ""
echo -e "${YELLOW}This may take 30-60 seconds...${NC}"
echo ""

# Read SQL file and execute
echo -e "${CYAN}Executing SQL... (detailed logs)${NC}"
result=$(cat "$SQL_FILE" | docker exec -i "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" 2>&1)

# Output result (for debugging)
echo ""
echo -e "${CYAN}SQL execution result:${NC}"
echo "$result"
echo ""

if [ $? -ne 0 ]; then
    echo -e "${RED}Test data insertion failed!${NC}"
    echo ""
    exit 1
fi

echo -e "${GREEN}Test data insertion completed!${NC}"
echo ""

# Check results
echo -e "${CYAN}Created data count:${NC}"
echo ""

countQuery="SELECT 'users' as table_name, COUNT(*) as count FROM users
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
ORDER BY table_name;"

docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$countQuery"

echo ""
echo -e "${CYAN}Created users:${NC}"
echo ""

userQuery="SELECT
    email,
    name,
    role
FROM users
ORDER BY role DESC
LIMIT 10;"

docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$userQuery"

echo ""
echo -e "${CYAN}Created businesses:${NC}"
echo ""

businessQuery="SELECT
    business_name,
    contact_phone,
    address
FROM business;"

docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -c "$businessQuery"

echo ""
echo -e "${GREEN}Complete!${NC}"
echo ""
echo -e "${CYAN}Test account info:${NC}"
echo "   Owner: owner1@timefit.com / password123"
echo "   Customer: customer1@timefit.com / password123"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "   1. Run backend: ./gradlew bootRun"
echo "   2. Run tests:"
echo "      - Level 1: npm run test:pattern:l1:load"
echo "      - Level 2: npm run test:pattern:l2:load"
echo "      - Level 3: npm run test:pattern:l3:load"
echo ""