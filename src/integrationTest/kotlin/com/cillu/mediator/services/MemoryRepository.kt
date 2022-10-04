package com.cillu.mediator.services

class MemoryRepository {

    var count = 0

    fun increment() {
        println("Count before increment: $count")
        count++
        println("Count after increment: $count")
    }

}