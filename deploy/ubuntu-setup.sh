#!/bin/bash
# ============================================================
# Quiz 系统 Ubuntu 一键部署脚本（验收用）
# ------------------------------------------------------------
# 在 Ubuntu 虚拟机上以 root 执行，参数是解包后的部署目录：
#     sudo bash ubuntu-setup.sh /opt/quiz-deploy
#
# 目录里应当有：
#     quiz.sql                 数据库全量迁移
#     quiz-0.0.1-SNAPSHOT.jar  后端 jar
#     application.yml          后端外部配置（连库账号 quizuser）
#     nginx-quiz.conf          双站点 + 反向代理
#     admin/                   管理端构建产物（dist 内容）
#     user/                    用户端静态页（index/login/register/css/js）
# ============================================================
set -euo pipefail

SRC="${1:-/opt/quiz-deploy}"
JAR_NAME="quiz-0.0.1-SNAPSHOT.jar"

log() { echo -e "\n\033[1;36m==> $*\033[0m"; }

if [ "$(id -u)" != "0" ]; then
    echo "请用 root 或 sudo 执行"
    exit 1
fi

log "1/7 安装 MySQL / JDK / Nginx"
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y mysql-server openjdk-21-jre-headless nginx curl
systemctl enable --now mysql
systemctl enable --now nginx

log "2/7 导入数据库"
mysql < "$SRC/quiz.sql"
mysql <<'SQL'
CREATE USER IF NOT EXISTS 'quizuser'@'localhost' IDENTIFIED BY 'quiz12345';
GRANT ALL PRIVILEGES ON quiz.* TO 'quizuser'@'localhost';
FLUSH PRIVILEGES;
SQL
mysql -e "select count(*) as users from quiz.user; select count(*) as questions from quiz.questions where isDelete=0;"

log "3/7 部署后端 jar"
mkdir -p /opt/quiz
cp "$SRC/$JAR_NAME" /opt/quiz/
cp "$SRC/application.yml" /opt/quiz/application.yml

cat > /etc/systemd/system/quiz.service <<EOF
[Unit]
Description=Quiz Spring Boot backend
After=network.target mysql.service

[Service]
Type=simple
WorkingDirectory=/opt/quiz
ExecStart=/usr/bin/java -jar /opt/quiz/$JAR_NAME
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable quiz
systemctl restart quiz

log "   等待后端启动..."
for i in $(seq 1 40); do
    if curl -s -o /dev/null -m 2 "http://127.0.0.1:8080/login" -X POST \
        -H "Content-Type: application/json" -d '{}' ; then
        break
    fi
    sleep 2
done

log "4/7 部署两个前端到 /var/www"
rm -rf /var/www/quiz-admin /var/www/quiz-user
mkdir -p /var/www/quiz-admin /var/www/quiz-user
cp -r "$SRC/admin/." /var/www/quiz-admin/
cp -r "$SRC/user/." /var/www/quiz-user/

# 用户端改为同源访问，由 Nginx 反代到 8080
sed -i "s#window.QUIZ_API_BASE = 'http://localhost:8080'#window.QUIZ_API_BASE = ''#" \
    /var/www/quiz-user/js/config.js

log "5/7 配置 Nginx"
cp "$SRC/nginx-quiz.conf" /etc/nginx/conf.d/quiz.conf
rm -f /etc/nginx/sites-enabled/default
nginx -t
systemctl reload nginx

log "6/7 开放本机端口自检"
echo "--- 用户端 80 ---"
curl -s -o /dev/null -w "首页 HTTP %{http_code}\n" http://127.0.0.1/
echo "--- 管理端 8081 ---"
curl -s -o /dev/null -w "首页 HTTP %{http_code}\n" http://127.0.0.1:8081/
echo "--- 登录接口（经 Nginx 反代） ---"
curl -s -X POST http://127.0.0.1:8081/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"123456","userrole":"1"}' | head -c 200
echo

log "7/7 完成"
systemctl --no-pager --lines=0 status quiz | head -5
echo
echo "管理端 http://localhost:8081  （admin / 123456）"
echo "用户端 http://localhost      （李小明 / 888888）"
