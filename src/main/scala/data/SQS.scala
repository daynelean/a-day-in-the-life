package com.example.day.data

import cats.effect.Sync
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}
import com.example.day.config.SqsConfig

import scala.collection.JavaConverters._
import scala.collection.immutable.List


case object SQS {

  // TODO newtype
  type QueueUrl = String
  type ReceiptHandle = String
  type Message = com.amazonaws.services.sqs.model.Message

  def sqsClient[F[_]: Sync](conf: SqsConfig): F[AmazonSQS] =
    Sync[F].delay({
      AmazonSQSClientBuilder.standard.withCredentials(
        new AWSStaticCredentialsProvider(new BasicAWSCredentials(conf.accessKey, conf.secretKey))).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(conf.endpoint, conf.region)).build
    })

  def sendMessage[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl)(message: String): F[Unit] =
    Sync[F].delay({
      sqs.sendMessage(queueUrl, message)
      ()
    })

  def fetchMessages[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl): F[List[Message]] =
    Sync[F].delay({
      sqs.receiveMessage(queueUrl).getMessages.asScala.toList
    })

  def deleteMessage[F[_]: Sync](sqs: AmazonSQS, queueUrl: QueueUrl)(receiptHandle: ReceiptHandle): F[Unit] =
    Sync[F].delay({
      sqs.deleteMessage(queueUrl, receiptHandle)
      ()
    })

  def createQueue[F[_]: Sync](sqs: AmazonSQS, queueName: String): F[QueueUrl] =
    Sync[F].delay({
      sqs.createQueue(queueName).getQueueUrl()
    })


}
