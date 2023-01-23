package com.cillu.mediator.domainevents.config.exception


import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import mu.KotlinLogging

@DomainEventHandler
class WrongInterfaceDomainEventHandler() :  Exception() {

    private val logger = KotlinLogging.logger {}

    fun handle( domainEvent: TestDomainEvent) {
        logger.info("Executing TestDomainEvent with ${domainEvent.item.name}")
        logger.info("Executed TestDomainEvent with ${domainEvent.item.name}")
    }
}

