#!/usr/bin/env bash
set -e
cd /opt/iims

echo "[1/6] install base packages"
yum install -y java-17-openjdk nginx docker || dnf install -y java-17-openjdk nginx docker
systemctl enable --now docker
systemctl enable --now nginx

if ! docker compose version >/dev/null 2>&1; then
  echo "[2/6] install docker compose plugin"
  mkdir -p /usr/local/lib/docker/cli-plugins
  curl -L https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
  chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
fi

echo "[3/6] start middleware"
docker compose up -d
sleep 25

echo "[4/6] init database"
docker exec -i iims-mysql mysql -uroot -proot < /opt/iims/sql/init-data.sql || true

echo "[5/6] prepare minio bucket"
docker exec iims-minio sh -c 'mc alias set local http://127.0.0.1:9000 minioadmin minioadmin >/dev/null && mc mb -p local/iims-bucket >/dev/null 2>&1 || true'

echo "[6/6] start backend and nginx"
pkill -f 'iims-starter-1.0.0.jar' || true
nohup java -jar /opt/iims/app/iims-starter-1.0.0.jar > /opt/iims/server.log 2>&1 &
cp /opt/iims/nginx/iims.conf /etc/nginx/conf.d/iims.conf
nginx -t
systemctl restart nginx
sleep 10

echo "done"
echo "frontend: http://47.93.158.196"
echo "backend:  http://47.93.158.196:8090/iims/user/login/key"
echo "minio:    http://47.93.158.196:9001"
