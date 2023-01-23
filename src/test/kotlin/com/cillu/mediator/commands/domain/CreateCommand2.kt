package com.cillu.mediator.commands.domain

import com.cillu.mediator.commands.Command
import java.util.*

class CreateCommand2(idEvent:UUID, name: String): Command(idEvent) {
    val name: String = name
}


