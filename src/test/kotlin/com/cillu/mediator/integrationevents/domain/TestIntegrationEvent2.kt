package com.cillu.mediator.integrationevents.domain

import com.cillu.mediator.TestItem
import com.cillu.mediator.integrationevents.IntegrationEvent
import java.util.*

class TestIntegrationEvent2(idEvent: UUID, item: TestItem): IntegrationEvent(idEvent) {
    val item: TestItem = item
}


