#!/usr/bin/env bash
cd /opt/iims
pkill -f 'iims-starter-1.0.0.jar' || true
nohup java -jar /opt/iims/app/iims-starter-1.0.0.jar > /opt/iims/server.log 2>&1 &
systemctl restart nginx
docker compose up -d
echo "restarted"
