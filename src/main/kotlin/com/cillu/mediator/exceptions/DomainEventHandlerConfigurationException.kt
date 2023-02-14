package com.cillu.mediator.exceptions

class DomainEventHandlerConfigurationException(val handler: String):
    Exception("Annotated @DomainEventHandler $handler Class must implement IDomainEventHandler interface")