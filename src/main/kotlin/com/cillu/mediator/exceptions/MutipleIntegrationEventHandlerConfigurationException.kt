package com.cillu.mediator.exceptions

class MutipleIntegrationEventHandlerConfigurationException(val command: String):
    Exception("Found multiple CommandHandlers for the command ${command}\")") {
}