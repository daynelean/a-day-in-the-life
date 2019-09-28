package com.example.day

import cats.effect.Sync
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

case object Queue {

  type QueueUrl = String

  // TODO move the configuration outside the app
  def sqsClient[F[_]: Sync](): F[AmazonSQS] =
    Sync[F].delay({
      val endpoint: String = "http://localhost:9324"
      val region: String = "elasticmq"
      val accessKey: String = "x"
      val secretKey: String = "x"
      println("about to create client")
      val client =  AmazonSQSClientBuilder.standard.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region)).build
      println("created client")
      client
    })

  def createQueue[F[_]: Sync](sqs: AmazonSQS, queueName: String): F[QueueUrl] =
    Sync[F].delay({
      println("about to create queue")
      val url =  sqs.createQueue(queueName).getQueueUrl()
      println("created queue")
      url
    })

  def sendMessage[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl)(message: String): F[Unit] =
    Sync[F].delay({
      println("about to send message")
      val r = sqs.sendMessage(queueUrl, message)
      println("sent message")
      println(r.toString())
    })

}
