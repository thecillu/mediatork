package com.cillu.mediator.messagebrokers

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.ChangeMessageVisibilityRequest
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.Message
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

class AwsSqsConsumer : Runnable {
    private var logger = KotlinLogging.logger {}
    var sqsClient: SqsClient
    var mediator: IMediator
    var queueUrlVal: String
    var maxMessages: Int
    var waitTimeSeconds: Int
    var retryAfterSeconds: Int

    constructor(
        mediator: IMediator,
        sqsClient: SqsClient,
        queueUrlVal: String,
        maxMessages: Int,
        waitTimeSeconds: Int,
        retryAfterSeconds: Int
    ) {
        this.mediator = mediator;
        this.sqsClient = sqsClient
        this.queueUrlVal = queueUrlVal
        this.maxMessages = maxMessages
        this.waitTimeSeconds = waitTimeSeconds
        this.retryAfterSeconds = retryAfterSeconds
    }

    public override fun run() {
        logger.info("Listening for IntegrationEvent on queue $queueUrlVal")
        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = queueUrlVal
            maxNumberOfMessages = maxMessages
            waitTimeSeconds = waitTimeSeconds
        }
        while (true) {
            try {
                runBlocking {
                    logger.debug("Polling $queueUrlVal...")
                    val response = sqsClient.receiveMessage(receiveMessageRequest)
                    response.messages?.forEach { message ->
                        try {
                            val snsNotification = Gson().fromJson(message.body, SnsNotification::class.java)
                            val integrationEventName = snsNotification.Subject
                            val payload = snsNotification.Message
                            logger.info("Received ${integrationEventName} [messageId=${message.messageId}]")
                            launch {
                                try {
                                    logger.info("Consuming ${integrationEventName} [messageId=${message.messageId}]")
                                    process(mediator, integrationEventName, payload)
                                    logger.info("Consumed ${integrationEventName} [messageId=${message.messageId}]")
                                    deleteMessage(message)
                                } catch (e: Throwable) {
                                    releaseMessage(message)
                                }
                            }
                        } catch (e: Throwable) {
                            try {
                                releaseMessage(message)
                            } catch (e: Throwable) {
                                logger.info("Catched exception releasing event with messageId=${message.messageId}: $e.message")
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                logger.error("Catched general exception during message consuming: ${e.message}")
            }
        }
    }

    private suspend fun deleteMessage(message: Message) {
        logger.info("Deleting Message ${message.messageId}")
        val deleteMessageRequest = DeleteMessageRequest {
            queueUrl = queueUrlVal
            receiptHandle = message.receiptHandle
        }
        sqsClient.deleteMessage(deleteMessageRequest)
        logger.info("Deleted Message ${message.messageId}")
    }

    private suspend fun releaseMessage(message: Message) {
        logger.info("Releasing messageId=${message.messageId}")
        val changeMessageVisibilityRequest = ChangeMessageVisibilityRequest {
            queueUrl = queueUrlVal
            receiptHandle = message.receiptHandle
            visibilityTimeout = retryAfterSeconds
        }
        sqsClient.changeMessageVisibility(changeMessageVisibilityRequest)
        logger.info("Released messageId=${message.messageId}")
    }

    private fun process(mediator: IMediator, integrationEventName: String, message: String) {
        logger.info("Processing ${integrationEventName}")
        var integrationEvent = Gson().fromJson(message, Class.forName(integrationEventName))
        mediator.process(integrationEvent as IntegrationEvent)
        logger.info("Processed ${integrationEventName}")
    }
}