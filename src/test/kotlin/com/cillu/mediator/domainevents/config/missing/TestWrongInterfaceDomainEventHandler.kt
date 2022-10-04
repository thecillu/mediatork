package com.cillu.mediator.domainevents.config.missing


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.MissingService
import mu.KotlinLogging

@DomainEventHandler
class TestMissingServiceDomainEventHandler(var missingService: MissingService) : IDomainEventHandler<TestDomainEvent>, Exception() {

    private val logger = KotlinLogging.logger {}

    override fun handle( domainEvent: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${domainEvent.item.name}")
        logger.info("Executed TestDomainEvent with ${domainEvent.item.name}")
    }
}

