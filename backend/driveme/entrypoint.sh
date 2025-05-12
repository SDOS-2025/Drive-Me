#!/bin/sh

# Debug: Check directories, permissions, and environment
echo "Debug: Checking directories and permissions..."
ls -ld /run/mysqld /var/lib/mysql /var/log/mysql
whoami
env
cat /etc/mysql/my.cnf

# Initialize MySQL if needed
if [ ! -d "/var/lib/mysql/mysql" ]; then
  echo "Initializing MySQL..."
  mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql || {
    echo "mysqld --initialize-insecure failed"
    cat /var/log/mysql/mysql.log
    exit 1
  }
fi

# Start MySQL server in background
echo "Starting MySQL..."
mysqld --user=mysql --datadir=/var/lib/mysql --socket=/run/mysqld/mysqld.sock --verbose &> /var/log/mysql/mysqld.log &
MYSQLD_PID=$!

# Wait for MySQL to start and create socket
for i in {30..0}; do
  if [ -S /run/mysqld/mysqld.sock ] && mysqladmin ping -uroot --socket=/run/mysqld/mysqld.sock --silent; then
    echo "MySQL started successfully"
    break
  fi
  echo "Waiting for MySQL to start... ($i seconds left)"
  if ! ps -p $MYSQLD_PID > /dev/null; then
    echo "mysqld process ($MYSQLD_PID) is not running"
    echo "MySQL log contents:"
    cat /var/log/mysql/mysql.log
    echo "Running processes:"
    ps aux
    exit 1
  fi
  sleep 1
done
if [ "$i" = 0 ]; then
  echo "MySQL failed to start. Check logs:"
  cat /var/log/mysql/mysql.log
  echo "Running processes:"
  ps aux
  echo "Socket status:"
  ls -l /run/mysqld/mysqld.sock || echo "Socket not found"
  exit 1
fi

# Debug: Check socket and user authentication plugin
echo "Checking socket and root user authentication plugin..."
ls -l /run/mysqld/mysqld.sock
mysql -uroot --socket=/run/mysqld/mysqld.sock -e "SELECT User, Host, plugin FROM mysql.user WHERE User = 'root';" || {
  echo "Failed to check root user"
  cat /var/log/mysql/mysql.log
  exit 1
}

# Configure root user for password authentication
echo "Configuring root user..."
mysql -uroot --socket=/run/mysqld/mysqld.sock -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$MYSQL_ROOT_PASSWORD'; FLUSH PRIVILEGES;" || {
  echo "Failed to configure root user"
  cat /var/log/mysql/mysql.log
  exit 1
}

# Create driveme database if it doesn't exist
echo "Creating database if needed..."
mysql -uroot -p$MYSQL_ROOT_PASSWORD --socket=/run/mysqld/mysqld.sock -e "CREATE DATABASE IF NOT EXISTS $MYSQL_DATABASE;" || {
  echo "Failed to create database"
  cat /var/log/mysql/mysql.log
  exit 1
}

# Debug: Verify database creation
echo "Listing databases..."
mysql -uroot -p$MYSQL_ROOT_PASSWORD --socket=/run/mysqld/mysqld.sock -e "SHOW DATABASES;"

# Start Spring Boot app
echo "Starting Spring Boot app..."
exec java -jar /app/app.jar
