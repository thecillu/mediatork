package com.cillu.mediator.integrationevents

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.integrationevents.config.noservice.TestNoServiceIntegrationEventHandler
import com.cillu.mediator.integrationevents.config.success.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.config.success.TestIntegrationEvent2Handler
import com.cillu.mediator.integrationevents.config.success.TestIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TestIntegrationEvents(): TestBase() {

    private val TEST_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent"
    private val TEST_INTEGRATIONEVENT2_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent2"
    private val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getIntegrationEventsHandlers();
        assert( handlers.size == 3)
        assert( handlers[TEST_INTEGRATIONEVENT_CLASS]!!::class.java == TestIntegrationEventHandler::class.java)
        assert( handlers[TEST_INTEGRATIONEVENT2_CLASS]!!::class.java == TestIntegrationEvent2Handler::class.java)
        assert( handlers[FAKE_INTEGRATIONEVENT_CLASS]!!::class.java == FakeIntegrationEventHandler::class.java)
    }

    @Test
    fun duplicateConfig() {
        assertThrows<MultipleIntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_DUPLICATE)
        }
    }


    @Test
    fun missingConfig() {
        assertThrows<IntegrationEventHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING)
            mediatorK.process(TestIntegrationEvent( UUID.randomUUID(), TestItem.create("TestIntegrationEvent")))
        }
    }

    @Test
    fun submit() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        mediatorK.publish(FakeIntegrationEvent(UUID.randomUUID()))
    }

    @Test
    fun missingService() {
        assertThrows<MissingComponentException> {
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING_SERVICE)
        }
    }


    @Test
    fun successConfigNoService() {
        assertThrows<MissingComponentException> {
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE, false)
        }
    }

    @Test
    fun wrongInterface() {
        assertThrows<IntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_EXCEPTION)
        }
    }

    @Test
    fun wrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_WRONG_CONSTRUCTOR)
        }
    }
}