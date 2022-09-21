package com.cillu.mediator.integrationevents.awssns

import com.cillu.mediator.TestBase
import com.cillu.mediator.integrationevents.config.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import org.junit.jupiter.api.Test

class TestIntegrationEvents(): TestBase() {


    val mediatorK = getMediatorK(INTEGRATION_EVENTS_CONFIG_FILE_SNS)

    @Test
    fun successConfig() {
        val handlers = mediatorK.getIntegrationEventsHandlers();
        assert( handlers.size == 1)
        assert( handlers[FAKE_INTEGRATIONEVENT_CLASS] == FakeIntegrationEventHandler::class.java)
    }

    @Test
    fun publishAndConsume() {
        var messages: Int = 1000
        for ( i in 1..messages){
            mediatorK.publish(FakeIntegrationEvent())
        }
        Thread.sleep(2000)
        val memoryRepository = mediatorK.getServiceRegistry().getService(MemoryRepository::class.java) as MemoryRepository
        println("MemoryRepo count: ${memoryRepository.count}")
        assert ( memoryRepository.count == messages)
    }
}