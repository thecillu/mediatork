package com.cillu.mediator.domainevents.domain

import com.cillu.mediator.Item
import com.cillu.mediator.domainevents.DomainEvent
import java.util.UUID

class TestDomainEvent(idEvent:UUID, item: Item): DomainEvent(idEvent) {
    val item: Item = item
}


