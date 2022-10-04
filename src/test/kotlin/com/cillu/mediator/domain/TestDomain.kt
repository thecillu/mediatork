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

class TestDomain() : TestBase() {

    @Test
    fun testCommend() {
        val command = Command()
        val id = command.id
        assert(!id.toString().isNullOrEmpty())
    }

    @Test
    fun testQuery() {
        val query = Query()
        val id = query.id
        assert(!id.toString().isNullOrEmpty())
    }

    @Test
    fun testIntegrationEvent() {
        val integrationEvent = IntegrationEvent()
        val id = integrationEvent.id
        assert(!id.toString().isNullOrEmpty())
    }

    @Test
    fun testDomainEvent() {
        val domainEvent = DomainEvent()
        val id = domainEvent.id
        assert(!id.toString().isNullOrEmpty())
    }


}