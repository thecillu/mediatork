package com.cillu.mediator

import com.cillu.mediator.configuration.MediatorConfig
import com.cillu.mediator.integrationevents.services.IRepository
import com.cillu.mediator.integrationevents.services.MemoryRepository
import com.cillu.mediator.messagebrokers.MessageBrokerFactory
import com.cillu.mediator.messagebrokers.aws.AwsSnsConfiguration
import com.cillu.mediator.messagebrokers.rabbitmq.RabbitMQConfiguration
import com.cillu.mediator.registry.ServiceRegistry
import com.sksamuel.hoplite.ConfigLoader
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName


open class IntegrationBase() {
    internal var INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ = "/mediatork-rabbitmq.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SNS_MULTIPLE = "/mediatork-sns-mutiple.yml"
    internal val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"
    internal val FAKE_INTEGRATION2EVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegration2Event"

    lateinit var localstack : LocalStackContainer
    lateinit var rabbitMq: GenericContainer<*>


    fun getMediatorKwithRabbitMQ(configFile: String, registerService: Boolean = true): IMediator {
        val mediatorConfig = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        rabbitMq = GenericContainer("rabbitmq:management").withExposedPorts(mediatorConfig.mediator.messageBroker!!.rabbitMq!!.port, 15672)
        rabbitMq.start()
        val rabbitMQConfiguration = RabbitMQConfiguration(
            host = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.host,
            port = rabbitMq.getMappedPort(mediatorConfig.mediator.messageBroker!!.rabbitMq!!.port),
            username =  mediatorConfig.mediator.messageBroker!!.rabbitMq!!.username,
            password = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.password,
            useSslProtocol =  mediatorConfig.mediator.messageBroker!!.rabbitMq!!.useSslProtocol,
            exchangeName = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.exchangeName,
            queueName = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.queueName,
            consumerRetryLimit = mediatorConfig.mediator.messageBroker!!.rabbitMq!!.consumerRetryLimit
            )
        val messageBroker = MessageBrokerFactory.getMessageBroker(rabbitMQConfiguration)
        if (registerService) {
            var serviceRegistry = ServiceRegistry()
            serviceRegistry.register(IRepository::class.java, MemoryRepository())
            return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, serviceRegistry, messageBroker)
        }
        return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, messageBroker = messageBroker)
    }

    fun getMediatorKwithAwsSns(configFile: String, registerService: Boolean = true): IMediator {

        var localstackImage = DockerImageName.parse("localstack/localstack:0.11.3")
        localstack = LocalStackContainer(localstackImage)
            .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)

        localstack.start()

        val mediatorConfig = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        var awsSnsConfiguration = AwsSnsConfiguration(
            region = localstack.region,
            topicName = mediatorConfig.mediator.messageBroker!!.awsSns!!.topicName,
            queueName =  mediatorConfig.mediator.messageBroker.awsSns!!.queueName,
            snsEndpointUrl =  localstack.getEndpointOverride(LocalStackContainer.Service.SNS).toString(),
            sqsEndpointUrl =  localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
            accessKeyId = localstack.accessKey,
            secretAccessKey  = localstack.secretKey
        )
        var messageBroker = MessageBrokerFactory.getMessageBroker(awsSnsConfiguration)
        if (registerService) {
            var serviceRegistry = ServiceRegistry()
            serviceRegistry.register(IRepository::class.java, MemoryRepository())
            return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, serviceRegistry, messageBroker)
        }
        return MediatorFactory.getDefaultMediator(mediatorConfig.mediator.paths, messageBroker)
    }

}