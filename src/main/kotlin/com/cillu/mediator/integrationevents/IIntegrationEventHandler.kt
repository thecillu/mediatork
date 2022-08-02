package com.cillu.mediator.integrationevents

interface IIntegrationEventHandler<T: IntegrationEvent> {
    fun handle(event: T)
}