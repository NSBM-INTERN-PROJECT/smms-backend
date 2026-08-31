#!/bin/bash

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

# =========================================
# Configuration
# =========================================

TMUX_SESSION="smms"

DISCOVERY_PORT="${DISCOVERY_PORT:-8761}"
CONFIG_PORT="${CONFIG_PORT:-8888}"
GATEWAY_PORT="${GATEWAY_PORT:-8080}"

# =========================================
# Helper functions
# =========================================

log() {
    echo ""
    echo "========================================="
    echo " $1"
    echo "========================================="
}

wait_for_port() {
    HOST="$1"
    PORT="$2"
    SERVICE="$3"

    echo "Waiting for $SERVICE on $HOST:$PORT..."

    for i in {1..60}; do
        if (echo > /dev/tcp/"$HOST"/"$PORT") >/dev/null 2>&1; then
            echo "$SERVICE is ready."
            return 0
        fi

        echo "  Waiting... ($i/60)"
        sleep 2
    done

    echo "ERROR: $SERVICE did not become ready."
    return 1
}

start_service() {
    SERVICE="$1"

    echo "Starting $SERVICE..."

    tmux new-window \
        -t "$TMUX_SESSION" \
        -n "$SERVICE"

    tmux send-keys \
        -t "$TMUX_SESSION:$SERVICE" \
        "cd '$PROJECT_ROOT/services/$SERVICE' && set -a && source '$PROJECT_ROOT/.env' && set +a && ./mvnw spring-boot:run" \
        C-m
}

# =========================================
# Load environment
# =========================================

cd "$PROJECT_ROOT"

log "Loading environment"

if [ ! -f ".env" ]; then
    echo "ERROR: .env file not found!"
    exit 1
fi

set -a
source .env
set +a

echo "Environment loaded."

# =========================================
# Start Docker
# =========================================

log "Starting Docker services"

docker compose up -d

echo "Docker services started."

# =========================================
# Wait for MySQL
# =========================================

log "Checking MySQL"

# Change this if your MySQL host/port is different
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"

wait_for_port "$MYSQL_HOST" "$MYSQL_PORT" "MySQL"

# =========================================
# Remove old tmux session
# =========================================

tmux kill-session -t "$TMUX_SESSION" 2>/dev/null || true

# =========================================
# Start Discovery Server
# =========================================

log "Starting Discovery Server"

tmux new-session \
    -d \
    -s "$TMUX_SESSION" \
    -n "discovery-server"

tmux send-keys \
    -t "$TMUX_SESSION:discovery-server" \
    "cd '$PROJECT_ROOT/services/discovery-server' && set -a && source '$PROJECT_ROOT/.env' && set +a && ./mvnw spring-boot:run" \
    C-m

wait_for_port "localhost" "$DISCOVERY_PORT" "Discovery Server"

# =========================================
# Start Config Server
# =========================================

log "Starting Config Server"

start_service "config-server"

wait_for_port "localhost" "$CONFIG_PORT" "Config Server"

# =========================================
# Start API Gateway
# =========================================

log "Starting API Gateway"

start_service "api-gateway"

wait_for_port "localhost" "$GATEWAY_PORT" "API Gateway"

# =========================================
# Start Business Services
# =========================================

log "Starting Business Services"

start_service "user-service"
start_service "auth-service"
start_service "allocation-service"
start_service "meeting-service"
start_service "session-service"
start_service "report-service"

# =========================================
# Finished
# =========================================

log "SMMS Backend Started"

echo "TMUX session: $TMUX_SESSION"
echo ""
echo "Services:"
echo "  Discovery Server : $DISCOVERY_PORT"
echo "  Config Server    : $CONFIG_PORT"
echo "  API Gateway      : $GATEWAY_PORT"
echo ""
echo "Business services:"
echo "  auth-service"
echo "  user-service"
echo "  allocation-service"
echo "  meeting-service"
echo "  session-service"
echo "  report-service"
echo ""
echo "Attach to the session:"
echo "  tmux attach -t $TMUX_SESSION"
echo ""
echo "Detach without stopping services:"
echo "  Ctrl+B, D"
echo ""

tmux attach -t "$TMUX_SESSION"