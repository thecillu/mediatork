package com.cillu.mediator

import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import java.util.UUID

open class TestItem private constructor(name: String): Aggregate() {
    lateinit var id: UUID
    var name: String = name

    companion object {
        fun create(name: String): TestItem {
            var item = TestItem(name)
            item.id = UUID.randomUUID();
            item.addDomainEvent(TestDomainEvent(UUID.randomUUID(), item))
            return item;
        }
    }
}
