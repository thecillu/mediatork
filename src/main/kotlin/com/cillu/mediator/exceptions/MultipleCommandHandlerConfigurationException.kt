package com.cillu.mediator.exceptions

class MultipleCommandHandlerConfigurationException(val command: String):
    Exception("Found multiple CommandHandlers for the command ${command}\")") {
}