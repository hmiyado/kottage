# sh scripts/run-on-docker.sh
# sh scripts/wait-starting-server.sh
#if [ $? -ne 0 ]; then
#  # server is not running
#  docker-compose stop
#  exit 1
#fi

docker build -t karate -f karate.Dockerfile .
docker run --rm --network container:kottage karate gradle karate:test
docker-compose stop
