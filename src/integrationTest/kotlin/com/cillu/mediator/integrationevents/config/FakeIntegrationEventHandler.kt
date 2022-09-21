package com.cillu.mediator.integrationevents.config

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import mu.KotlinLogging

@IntegrationEventHandler
class FakeIntegrationEventHandler(val memoryRepository: MemoryRepository) : IIntegrationEventHandler<FakeIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( event: FakeIntegrationEvent) {
        logger.info("Executing FakeIntegrationEvent")
        memoryRepository.increment()
        logger.info("Executed FakeIntegrationEvent")
    }
}

