package com.cillu.mediator.messagebrokers

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

class AwsSqsConsumer: Runnable {

    private var logger = KotlinLogging.logger {}
    var sqsClient: SqsClient
    var mediator: IMediator
    var queueUrlVal: String
    var maxMessages: Int
    var waitTimeSeconds: Int

    constructor(mediator: IMediator, sqsClient: SqsClient, queueUrlVal:String, maxMessages: Int, waitTimeSeconds: Int){
        this.mediator = mediator;
        this.sqsClient = sqsClient
        this.queueUrlVal = queueUrlVal
        this.maxMessages = maxMessages
        this.waitTimeSeconds = waitTimeSeconds
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
                        val snsNotification = Gson().fromJson(message.body, SnsNotification::class.java)
                        val integrationEventName = snsNotification.Subject
                        val payload = snsNotification.Message
                        logger.info("Received ${integrationEventName} [messageId=${message.messageId}]")
                        launch {
                            logger.info("Consuming ${integrationEventName} [messageId=${message.messageId}]")
                            process(mediator, integrationEventName, payload)
                            logger.info("Consumed ${integrationEventName} [messageId=${message.messageId}]")
                            logger.info("Deleting Message ${message.messageId}")
                            val deleteMessageRequest = DeleteMessageRequest {
                                queueUrl = queueUrlVal
                                receiptHandle = message.receiptHandle
                            }
                            sqsClient.deleteMessage(deleteMessageRequest)
                            logger.info("Deleted Message ${message.messageId}")
                        }
                    }
                }
            } catch (e: Throwable) {
                logger.error(e.message)
            }
        }
        logger.info("Consumer Launched for Queue $queueUrlVal")
    }

    private suspend fun process(mediator: IMediator, integrationEventName: String, message: String) {
        logger.info("Processing ${integrationEventName}")
        var integrationEvent = Gson().fromJson(message, Class.forName(integrationEventName))
        mediator.process(integrationEvent as IntegrationEvent)
        logger.info("Processed ${integrationEventName}")
    }
}