package com.cillu.mediator

import com.cillu.mediator.configuration.Mediator
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.messagebrokers.MessageBrokerFactory
import com.cillu.mediator.services.*
import com.sksamuel.hoplite.ConfigLoader


open class TestBase()
{
    internal var INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ = "/mediatork-rabbitmq.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SNS = "/mediatork-sns.yml"

    internal val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"

    fun getMediatorK(configFile: String, registerService: Boolean = true): IMediator {
        val mediatorConfiguration = ConfigLoader().loadConfigOrThrow<Mediator>(configFile)
        val serviceBus = MessageBrokerFactory.getMessageBroker(mediatorConfiguration);
        if (registerService) {
            val servicesRegistry = ServiceRegistry()
            servicesRegistry.register(MemoryRepository::class.java, MemoryRepository())
            return MediatorK.create(mediatorConfiguration.mediatorPaths, serviceBus, servicesRegistry);
        } else {
            return MediatorK.create(mediatorConfiguration.mediatorPaths, serviceBus);
        }
    }
}