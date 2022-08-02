package com.cillu.mediator.exceptions

class CommandHandlerNotFoundException(val command: String):
Exception("No CommandHandlers found for the command ${command}\")") {
}
