package com.cillu.mediator.commands

import com.cillu.mediator.Event
import java.util.*


open class Command(idEvent: UUID): Event(idEvent)