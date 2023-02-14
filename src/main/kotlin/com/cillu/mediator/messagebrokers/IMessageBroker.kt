package com.cillu.mediator.messagebrokers

import com.cillu.mediator.IMediator
import com.cillu.mediator.integrationevents.IntegrationEvent

open interface IMessageBroker {
    fun bind(integrationEventName: String)
    fun consume(mediator: IMediator)
    fun publish(integrationEvent: IntegrationEvent)
}