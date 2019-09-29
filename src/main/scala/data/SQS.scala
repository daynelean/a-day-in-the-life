package com.example.day.data

import cats.effect.Sync
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

import scala.collection.JavaConverters._
import scala.collection.immutable.List


case object SQS {

  // TODO newtype
  type QueueUrl = String
  type ReceiptHandle = String
  type Message = com.amazonaws.services.sqs.model.Message

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

  def fetchMessages[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl): F[List[Message]] =
    Sync[F].delay({
      println("about to receive messages")
      val r = sqs.receiveMessage(queueUrl).getMessages.asScala.toList
      println(s"received ${r.size} messages")
      r
    })

  def deleteMessage[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl)(receiptHandle: ReceiptHandle): F[Unit] =
    Sync[F].delay({
      println("about to delete message")
      sqs.deleteMessage(queueUrl, receiptHandle)
      println("deleted message")
    })



}
