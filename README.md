# A day in the life

## Requirements

- sbt
- docker-compose
- make

## Instructions

To run:

`make run`

To make some test requests:

`make accept-request`
`make decline-request`
`make badjson-request`

To tear down:

`make stop`

## Limitations

These could use some more work but I've run out of time.

- Needs more unit tests. And some integration tests.
- Needs logging.
- Neither app handles SQS not being available when they start up.
  The workaround is the `restart: always` lines in docker-compose.yml.
  Probably need something like `cats-retry`.
- The build process only produces a single artifact. It's passed
  a different command in the docker-compose file to select
  a main class.
- Config comes from env vars. If these are missing it throws an
  exception. Failing hard and fast is the correct behaviour
  but nicer messaging would be nice.
- The build process produces a docker image tagged with both
  `latest` and the git hash but docker-compose is only using
  the `latest` tag.
