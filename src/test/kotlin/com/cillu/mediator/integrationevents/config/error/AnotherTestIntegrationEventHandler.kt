package com.cillu.mediator.integrationevents.config.error

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import com.cillu.mediator.services.AnotherService
import com.cillu.mediator.services.IAnotherService
import mu.KotlinLogging

@IntegrationEventHandler
class AnotherTestIntegrationEventHandler(var anotherService: IAnotherService) : IIntegrationEventHandler<TestIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( event: TestIntegrationEvent) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        anotherService.sayhello()
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

