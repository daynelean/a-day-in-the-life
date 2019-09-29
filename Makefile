build:
	sbt docker

run: build
	docker-compose up

stop:
	docker-compose down
