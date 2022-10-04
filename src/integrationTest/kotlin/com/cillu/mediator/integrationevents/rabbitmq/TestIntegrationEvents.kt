package com.cillu.mediator.integrationevents.rabbitmq

import com.cillu.mediator.TestBase
import com.cillu.mediator.integrationevents.config.single.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import org.junit.jupiter.api.Test

class TestIntegrationEvents(): TestBase() {

    val pair = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ)
    val mediatorK = pair.first
    val messageBroker = pair.second

    @Test
    fun successConfig() {
        val handlers = mediatorK.getIntegrationEventsHandlers();
        assert( handlers.size == 1)
        assert( handlers[FAKE_INTEGRATIONEVENT_CLASS] == FakeIntegrationEventHandler::class.java)
    }

    @Test
    fun publishAndConsume() {
        mediatorK.publish(FakeIntegrationEvent())
        Thread.sleep(3000)
        val memoryRepository = mediatorK.getServiceRegistry().getService(MemoryRepository::class.java) as MemoryRepository
        assert ( memoryRepository.count == 1)
    }
}