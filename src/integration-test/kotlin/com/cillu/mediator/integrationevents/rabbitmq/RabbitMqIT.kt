package com.cillu.mediator.integrationevents.rabbitmq

import com.cillu.mediator.IntegrationBase
import com.cillu.mediator.integrationevents.config.single.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.services.IRepository
import com.cillu.mediator.integrationevents.services.MemoryRepository
import org.junit.jupiter.api.Test
import java.util.*

class RabbitMqIT(): IntegrationBase() {


    @Test
    fun testPublishAndConsume() {
        val mediatorK = getMediatorKwithRabbitMQ(INTEGRATION_EVENTS_CONFIG_FILE_RABBITMQ)
        mediatorK.publish(FakeIntegrationEvent(UUID.randomUUID()))
        Thread.sleep(3000)
        val memoryRepository = mediatorK.getComponent(IRepository::class.java) as MemoryRepository
        println( memoryRepository.count )
        assert ( memoryRepository.count == 1)
    }
}