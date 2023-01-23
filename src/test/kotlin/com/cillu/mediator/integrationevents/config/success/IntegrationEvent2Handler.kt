package com.cillu.mediator.integrationevents.config.success

import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent2
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService
import mu.KotlinLogging

@IntegrationEventHandler
class IntegrationEvent2Handler: IIntegrationEventHandler<TestIntegrationEvent2>, Exception() {

    @Inject
    lateinit var testService: ITestService

    @Inject
    lateinit var anotherService: IAnotherService

    private val logger = KotlinLogging.logger {}

    override fun handle(event: TestIntegrationEvent2) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        testService.sayhello()
        anotherService.sayhello()
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

