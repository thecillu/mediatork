package com.cillu.mediator.integrationevents.config.multiple

import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.PippoIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import mu.KotlinLogging

@IntegrationEventHandler
class PippoIntegrationEventHandler: IIntegrationEventHandler<PippoIntegrationEvent>, Exception() {

    @Inject
    lateinit var memoryRepository: MemoryRepository

    private val logger = KotlinLogging.logger {}

    override fun handle( event: PippoIntegrationEvent) {
        logger.info("Executing PippoIntegrationEvent")
        memoryRepository.increment()
        logger.info("Executed PippoIntegrationEvent")
    }
}

