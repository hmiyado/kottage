# DOCKER_BUILDKIT=1 is need to use karate.Dockerfile.dockerignore.
# https://docs.docker.com/engine/reference/commandline/build/#use-a-dockerignore-file
DOCKER_BUILDKIT=1 docker build -t karate -f karate.Dockerfile .
docker run --rm --network container:kottage karate gradle karate:test
