package com.cillu.mediator.servicebus

class MessageBrokerFactory private constructor(){

    companion object{
        fun getMessageBroker(mediatorConfiguration: com.cillu.mediator.configuration.Mediator): IServiceBus {
            return when(mediatorConfiguration.mediatorServicebus.name) {
                "azure-service-bus" -> throw NotImplementedError()
                "rabbitmq" -> RabbitMQServiceBus(mediatorConfiguration.mediatorServicebus.rabbitMq.connectionUrl,
                    mediatorConfiguration.mediatorServicebus.rabbitMq.exchangeName,
                    mediatorConfiguration.mediatorServicebus.rabbitMq.queueName, mediatorConfiguration.mediatorServicebus.rabbitMq.exchangeType,
                    mediatorConfiguration.mediatorServicebus.rabbitMq.consumerRetryLimit)
                else -> throw NotImplementedError()
            }
        }
    }

}