package com.cillu.mediator.domainevents.config.missing


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.services.MissingService
import mu.KotlinLogging

@DomainEventHandler
class MissingServiceDomainEventHandler() : IDomainEventHandler<TestDomainEvent>, Exception() {

    @Inject
    lateinit var missingService: MissingService

    private val logger = KotlinLogging.logger {}

    override fun handle(event: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${event.item.name}")
        logger.info("Executed TestDomainEvent with ${event.item.name}")
    }
}

