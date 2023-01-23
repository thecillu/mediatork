package com.cillu.mediator.integrationevents.config.success

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import mu.KotlinLogging

@IntegrationEventHandler
class FakeIntegrationEventHandler : IIntegrationEventHandler<FakeIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle(event: FakeIntegrationEvent) {
        logger.info("Executing FakeIntegrationEvent")
        logger.info("Executed FakeIntegrationEvent")
    }
}

