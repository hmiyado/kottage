sh scripts/run-on-docker.sh
sh scripts/wait-starting-server.sh
if [ $? -ne 0 ]; then
  # server is not running
  exit 1
fi

./gradlew karate:test
docker-compose stop
