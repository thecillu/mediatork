package com.cillu.mediator.integrationevents.awssns

import com.cillu.mediator.TestBase
import com.cillu.mediator.integrationevents.config.single.FakeIntegrationEventHandler
import com.cillu.mediator.integrationevents.domain.PippoIntegrationEvent
import com.cillu.mediator.integrationevents.domain.PlutoIntegrationEvent
import com.cillu.mediator.integrationevents.domain.FakeIntegrationEvent
import com.cillu.mediator.integrationevents.domain.FilteredIntegrationEvent
import com.cillu.mediator.services.MemoryRepository
import org.junit.jupiter.api.Test
import java.util.*

class
TestIntegrationEvents(): TestBase() {

    @Test
    fun publishMultiple() {
        val mediatorK = getMediatorKwithAwsSns(INTEGRATION_EVENTS_CONFIG_FILE_SNS_MULTIPLE)
        val memoryRepository = mediatorK.getComponent(MemoryRepository::class.java) as MemoryRepository
        memoryRepository.count = 0
        var messages: Long = 3
        for ( i in 1..messages ){
            mediatorK.publish(FakeIntegrationEvent())
            mediatorK.publish(PippoIntegrationEvent())
            mediatorK.publish(FilteredIntegrationEvent())
            mediatorK.publish(PlutoIntegrationEvent())
        }
        Thread.sleep(5000)
        println("MemoryRepo count: ${memoryRepository.count}")
        assert ( memoryRepository.count == messages.toInt() * 3)
    }
}