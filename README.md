# kottage

## Development

```sh
# run on local machine http://0.0.0.0:8080
$ ./gradlew run

# run on docker container http://0.0.0.0:8080
$ sh ./scripts/run-on-docker.sh
```

### auto-reload

use [aut-reload feature of ktor](https://ktor.io/docs/auto-reload.html).

```sh
$ ./gradlew -t build

# open another terminal
$ ./gradlew run

# reload browser when you change some files 
```

### test

```sh
# all
$ ./gradlew test

# run only unit test. e2e(karate) tests are excluded.
$ ./gradlew test -x karate:test

# run e2e test. fail if local server is not running
$ ./gradlew karate:test # run on local
$ sh ./scripts/run-e2e-test-on-docker.sh # run on circle ci. this is same ./gradlew karate:test. 
```
