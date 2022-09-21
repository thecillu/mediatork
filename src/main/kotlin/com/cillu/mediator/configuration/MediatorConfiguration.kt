package com.cillu.mediator.configuration

data class Mediator(
    val mediatorPaths: List<String>,
    val mediatorServicebus: ServiceBus
)

data class ServiceBus(
    val name: String,
    val rabbitMq: RabbitMq?,
    val awsSns: AwsSns?
)

data class RabbitMq(
    val connectionUrl: String,
    val exchangeName: String,
    val queueName: String,
    val exchangeType: String,
    val consumerRetryLimit: Int
)

data class AwsSns(
    val region: String,
    val topicName: String,
    val queueName: String
)




