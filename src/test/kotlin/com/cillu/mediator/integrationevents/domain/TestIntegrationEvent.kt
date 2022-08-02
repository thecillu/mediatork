package com.cillu.mediator.integrationevents.domain

import com.cillu.mediator.TestItem
import com.cillu.mediator.integrationevents.IntegrationEvent

class TestIntegrationEvent(item: TestItem): IntegrationEvent() {
    val item: TestItem = item
}


