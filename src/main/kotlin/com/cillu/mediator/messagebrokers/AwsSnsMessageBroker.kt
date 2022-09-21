package com.cillu.mediator.messagebrokers

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreateTopicRequest
import aws.sdk.kotlin.services.sns.model.PublishRequest
import aws.sdk.kotlin.services.sns.model.PublishResponse
import aws.sdk.kotlin.services.sns.model.SubscribeRequest
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.*
import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AwsSnsMessageBroker : IMessageBroker {
    private var consumerRetryLimit: Int = 10
    private var logger = KotlinLogging.logger {}
    private lateinit var queueArnVal: String
    private var queueName: String
    private var topicName: String
    private var region: String
    private lateinit var queueUrlVal: String
    private lateinit var topicArnVal: String
    private var snsClient: SnsClient
    private var sqsClient: SqsClient
    val maxConsumers: Int = 20
    val maxMessages: Int = 10
    val waitTimeSeconds: Int = 20000
    val workerPool: ExecutorService = Executors.newFixedThreadPool(maxConsumers)

    internal constructor(region: String, topicName: String, queueName: String) {
        this.queueName = queueName
        this.topicName = topicName
        this.region = region
        runBlocking {
            snsClient = SnsClient.fromEnvironment {
                credentialsProvider = ProfileCredentialsProvider(region = region)
            }
            sqsClient = SqsClient.fromEnvironment {
                credentialsProvider = ProfileCredentialsProvider(region = region)
            }
            createTopicAndQueue(topicName, queueName, region)
        }

    }

    suspend fun createTopicAndQueue(
        topicNameVal: String,
        queueNameVal: String,
        region: String
    ) {
        val createQueueRequest = CreateQueueRequest {
            queueName = queueNameVal
        }
        logger.info("Creating Topic $topicNameVal on Region $region")
        runBlocking {
            val createTopicRequest = CreateTopicRequest {
                name = topicNameVal
            }
            val result = snsClient.createTopic(createTopicRequest)
            topicArnVal = result.topicArn.toString()
        }
        logger.info("Created Topic $topicNameVal on Region $region")
        logger.info("Creating Queue $queueNameVal on Region $region")
        runBlocking {
            sqsClient = SqsClient.fromEnvironment {
                credentialsProvider = ProfileCredentialsProvider(region = region)
            }
            sqsClient.createQueue(createQueueRequest)
        }
        runBlocking {
            val getQueueUrlRequest = GetQueueUrlRequest {
                queueName = queueNameVal
            }
            queueUrlVal = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl.toString()
        }
        runBlocking {
            var getQueueAttributesRequest = GetQueueAttributesRequest {
                queueUrl = queueUrlVal
                attributeNames = listOf(QueueAttributeName.QueueArn)
            }
            queueArnVal = sqsClient.getQueueAttributes(getQueueAttributesRequest).attributes?.get("QueueArn")!!
        }
        logger.info("Created Queue $queueNameVal on Region $region")
        val subscribeRequest = SubscribeRequest {
            protocol = "sqs"
            endpoint = queueArnVal
            topicArn = topicArnVal
            returnSubscriptionArn = true
        }
        runBlocking {
            logger.info("Subscribing Queue $queueNameVal to the Topic $topicArnVal")
            val result = snsClient.subscribe(subscribeRequest)
            logger.info("Subscribed Queue $queueNameVal to the Topic $topicArnVal")
            logger.info("Setting SQS Access Policy for SNS")
            val policyDocument = AwsSqsPolicy.getPolicyDocument(queueArnVal, topicArnVal);
            val setQueueAttributesRequest = SetQueueAttributesRequest {
                queueUrl = queueUrlVal
                attributes = mapOf(QueueAttributeName.Policy.value to policyDocument)
            }
            sqsClient.setQueueAttributes( setQueueAttributesRequest)
            logger.info("Set SQS Access Policy for SNS")
        }
    }

    override fun bind(integrationEventName: String) {
        /*logger.info("Binding routingKey = $integrationEventName on Queue $queueName and Exchange $exchangeName")
         channel.queueBind(queueName, exchangeName, integrationEventName)
         logger.info("Binding routingKey = $integrationEventName on Queue $dlqQueueName and Exchange $dlqExchangeName")
         channel.queueBind(dlqQueueName, dlqExchangeName, integrationEventName)*/
    }

    override fun consume(mediator: IMediator) {
        logger.info("Launching messages Consumers for Queue $queueUrlVal")
        for (i in 1..maxConsumers) {
            workerPool.submit(AwsSqsConsumer(mediator, sqsClient, queueUrlVal, maxMessages, waitTimeSeconds))
        }
        logger.info("Consumer Launched for Queue $queueUrlVal")
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        logger.info("Publishing  ${integrationEvent::class.java.name}  on $topicName")
        var json = Gson().toJson(integrationEvent)
        var publishRequest = PublishRequest {
            topicArn = topicArnVal
            subject = integrationEvent::class.java.name
            message = json
        }
        val result: PublishResponse
        runBlocking {
            result = snsClient.publish(publishRequest)
        }
        logger.info("Published  ${integrationEvent::class.java.name}  on $topicName [messageId:${result.messageId}]")
    }
}

data class SnsNotification(val Type: String, val MessageId: String, val Subject: String, val Message: String)