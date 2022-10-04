package com.cillu.mediator.messagebrokers

import com.cillu.mediator.messagebrokers.aws.AwsSnsMessageBroker
import com.cillu.mediator.messagebrokers.local.LocalMessageBroker
import com.cillu.mediator.messagebrokers.rabbitmq.RabbitMQMessageBroker

class MessageBrokerFactory private constructor() {

    companion object {
        fun getMessageBroker(mediatorConfiguration: com.cillu.mediator.configuration.MediatorConfig): IMessageBroker {
            return when (mediatorConfiguration.mediator.messageBroker.name) {
                "localMessageBroker" -> LocalMessageBroker()
                "azureServiceBus" -> throw NotImplementedError()
                "awsSns" -> AwsSnsMessageBroker(
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.region,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.topicName,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.queueName,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.consumer.maxConsumers,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.consumer.maxMessages,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.consumer.waitTimeSeconds,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.consumer.retryAfterSeconds,
                    mediatorConfiguration.mediator.messageBroker.awsSns!!.consumer.processTimeoutSeconds.toString()
                )
                "rabbitMq" -> RabbitMQMessageBroker(
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.host,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.port,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.username,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.password,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.useSslProtocol,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.exchangeName,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.queueName,
                    mediatorConfiguration.mediator.messageBroker.rabbitMq!!.consumerRetryLimit
                )
                else -> throw NotImplementedError()
            }
        }
    }

}