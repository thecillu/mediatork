package com.cillu.mediator.commands

import com.cillu.mediator.Event
import java.util.*


open class Command(): Event() {
    val id: UUID = UUID.randomUUID();
}


