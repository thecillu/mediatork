package com.cillu.mediator.exceptions

class IntegrationEventHandlerNotFoundException(val integrationEvent: String):
Exception("No IntegrationEventHandler found for the command ${integrationEvent}\")") {
}
