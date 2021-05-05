# kottage

## Development

```sh
# test
$ ./gradlew test
$ sh ./scripts/run-e2e-test-on-docker.sh

# run on local machine http://0.0.0.0:8080
$ ./gradlew run

# run on docker container http://0.0.0.0:8080
$ sh scripts run-on-docker.sh
```

### auto-reload

use [aut-reload feature of ktor](https://ktor.io/docs/auto-reload.html).

```sh
$ ./gradlew -t build

# open another terminal
$ ./gradlew run

# reload browser when you change some files 
```
