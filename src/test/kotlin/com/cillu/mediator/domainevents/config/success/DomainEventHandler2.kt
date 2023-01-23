package com.cillu.mediator.domainevents.config.success


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import mu.KotlinLogging

@DomainEventHandler
class DomainEventHandler2 : IDomainEventHandler<TestDomainEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle(event: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

