run-sqs:
	docker run -p 9324:9324 softwaremill/elasticmq

run-emailer:
	sbt "runMain com.example.day.Emailer"

run-webhook:
	sbt "runMain com.example.day.Webhook"
