package com.cillu.mediator.domainevents.domain

import com.cillu.mediator.TestItem
import com.cillu.mediator.domainevents.DomainEvent
import java.util.UUID

class TestDomainEvent(idEvent:UUID, item: TestItem): DomainEvent(idEvent) {
    val item: TestItem = item
}


