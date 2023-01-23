package com.cillu.mediator.integrationevents.config.noservice

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import mu.KotlinLogging

@IntegrationEventHandler
class NoServiceIntegrationEventHandler: IIntegrationEventHandler<TestIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle(event: TestIntegrationEvent) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

