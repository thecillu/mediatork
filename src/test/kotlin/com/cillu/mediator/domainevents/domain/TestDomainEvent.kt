package com.cillu.mediator.domainevents.domain

import com.cillu.mediator.TestItem
import com.cillu.mediator.domainevents.DomainEvent

class TestDomainEvent(item: TestItem): DomainEvent() {
    val item: TestItem = item
}


