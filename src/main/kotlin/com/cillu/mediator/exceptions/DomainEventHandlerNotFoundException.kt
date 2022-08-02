package com.cillu.mediator.exceptions

class DomainEventHandlerNotFoundException(val domainEvent: String):
Exception("No DomainEventHandler found for the domainEvent ${domainEvent}\")") {
}
