package com.cillu.mediator.domainevents.config.success


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.ITestService
import mu.KotlinLogging

@DomainEventHandler
class TestDomainEventHandler(var testService: ITestService) : IDomainEventHandler<TestDomainEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( domainEvent: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${domainEvent.item.name}")
        testService.sayhello()
        logger.info("Executed TestDomainEvent with ${domainEvent.item.name}")
    }
}

