package com.cillu.mediator.commands.domain

import com.cillu.mediator.commands.Command
import java.util.*

class TestCreateCommand2(id: UUID, name: String): Command() {
    val name: String = name
}


