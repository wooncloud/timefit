#!/bin/bash

# 개발 환경 관리 스크립트

case "$1" in
  start)
    echo "🚀 Starting development environment..."
    docker-compose --profile dev up -d --build
    echo "✅ Containers started!"
    echo "📊 Check logs: ./scripts/dev.sh logs"
    ;;

  restart)
    echo "🔄 Restarting backend container..."
    docker-compose restart timefit-back
    echo "✅ Backend restarted!"
    ;;

  stop)
    echo "🛑 Stopping all containers..."
    docker-compose down
    echo "✅ Containers stopped!"
    ;;

  logs)
    docker logs timefit-back -f --tail 100
    ;;

  db)
    docker exec -it timefit-postgres psql -U root -d postgres
    ;;

  status)
    docker-compose ps
    ;;

  clean)
    echo "🧹 Cleaning up containers and volumes..."
    docker-compose down -v
    docker volume rm timefit-back_gradle_cache 2>/dev/null || true
    echo "✅ Cleanup complete!"
    ;;

  rebuild)
    echo "🔨 Rebuilding without cache..."
    docker-compose --profile dev build --no-cache
    docker-compose --profile dev up -d
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
