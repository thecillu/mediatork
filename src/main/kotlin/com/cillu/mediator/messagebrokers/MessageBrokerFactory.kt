package com.cillu.mediator.messagebrokers

import com.cillu.mediator.messagebrokers.aws.AwsSnsConfiguration
import com.cillu.mediator.messagebrokers.aws.AwsSnsMessageBroker
import com.cillu.mediator.messagebrokers.local.InMemoryMessageBroker
import com.cillu.mediator.messagebrokers.rabbitmq.RabbitMQConfiguration
import com.cillu.mediator.messagebrokers.rabbitmq.RabbitMQMessageBroker

class MessageBrokerFactory private constructor() {

    companion object {
        fun getMessageBroker(rabbitMQConfiguration: RabbitMQConfiguration): IMessageBroker {
            return RabbitMQMessageBroker( rabbitMQConfiguration )
        }

        fun getMessageBroker(awsSnsConfiguration: AwsSnsConfiguration): IMessageBroker {
            return AwsSnsMessageBroker( awsSnsConfiguration )
        }
    }

}