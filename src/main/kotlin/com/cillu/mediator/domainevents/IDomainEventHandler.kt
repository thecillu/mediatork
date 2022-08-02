package com.cillu.mediator.domainevents

interface IDomainEventHandler<T: DomainEvent> {
    fun handle(event: T)
}