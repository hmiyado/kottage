SECONDS=0
while [ $SECONDS -lt 10 ]
do
  curl http://127.0.0.1:8080 1>/dev/null 2>/dev/null
  if [ $? -eq 0 ]; then
    exit 0
  fi
  sleep 0.5
done
exit 1
