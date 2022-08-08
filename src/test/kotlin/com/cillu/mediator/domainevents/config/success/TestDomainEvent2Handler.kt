package com.cillu.mediator.domainevents.config.success


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent2
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService
import mu.KotlinLogging

@DomainEventHandler
class TestDomainEvent2Handler(var testService: ITestService, var anotherService: IAnotherService) : IDomainEventHandler<TestDomainEvent2>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( event: TestDomainEvent2) {
        logger.info("Executing TestDomainEvent2 with ${event.item.name}")
        testService.sayhello()
        anotherService.sayhello()
        logger.info("Executed TestDomainEvent2 with ${event.item.name}")
    }
}

