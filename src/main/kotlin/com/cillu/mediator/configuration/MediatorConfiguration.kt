package com.cillu.mediator.configuration

data class Mediator(
    val mediatorPaths: List<String>,
    val mediatorServicebus: ServiceBus
)

data class ServiceBus(
    val name: String,
    val rabbitMq: RabbitMq
)

data class RabbitMq(
    val connectionUrl: String,
    val exchangeName: String,
    val queueName: String,
    val exchangeType: String,
    val consumerRetryLimit: Int
)




