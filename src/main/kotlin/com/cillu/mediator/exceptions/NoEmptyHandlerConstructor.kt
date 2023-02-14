package com.cillu.mediator.exceptions

class NoEmptyHandlerConstructor(val handler: String):
    Exception("$handler Class must have an empty constructor")