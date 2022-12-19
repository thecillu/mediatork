package com.cillu.mediator.domainevents.domain

import com.cillu.mediator.TestItem
import com.cillu.mediator.domainevents.DomainEvent
import java.util.*

class TestDomainEvent2(idEvent: UUID, item: TestItem): DomainEvent(idEvent) {
    val item: TestItem = item
}


