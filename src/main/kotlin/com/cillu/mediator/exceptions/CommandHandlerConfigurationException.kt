package com.cillu.mediator.exceptions

class CommandHandlerConfigurationException(val command: String):
    Exception("Found multiple CommandHandlers for the command ${command}\")") {
}