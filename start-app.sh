#!/bin/bash

# Configuration
FRONTEND_PORT=5501
BACKEND_STARTUP_WAIT=15

echo "🚀 Starting the 2Do application (Backend + Frontend)"

# 1. Start the backend with Docker
echo "🔧 Building and raising backend containers..."
cd backend
docker-compose up --build -d
cd ..  # 🔁 Return to the roots of the project

# 2. Waiting for backend initialisation
echo "⏳ Waiting $BACKEND_STARTUP_WAIT seconds for the backend to be ready..."
sleep $BACKEND_STARTUP_WAIT

# 3. Start frontend
echo "💻 Starting frontend server on http://localhost:$FRONTEND_PORT"
echo "📂 Frontend directory: $(pwd)/frontend"

# Check if live-server is installed
if command -v live-server &> /dev/null; then
    cd frontend && live-server --port=$FRONTEND_PORT
else
    echo "ℹ️ live-server not found, using alternative Python server"
    cd frontend && python3 -m http.server $FRONTEND_PORT
fi