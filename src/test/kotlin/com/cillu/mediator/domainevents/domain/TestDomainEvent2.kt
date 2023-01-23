package com.cillu.mediator.domainevents.domain

import com.cillu.mediator.Item
import com.cillu.mediator.domainevents.DomainEvent
import java.util.*

class TestDomainEvent2(idEvent: UUID, item: Item): DomainEvent(idEvent) {
    val item: Item = item
}


