package com.cillu.mediator.integrationevents.config.missing

import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.MissingService
import mu.KotlinLogging

@IntegrationEventHandler
class TestMissingServiceIntegrationEventHandler: IIntegrationEventHandler<TestIntegrationEvent>, Exception() {

    @Inject
    lateinit var missingService: MissingService

    private val logger = KotlinLogging.logger {}

    override fun handle( testIntegrationEvent: TestIntegrationEvent) {
        logger.info("Executing TestDomainEvent with ${testIntegrationEvent.item.name}")
        logger.info("Executed TestDomainEvent with ${testIntegrationEvent.item.name}")
    }
}

