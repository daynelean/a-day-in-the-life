build:
	sbt docker

run: build
	docker-compose up

stop:
	docker-compose down

accept-request:
	src/main/sh/accept.sh

decline-request:
	src/main/sh/decline.sh

badjson-request:
	src/main/sh/badjson.sh
