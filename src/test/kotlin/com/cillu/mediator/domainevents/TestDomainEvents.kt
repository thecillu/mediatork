package com.cillu.mediator.domainevents

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.config.noservice.TestNoServiceDomainEventHandler
import com.cillu.mediator.domainevents.config.success.TestDomainEvent2Handler
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler2
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.exceptions.MissingServiceException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestDomainEvents(): TestBase() {


    private val TEST_DOMAINEVENT_CLASS = "com.cillu.mediator.domainevents.domain.TestDomainEvent"
    private val TEST_DOMAINEVENT2_CLASS = "com.cillu.mediator.domainevents.domain.TestDomainEvent2"


    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getDomainEventsHandlers();
        assert( handlers.size == 2)
        val handler = handlers[TEST_DOMAINEVENT_CLASS]
        val handler2 = handlers[TEST_DOMAINEVENT2_CLASS]
        assert( handler!!.size == 2 )
        assert( handler.contains<Class<out Any>>(TestDomainEventHandler::class.java))
        assert( handler.contains<Class<out Any>>(TestDomainEventHandler2::class.java))
        assert( handler2!!.size == 1 )
        assert( handler2.contains<Class<out Any>>(TestDomainEvent2Handler::class.java))
    }


    @Test
    fun createDomainEventTest() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS)
        var testItem = TestItem.create("pippo")
        assert( testItem.getDomainEvents().size == 1 )
        assert( testItem.getDomainEvents()[0]::class.java == TestDomainEvent::class.java )
        mediatorK.raiseDomainEvents(testItem)
        assert(testItem.getDomainEvents().isEmpty())
    }

    @Test
    fun missingService() {
        assertThrows<MissingServiceException> {
            val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_MISSING)
        }
    }

    @Test
    fun successConfigNoService() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE, false)
        val handlers = mediatorK.getDomainEventsHandlers()
        assert( handlers.size == 1)
        val domainEventSet: MutableSet<Class<DomainEventHandler>> = handlers.getOrDefault(TEST_DOMAINEVENT_CLASS, mutableSetOf())
        var found = false
        domainEventSet.forEach{
            if (it == TestNoServiceDomainEventHandler::class.java) found = true
        }
        assert( found)
    }
}