docker build -t karate -f karate.Dockerfile .
docker run --rm --network container:kottage karate gradle karate:test
