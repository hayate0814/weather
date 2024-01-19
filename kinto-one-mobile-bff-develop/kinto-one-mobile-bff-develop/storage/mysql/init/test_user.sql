GRANT ALL PRIVILEGES ON *.* TO developer@'%';
ALTER USER 'developer'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
