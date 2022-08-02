package com.cillu.mediator.domainevents

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.domainevents.config.success.TestDomainEvent2Handler
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler2
import com.cillu.mediator.domainevents.config.success.TestDomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import org.junit.jupiter.api.Test

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
}