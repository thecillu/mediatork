package com.cillu.mediator

import com.cillu.mediator.configuration.Mediator
import com.cillu.mediator.configuration.MediatorConfig
import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.messagebrokers.MessageBrokerFactory
import com.cillu.mediator.services.*
import com.sksamuel.hoplite.ConfigLoader


open class TestBase()
{
    internal var INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ = "/mediatork-rabbitmq.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SNS_MULTIPLE = "/mediatork-sns-mutiple.yml"
    internal val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"
    internal val FAKE_INTEGRATION2EVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegration2Event"

    fun getMediatorK(configFile: String, registerService: Boolean = true): Pair<IMediator, IMessageBroker> {
        val mediatorConfiguration = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        val messageBroker = MessageBrokerFactory.getMessageBroker(mediatorConfiguration);
        if (registerService) {
            val servicesRegistry = ServiceRegistry()
            servicesRegistry.register(MemoryRepository::class.java, MemoryRepository())
            return Pair(MediatorK.create(mediatorConfiguration.mediator.paths, messageBroker, servicesRegistry), messageBroker)
        } else {
            return Pair(MediatorK.create(mediatorConfiguration.mediator.paths, messageBroker), messageBroker)
        }
    }
}