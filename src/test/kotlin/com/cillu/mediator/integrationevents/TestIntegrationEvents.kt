package com.cillu.mediator.integrationevents

import com.cillu.mediator.Base
import com.cillu.mediator.Item
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.integrationevents.config.success.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.config.success.IntegrationEvent2Handler
import com.cillu.mediator.integrationevents.config.success.IntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TestIntegrationEvents : Base() {

    private val TEST_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent"
    private val TEST_INTEGRATIONEVENT2_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent2"
    private val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"

    @Test
    fun testSuccessConfig() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getIntegrationEventsHandlers()
        assert( handlers.size == 3)
        assert( handlers[TEST_INTEGRATIONEVENT_CLASS]!!::class.java == IntegrationEventHandler::class.java)
        assert( handlers[TEST_INTEGRATIONEVENT2_CLASS]!!::class.java == IntegrationEvent2Handler::class.java)
        assert( handlers[FAKE_INTEGRATIONEVENT_CLASS]!!::class.java == FakeIntegrationEventHandler::class.java)
    }

    @Test
    fun testDuplicateConfig() {
        assertThrows<MultipleIntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_DUPLICATE)
        }
    }


    @Test
    fun testMissingConfig() {
        assertThrows<IntegrationEventHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING)
            mediatorK.process(TestIntegrationEvent( UUID.randomUUID(), Item.create("TestIntegrationEvent")))
        }
    }

    @Test
    fun testSubmit() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        mediatorK.publish(FakeIntegrationEvent(UUID.randomUUID()))
    }

    @Test
    fun testMissingService() {
        assertThrows<MissingComponentException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING_SERVICE)
        }
    }


    @Test
    fun testSuccessConfigNoService() {
        assertThrows<MissingComponentException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE, false)
        }
    }

    @Test
    fun testWrongInterface() {
        assertThrows<IntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_EXCEPTION)
        }
    }

    @Test
    fun testWrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_WRONG_CONSTRUCTOR)
        }
    }
}