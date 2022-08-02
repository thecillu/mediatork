package com.cillu.mediator.servicebus

import com.cillu.mediator.IMediator
import com.cillu.mediator.commands.Command
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.queries.Query

interface IServiceBus {
    fun bind( integrationEventName: String )
    fun consume( mediator: IMediator )
    fun publish( integrationEvent: IntegrationEvent )
}