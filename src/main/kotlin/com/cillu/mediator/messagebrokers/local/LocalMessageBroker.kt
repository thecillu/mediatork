package com.cillu.mediator.messagebrokers.local

import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.messagebrokers.IMessageBroker
import mu.KotlinLogging


class LocalMessageBroker() : IMessageBroker {

    var integrationEvents: MutableList<IntegrationEvent> = mutableListOf()
    var bindedIntegrationEvents: MutableList<String> = mutableListOf()
    var logger = KotlinLogging.logger {}
    lateinit var mediator: IMediator;

    override fun bind(integrationEventName: String) {
        bindedIntegrationEvents.add(integrationEventName)
    }

    override fun consume(mediator: IMediator) {
        this.mediator = mediator
        integrationEvents.forEach {
            val integrationEventName = it::class.java
            logger.info("Processing $integrationEventName")
            mediator.process(it)
            logger.info("Processed $integrationEventName")
        }
        integrationEvents.clear()
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        integrationEvents.add(integrationEvent)
        consume(mediator)
    }
}