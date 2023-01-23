package com.cillu.mediator.domainevents.config.success


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.ITestService
import mu.KotlinLogging

@DomainEventHandler
class DomainEventHandler: IDomainEventHandler<TestDomainEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    @Inject
    lateinit var testService: ITestService

    override fun handle(event: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        testService.sayhello()
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

