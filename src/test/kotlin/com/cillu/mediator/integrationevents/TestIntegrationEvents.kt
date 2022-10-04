package com.cillu.mediator.integrationevents

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.exceptions.IntegrationEventHandlerConfigurationException
import com.cillu.mediator.exceptions.MutipleIntegrationEventHandlerConfigurationException
import com.cillu.mediator.exceptions.IntegrationEventHandlerNotFoundException
import com.cillu.mediator.exceptions.MissingServiceException
import com.cillu.mediator.integrationevents.config.noservice.TestNoServiceIntegrationEventHandler
import com.cillu.mediator.integrationevents.config.success.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.config.success.TestIntegrationEvent2Handler
import com.cillu.mediator.integrationevents.config.success.TestIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.TestIntegrationEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestIntegrationEvents(): TestBase() {

    private val TEST_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent"
    private val TEST_INTEGRATIONEVENT2_CLASS = "com.cillu.mediator.integrationevents.domain.TestIntegrationEvent2"
    private val FAKE_INTEGRATIONEVENT_CLASS = "com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getIntegrationEventsHandlers();
        assert( handlers.size == 3)
        assert( handlers[TEST_INTEGRATIONEVENT_CLASS] == TestIntegrationEventHandler::class.java)
        assert( handlers[TEST_INTEGRATIONEVENT2_CLASS] == TestIntegrationEvent2Handler::class.java)
        assert( handlers[FAKE_INTEGRATIONEVENT_CLASS] == FakeIntegrationEventHandler::class.java)
    }

    @Test
    fun duplicateConfig() {
        assertThrows<MutipleIntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_DUPLICATE)
        }
    }


    @Test
    fun missingConfig() {
        assertThrows<IntegrationEventHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING)
            mediatorK.process(TestIntegrationEvent( TestItem.create("TestIntegrationEvent")))
        }
    }

    @Test
    fun submit() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS)
        mediatorK.publish(FakeIntegrationEvent())
    }

    @Test
    fun missingService() {
        assertThrows<MissingServiceException> {
            val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_MISSING_SERVICE)
        }
    }


    @Test
    fun successConfigNoService() {
        val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE, false)
        val handlers = mediatorK.getIntegrationEventsHandlers()
        assert( handlers.size == 1)
        assert( handlers[TEST_INTEGRATIONEVENT_CLASS] == TestNoServiceIntegrationEventHandler::class.java)
    }

    @Test
    fun wrongInterface() {
        assertThrows<IntegrationEventHandlerConfigurationException> {
            getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_EXCEPTION)
        }
    }
}