package com.cillu.mediator

import com.cillu.mediator.domain.Aggregate
import java.util.UUID

open class Item2 private constructor(name: String): Aggregate() {
    lateinit var id: UUID
    var name: String = name

    companion object {
        fun create(name: String): Item2 {
            var item2 = Item2(name)
            item2.id = UUID.randomUUID()
            return item2
        }
    }
}
