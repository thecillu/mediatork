package com.cillu.mediator.messagebrokers.aws

data class AwsSnsConfiguration(
    val region: String,
    val topicName: String,
    val queueName: String,
    val consumers: Int? = 5,
    val maxMessages: Int? = 10,
    val waitTimeSeconds: Int? = 20,
    val retryAfterSeconds: Int? = 1,
    val processTimeoutSeconds: Int? = 60
)