#!/bin/bash

# 개발 환경 관리 스크립트

COMPOSE_CMD="docker-compose -f docker-compose.prod.yml --env-file .env.prod"

case "$1" in
  start)
    echo "🚀 Starting development environment..."
    $COMPOSE_CMD up -d --build
    echo "✅ Containers started!"
    echo "📊 Check logs: ./scripts/dev.sh logs"
    ;;

  restart)
    echo "🔄 Restarting backend container..."
    $COMPOSE_CMD restart timefit-backend
    echo "✅ Backend restarted!"
    ;;

  stop)
    echo "🛑 Stopping all containers..."
    $COMPOSE_CMD down
    echo "✅ Containers stopped!"
    ;;

  logs)
    docker logs timefit-backend -f --tail 100
    ;;

  db)
    docker exec -it timefit-postgres psql -U root -d postgres
    ;;

  status)
    $COMPOSE_CMD ps
    ;;

  clean)
    echo "🧹 Cleaning up containers and volumes..."
    $COMPOSE_CMD down -v
    docker volume rm timefit-back_gradle_cache 2>/dev/null || true
    echo "✅ Cleanup complete!"
    ;;

  rebuild)
    echo "🔨 Rebuilding without cache..."
    $COMPOSE_CMD build --no-cache
    $COMPOSE_CMD up -d
    echo "✅ Rebuild complete!"
    ;;

  *)
    echo "Usage: $0 {start|restart|stop|logs|db|status|clean|rebuild}"
    echo ""
    echo "Commands:"
    echo "  start   - Start development environment (first time or after stop)"
    echo "  restart - Restart backend container (after code changes)"
    echo "  stop    - Stop all containers"
    echo "  logs    - Show backend logs (follow mode)"
    echo "  db      - Connect to PostgreSQL database"
    echo "  status  - Show container status"
    echo "  clean   - Stop containers and remove volumes"
    echo "  rebuild - Rebuild containers without cache"
    exit 1
    ;;
esac
