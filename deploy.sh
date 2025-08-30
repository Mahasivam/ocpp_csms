#!/bin/bash

# OCPP CSMS Production Deployment Script
# This script helps deploy the OCPP CSMS system to production

set -e

echo "🚀 Starting OCPP CSMS Production Deployment..."

# Configuration
APP_NAME="ocpp-csms"
DOCKER_COMPOSE_FILE="docker-compose.yml"
LOG_DIR="./logs"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required files exist
check_requirements() {
    print_status "Checking deployment requirements..."
    
    if [ ! -f "$DOCKER_COMPOSE_FILE" ]; then
        print_error "docker-compose.yml not found!"
        exit 1
    fi
    
    if [ ! -f "Dockerfile" ]; then
        print_error "Dockerfile not found!"
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed!"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed!"
        exit 1
    fi
    
    print_status "✅ All requirements met"
}

# Create necessary directories
setup_directories() {
    print_status "Setting up directories..."
    
    mkdir -p "$LOG_DIR"
    mkdir -p ./monitoring/grafana/dashboards
    mkdir -p ./monitoring/grafana/datasources
    
    print_status "✅ Directories created"
}

# Build the application
build_application() {
    print_status "Building OCPP CSMS application..."
    
    docker-compose build --no-cache ocpp-csms
    
    print_status "✅ Application built successfully"
}

# Start the services
start_services() {
    print_status "Starting OCPP CSMS services..."
    
    # Start PostgreSQL first
    docker-compose up -d postgres
    
    # Wait for PostgreSQL to be ready
    print_status "Waiting for PostgreSQL to be ready..."
    sleep 10
    
    # Start the main application
    docker-compose up -d ocpp-csms
    
    # Start monitoring services
    docker-compose up -d prometheus grafana
    
    print_status "✅ All services started"
}

# Check service health
check_health() {
    print_status "Checking service health..."
    
    # Wait for services to start
    sleep 30
    
    # Check OCPP CSMS health
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        print_status "✅ OCPP CSMS is healthy"
    else
        print_warning "⚠️  OCPP CSMS health check failed - it might still be starting"
    fi
    
    # Check if PostgreSQL is running
    if docker-compose ps postgres | grep -q "Up"; then
        print_status "✅ PostgreSQL is running"
    else
        print_error "❌ PostgreSQL is not running"
    fi
    
    # Check if Prometheus is running
    if docker-compose ps prometheus | grep -q "Up"; then
        print_status "✅ Prometheus is running"
    else
        print_warning "⚠️  Prometheus is not running"
    fi
    
    # Check if Grafana is running
    if docker-compose ps grafana | grep -q "Up"; then
        print_status "✅ Grafana is running"
    else
        print_warning "⚠️  Grafana is not running"
    fi
}

# Display service URLs
show_endpoints() {
    print_status "🌐 Service endpoints:"
    echo ""
    echo "  📡 OCPP CSMS WebSocket: ws://localhost:8080/ocpp/{chargePointId}"
    echo "  🌐 OCPP CSMS REST API: http://localhost:8080/api"
    echo "  ❤️  Health Check: http://localhost:8080/actuator/health"
    echo "  📊 Metrics: http://localhost:8080/actuator/metrics"
    echo "  📈 Prometheus: http://localhost:9090"
    echo "  📊 Grafana: http://localhost:3000 (admin/admin)"
    echo ""
}

# Show logs
show_logs() {
    print_status "Recent logs from OCPP CSMS:"
    docker-compose logs --tail=20 ocpp-csms
}

# Main deployment function
deploy() {
    print_status "🚀 Starting deployment process..."
    
    check_requirements
    setup_directories
    build_application
    start_services
    check_health
    show_endpoints
    
    print_status "✅ Deployment completed successfully!"
    print_status "📝 Check logs with: docker-compose logs -f ocpp-csms"
    print_status "🔄 Restart services with: docker-compose restart"
    print_status "⏹️  Stop services with: docker-compose down"
}

# Handle command line arguments
case "$1" in
    "deploy")
        deploy
        ;;
    "start")
        start_services
        check_health
        show_endpoints
        ;;
    "stop")
        print_status "Stopping OCPP CSMS services..."
        docker-compose down
        print_status "✅ Services stopped"
        ;;
    "restart")
        print_status "Restarting OCPP CSMS services..."
        docker-compose restart
        check_health
        ;;
    "logs")
        show_logs
        ;;
    "status")
        check_health
        show_endpoints
        ;;
    "clean")
        print_status "Cleaning up Docker resources..."
        docker-compose down -v
        docker system prune -f
        print_status "✅ Cleanup completed"
        ;;
    *)
        echo "Usage: $0 {deploy|start|stop|restart|logs|status|clean}"
        echo ""
        echo "Commands:"
        echo "  deploy  - Full deployment (build and start all services)"
        echo "  start   - Start all services"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  logs    - Show recent logs"
        echo "  status  - Check service health and show endpoints"
        echo "  clean   - Clean up Docker resources"
        exit 1
        ;;
esac