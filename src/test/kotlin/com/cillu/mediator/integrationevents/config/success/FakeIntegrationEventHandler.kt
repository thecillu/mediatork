package com.cillu.mediator.integrationevents.config.success

import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import mu.KotlinLogging

@IntegrationEventHandler
class FakeIntegrationEventHandler : IIntegrationEventHandler<FakeIntegrationEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( fakeIntegrationEventHandler: FakeIntegrationEvent) {
        logger.info("Executing FakeIntegrationEvent")
        logger.info("Executed FakeIntegrationEvent")
    }
}

