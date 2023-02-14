package com.cillu.mediator.domainevents.config.success


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.domainevents.domain.TestDomainEvent2
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.*
import mu.KotlinLogging

@DomainEventHandler
class DomainEvent2Handler: IDomainEventHandler<TestDomainEvent2>, Exception() {

    @Inject
    lateinit var testService: ITestService

    @Inject
    lateinit var anotherService: IAnotherService

    private val logger = KotlinLogging.logger {}

    override fun handle( event: TestDomainEvent2) {
        logger.info("Executing TestDomainEvent2 with ${event.item.name}")
        testService.sayhello()
        anotherService.sayhello()
        logger.info("Executed TestDomainEvent2 with ${event.item.name}")
    }
}

