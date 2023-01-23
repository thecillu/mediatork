package com.cillu.mediator.integrationevents.config.exception

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import mu.KotlinLogging

@IntegrationEventHandler
class WrongInterfaceIntegrationEventHandler: Exception() {

    private val logger = KotlinLogging.logger {}

     fun handle( testIntegrationEvent: TestIntegrationEvent) {
        logger.info("Executing TestDomainEvent with ${testIntegrationEvent.item.name}")
        logger.info("Executed TestDomainEvent with ${testIntegrationEvent.item.name}")
    }
}

