# sh scripts/run-on-docker.sh
# sh scripts/wait-starting-server.sh
#if [ $? -ne 0 ]; then
#  # server is not running
#  docker-compose stop
#  exit 1
#fi

docker run --rm --network container:kottage -v "$PWD":/home/gradle/project -w /home/gradle/project gradle gradle karate:test --no-daemon
docker-compose stop
