package com.cillu.mediator.domain

import com.cillu.mediator.Base
import com.cillu.mediator.commands.Command
import com.cillu.mediator.domainevents.DomainEvent
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.queries.Query
import org.junit.jupiter.api.Test
import java.util.*

class TestDomain() : Base() {

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