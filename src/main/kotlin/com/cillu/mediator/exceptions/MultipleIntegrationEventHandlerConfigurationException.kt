package com.cillu.mediator.exceptions

class MultipleIntegrationEventHandlerConfigurationException(val command: String):
    Exception("Found multiple CommandHandlers for the command ${command}\")") {
}