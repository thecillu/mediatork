package com.cillu.mediator.domainevents

import com.cillu.mediator.Base
import com.cillu.mediator.Item
import com.cillu.mediator.Item2
import com.cillu.mediator.domainevents.config.success.DomainEvent2Handler
import com.cillu.mediator.domainevents.config.success.DomainEventHandler2
import com.cillu.mediator.domainevents.config.success.DomainEventHandler
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import com.cillu.mediator.exceptions.DomainEventHandlerConfigurationException
import com.cillu.mediator.exceptions.MissingComponentException
import com.cillu.mediator.exceptions.NoEmptyHandlerConstructor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestDomainEvents : Base() {


    private val TEST_DOMAINEVENT_CLASS = "com.cillu.mediator.domainevents.domain.TestDomainEvent"
    private val TEST_DOMAINEVENT2_CLASS = "com.cillu.mediator.domainevents.domain.TestDomainEvent2"


    @Test
    fun testSuccessConfig() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getDomainEventsHandlers()
        assert(handlers.size == 2)
        val handler = handlers[TEST_DOMAINEVENT_CLASS]
        val handler2 = handlers[TEST_DOMAINEVENT2_CLASS]
        assert(handler!!.size == 2)
        assert(handler2!!.size == 1)
        assert((handler.filter { it.javaClass == DomainEventHandler::class.java }.size == 1))
        assert((handler.filter { it.javaClass == DomainEventHandler2::class.java }.size == 1))
        assert((handler2.filter { it.javaClass == DomainEvent2Handler::class.java }.size == 1))
    }

    @Test
    fun testCreateDomainEventTest() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS)
        var testItem = Item.create("pippo")
        assert(testItem.getDomainEvents().size == 1)
        assert(testItem.getDomainEvents()[0]::class.java == TestDomainEvent::class.java)
        mediatorK.raiseDomainEvents(testItem)
        assert(testItem.getDomainEvents().isEmpty())
    }

    @Test
    fun testNoDomainEventTest() {
        val mediatorK = getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS)
        var testItem = Item2.create("pippo")
        assert(testItem.getDomainEvents().size == 0)
        mediatorK.raiseDomainEvents(testItem)
        assert(testItem.getDomainEvents().isEmpty())

    }

    @Test
    fun testMissingService() {
        assertThrows<MissingComponentException> {
            getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_MISSING)
        }
    }

    @Test
    fun testSuccessConfigNoService() {
        assertThrows<MissingComponentException> {
            getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE, false)
        }
    }

    @Test
    fun testMissingWrongInterface() {
        assertThrows<DomainEventHandlerConfigurationException> {
           getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_EXCEPTION)
        }
    }

    @Test
    fun testWrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            getMediatorK(DOMAIN_EVENTS_CONFIG_FILE_WRONG_CONSTRUCTOR)
            //mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
        }
    }

}