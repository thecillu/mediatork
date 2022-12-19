package com.cillu.mediator.integrationevents.config.wrongconstructor

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import mu.KotlinLogging

@IntegrationEventHandler
class TestIntegrationEventHandler(var wrongParam: Any) : IIntegrationEventHandler<TestIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( testIntegrationEvent: TestIntegrationEvent) {
        logger.info("Executing TestDomainEvent with ${testIntegrationEvent.item.name}")
        logger.info("Executed TestDomainEvent with ${testIntegrationEvent.item.name}")
    }
}

