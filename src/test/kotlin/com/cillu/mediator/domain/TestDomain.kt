package com.cillu.mediator.domain

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.TestItem2
import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.commands.Command
import com.cillu.mediator.domainevents.DomainEvent
import com.cillu.mediator.domainevents.config.noservice.TestNoServiceDomainEventHandler
import com.cillu.mediator.domainevents.config.success.TestDomainEvent2Handler
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler2
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.exceptions.DomainEventHandlerConfigurationException
import com.cillu.mediator.exceptions.MissingServiceException
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.queries.Query
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TestDomain() : TestBase() {

    @Test
    fun testCommand() {
        val command = Command(UUID.randomUUID())
        val idEvent = command.idEvent
        assert(!idEvent.toString().isNullOrEmpty())
    }

    @Test
    fun testQuery() {
        val query = Query(UUID.randomUUID())
        val idEvent = query.idEvent
        assert(!idEvent.toString().isNullOrEmpty())
    }

    @Test
    fun testIntegrationEvent() {
        val integrationEvent = IntegrationEvent(UUID.randomUUID())
        val idEvent = integrationEvent.idEvent
        assert(!idEvent.toString().isNullOrEmpty())
    }

    @Test
    fun testDomainEvent() {
        val domainEvent = DomainEvent(UUID.randomUUID())
        val idEvent = domainEvent.idEvent
        assert(!idEvent.toString().isNullOrEmpty())
    }


}