package com.cillu.mediator.integrationevents.config.single

import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import mu.KotlinLogging

@IntegrationEventHandler
class FakeIntegrationEventHandler: IIntegrationEventHandler<FakeIntegrationEvent>, Exception() {

    @Inject
    lateinit var memoryRepository: MemoryRepository

    private val logger = KotlinLogging.logger {}

    override fun handle( event: FakeIntegrationEvent) {
        logger.info("Executing FakeIntegrationEvent")
        memoryRepository.increment()
        logger.info("Executed FakeIntegrationEvent")
    }
}

