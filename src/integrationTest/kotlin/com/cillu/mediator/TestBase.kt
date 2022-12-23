package com.cillu.mediator

import com.cillu.mediator.configuration.Mediator
import com.cillu.mediator.configuration.MediatorConfig
import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.messagebrokers.MessageBrokerFactory
import com.cillu.mediator.messagebrokers.aws.AwsSnsConfiguration
import com.cillu.mediator.messagebrokers.rabbitmq.RabbitMQConfiguration
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.services.*
import com.sksamuel.hoplite.ConfigLoader


open class TestBase() {
    internal var INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ = "/mediatork-rabbitmq.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SNS_MULTIPLE = "/mediatork-sns-mutiple.yml"
    internal val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"
    internal val FAKE_INTEGRATION2EVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegration2Event"

    fun getMediatorKwithRabbitMQ(configFile: String, registerService: Boolean = true): IMediator {
        val mediatorConfig = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        var rabbitMQConfiguration = RabbitMQConfiguration(
            host = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.host,
            port = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.port,
            username =  mediatorConfig.mediator.messageBroker!!.rabbitMq!!.username,
            password = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.password,
            //mediatorConfig.mediator.messageBroker!!.rabbitMq!!.useSslProtocol,
            exchangeName = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.exchangeName,
            queueName = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.queueName
            //mediatorConfig.mediator.messageBroker!!.rabbitMq!!.consumerRetryLimit
            )
        var messageBroker = MessageBrokerFactory.getMessageBroker(rabbitMQConfiguration)
        if (registerService) {
            var serviceRegistry = ServiceRegistry()
            serviceRegistry.register(MemoryRepository::class.java, MemoryRepository())
            return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, serviceRegistry, messageBroker)
        }
        return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, messageBroker)
    }

    fun getMediatorKwithAwsSns(configFile: String, registerService: Boolean = true): IMediator {
        val mediatorConfig = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        var awsSnsConfiguration = AwsSnsConfiguration(
            region = mediatorConfig.mediator.messageBroker!!.awsSns!!.region,
            topicName = mediatorConfig.mediator.messageBroker!!.awsSns!!.topicName,
            queueName =  mediatorConfig.mediator.messageBroker!!.awsSns!!.queueName
        )
        var messageBroker = MessageBrokerFactory.getMessageBroker(awsSnsConfiguration)
        if (registerService) {
            var serviceRegistry = ServiceRegistry()
            serviceRegistry.register(MemoryRepository::class.java, MemoryRepository())
            return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, serviceRegistry, messageBroker)
        }
        return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, messageBroker)
    }

}