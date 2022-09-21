package com.cillu.mediator.messagebrokers

class MessageBrokerFactory private constructor(){

    companion object{
        fun getMessageBroker(mediatorConfiguration: com.cillu.mediator.configuration.Mediator): IMessageBroker {
            return when(mediatorConfiguration.mediatorServicebus.name) {
                "local-service-bus" -> LocalMessageBroker()
                "azure-service-bus" -> throw NotImplementedError()
                "awsSns" -> AwsSnsMessageBroker(
                    mediatorConfiguration.mediatorServicebus.awsSns!!.region,
                    mediatorConfiguration.mediatorServicebus.awsSns!!.topicName,
                    mediatorConfiguration.mediatorServicebus.awsSns!!.queueName)
                "rabbitMq" -> RabbitMQMessageBroker(
                    mediatorConfiguration.mediatorServicebus.rabbitMq!!.connectionUrl,
                    mediatorConfiguration.mediatorServicebus.rabbitMq!!.exchangeName,
                    mediatorConfiguration.mediatorServicebus.rabbitMq!!.queueName, mediatorConfiguration.mediatorServicebus.rabbitMq!!.exchangeType,
                    mediatorConfiguration.mediatorServicebus.rabbitMq!!.consumerRetryLimit)
                else -> throw NotImplementedError()
            }
        }
    }

}