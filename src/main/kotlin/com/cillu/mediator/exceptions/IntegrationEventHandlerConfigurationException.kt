package com.cillu.mediator.exceptions

class IntegrationEventHandlerConfigurationException(val command: String):
    Exception("Found multiple CommandHandlers for the command ${command}\")") {
}