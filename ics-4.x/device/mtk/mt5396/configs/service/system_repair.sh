
#!/system/bin/sh

echo "start system repair ......"

cd  /data

ls  /data  |grep  -v  "app"  |grep  -v  "app-private"  |xargs  rm  -rf 

echo "finish system repair ......"

echo "Rebooting ......"

reboot



