package com.cillu.mediator.messagebrokers

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.*
import aws.sdk.kotlin.services.sns.model.MessageAttributeValue
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
    private var queueName: String
    private var topicName: String
    private var region: String
    private lateinit var queueUrlVal: String
    private lateinit var deadLetterQueueUrlVal: String
    private lateinit var topicArnVal: String
    private lateinit var queueArnVal: String
    private lateinit var deadLetterQueueArnVal: String
    private lateinit var subscriptionArnVal: String
    private var snsClient: SnsClient
    private var sqsClient: SqsClient
    private val maxConsumers: Int
    private val maxMessages: Int
    private val waitTimeSeconds: Int
    private var retryAfterSeconds: Int
    private var workerPool: ExecutorService

    internal constructor(
        regionVal: String,
        topicName: String,
        queueName: String,
        maxConsumers: Int,
        maxMessages: Int,
        waitTimeSeconds: Int,
        retryAfterSeconds: Int
    ) {
        this.queueName = queueName
        this.topicName = topicName
        this.region = regionVal
        this.retryAfterSeconds = retryAfterSeconds
        this.maxConsumers = maxConsumers
        this.maxMessages = maxMessages
        this.waitTimeSeconds = waitTimeSeconds
        workerPool = Executors.newFixedThreadPool(maxConsumers)
        runBlocking {
            snsClient = SnsClient { region = regionVal }
            sqsClient = SqsClient { region = regionVal }
            createTopicAndQueue(topicName, queueName)
        }
    }

    fun createTopicAndQueue(
        topicNameVal: String,
        queueNameVal: String
    ) {
        createTopic(topicNameVal)
        createSourceQueue(queueNameVal)
        createDeadLetterQueue(queueNameVal)
        subscribe(queueNameVal)
    }

    private fun subscribe(queueNameVal: String) {
        runBlocking {
            val subscribeRequest = SubscribeRequest {
                protocol = "sqs"
                endpoint = queueArnVal
                topicArn = topicArnVal
                returnSubscriptionArn = true
            }
            logger.info("Subscribing Queue $queueNameVal to the Topic $topicArnVal")
            val result = snsClient.subscribe(subscribeRequest)
            subscriptionArnVal = result.subscriptionArn!!
            logger.info("Subscribed Queue $queueNameVal to the Topic $topicArnVal")
            logger.info("Setting SQS Policies...")
            val policyDocument = AwsSqsPolicy.getPolicyDocument(queueArnVal, topicArnVal);
            val redrivePolicyDocument = AwsSqsPolicy.getRedrivePolicy(deadLetterQueueArnVal, consumerRetryLimit);
            val setQueueAttributesRequest = SetQueueAttributesRequest {
                queueUrl = queueUrlVal
                attributes = mapOf(
                    QueueAttributeName.Policy.value to policyDocument,
                    QueueAttributeName.RedrivePolicy.value to redrivePolicyDocument
                )
                QueueAttributeName.RedriveAllowPolicy
            }
            sqsClient.setQueueAttributes(setQueueAttributesRequest)
            logger.info("Set SQS Policies")
        }
    }

    private fun createSourceQueue(queueNameVal: String) {
        logger.info("Creating Queue $queueNameVal on Region $region")
        runBlocking {
            val createQueueRequest = CreateQueueRequest {
                queueName = queueNameVal
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
    }

    private fun createDeadLetterQueue(queueNameVal: String) {
        val deadLetterQueueVal = queueNameVal.plus("_dlq")
        logger.info("Creating DeadLetter Queue $deadLetterQueueVal on Region $region")
        runBlocking {
            val createQueueRequest = CreateQueueRequest {
                queueName = deadLetterQueueVal
            }
            sqsClient.createQueue(createQueueRequest)
        }
        runBlocking {
            val getQueueUrlRequest = GetQueueUrlRequest {
                queueName = deadLetterQueueVal
            }
            deadLetterQueueUrlVal = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl.toString()
        }
        runBlocking {
            var getQueueAttributesRequest = GetQueueAttributesRequest {
                queueUrl = deadLetterQueueUrlVal
                attributeNames = listOf(QueueAttributeName.QueueArn)
            }
            deadLetterQueueArnVal =
                sqsClient.getQueueAttributes(getQueueAttributesRequest).attributes?.get("QueueArn")!!
        }
        logger.info("Created DeadLetter Queue $deadLetterQueueVal on Region $region")
    }


    private fun createTopic(topicNameVal: String) {
        logger.info("Creating Topic $topicNameVal on Region $region")
        runBlocking {
            val createTopicRequest = CreateTopicRequest {
                name = topicNameVal
            }
            val result = snsClient.createTopic(createTopicRequest)
            topicArnVal = result.topicArn.toString()
        }
        logger.info("Created Topic $topicNameVal on Region $region")
    }

    override fun bind(integrationEventName: String) {
        logger.info("Binding routingKey = $integrationEventName for Queue $queueName and sns subscription $subscriptionArnVal")
        val policy = AwsSnsPolicy.getFilterPolicy(listOf(integrationEventName))
        runBlocking {
            val setSubscriptionAttributesRequest = SetSubscriptionAttributesRequest {
                attributeName = "FilterPolicy"
                attributeValue = policy
                subscriptionArn = subscriptionArnVal
            }
            snsClient.setSubscriptionAttributes(setSubscriptionAttributesRequest)
        }
        logger.info("Bound routingKey = $integrationEventName for Queue $queueName and sns subscription $subscriptionArnVal")
    }

    override fun consume(mediator: IMediator) {
        logger.info("Launching messages Consumers for Queue $queueUrlVal")
        for (i in 1..maxConsumers) {
            workerPool.submit(
                AwsSqsConsumer(
                    mediator,
                    sqsClient,
                    queueUrlVal,
                    maxMessages,
                    waitTimeSeconds,
                    retryAfterSeconds
                )
            )
        }
        logger.info("Consumer Launched for Queue $queueUrlVal")
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        runBlocking {
            logger.info("Publishing  ${integrationEvent::class.java.name}  on $topicName")
            var json = Gson().toJson(integrationEvent)
            val attributes: MutableMap<String, MessageAttributeValue> = mutableMapOf()
            attributes["event"] = MessageAttributeValue {
                dataType = "String"
                stringValue = integrationEvent::class.java.name
            }
            var publishRequest = PublishRequest {
                topicArn = topicArnVal
                subject = integrationEvent::class.java.name
                message = json
                messageAttributes = attributes
            }
            val result: PublishResponse
            result = snsClient.publish(publishRequest)
            logger.info("Published  ${integrationEvent::class.java.name}  on $topicName [messageId:${result.messageId}]")
        }
    }
}

data class SnsNotification(val Type: String, val MessageId: String, val Subject: String, val Message: String)