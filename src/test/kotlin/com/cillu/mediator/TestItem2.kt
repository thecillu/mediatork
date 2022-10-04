package com.cillu.mediator

import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.domainevents.domain.TestDomainEvent
import java.util.UUID

open class TestItem2 private constructor(name: String): Aggregate() {
    lateinit var id: UUID
    var name: String = name

    companion object {
        fun create(name: String): TestItem2 {
            var item2 = TestItem2(name)
            item2.id = UUID.randomUUID();
            return item2;
        }
    }
}
