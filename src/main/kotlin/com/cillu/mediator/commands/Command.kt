package com.cillu.mediator.commands

import com.cillu.mediator.Event
import java.util.*


open class Command(id: UUID): Event() {
    val id: UUID = id;
}


