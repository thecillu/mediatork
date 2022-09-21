package com.cillu.mediator.exceptions

class IntegrationEventHandlerConfigurationException(val handler: String):
    Exception("Annotated @IntegrationEventHandler $handler Class must implement IIntegrationEventHandler interface") {
}