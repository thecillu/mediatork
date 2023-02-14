package com.cillu.mediator.testutils.configuration

data class MediatorConfig(
    val mediator: Mediator,
)

data class Mediator(
    val paths: List<String>,
    val messageBroker: MessageBroker?
)

data class MessageBroker(
    val name: String,
    val rabbitMq: RabbitMq?,
    val awsSns: AwsSns?
)

data class RabbitMq(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val useSslProtocol: Boolean,
    val exchangeName: String,
    val queueName: String,
    val consumerRetryLimit: Int
)

data class AwsSns(
    val region: String,
    val topicName: String,
    val queueName: String,
    val consumer: Consumer
)

data class Consumer(
    val maxConsumers: Int,
    val maxMessages: Int,
    val waitTimeSeconds: Int,
    val retryAfterSeconds: Int,
    val processTimeoutSeconds: Int
)



