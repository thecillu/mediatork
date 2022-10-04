package com.cillu.mediator.integrationevents.config

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.PippoIntegrationEvent
import com.cillu.mediator.integrationevents.domain.PlutoIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import mu.KotlinLogging

@IntegrationEventHandler
class PlutoIntegrationEventHandler(val memoryRepository: MemoryRepository) : IIntegrationEventHandler<PlutoIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( event: PlutoIntegrationEvent) {
        logger.info("Executing PlutoIntegrationEvent")
        memoryRepository.increment()
        logger.info("Executed PlutoIntegrationEvent")
    }
}

