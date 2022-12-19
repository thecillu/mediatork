package com.cillu.mediator.exceptions

class DomainEventHandlerNotFoundException(val domainEvent: String):
Exception("No DomaiEventHandler found for the domainEvent ${domainEvent}\")") {
}
