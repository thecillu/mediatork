package com.cillu.mediator

import com.cillu.mediator.commands.Command
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.queries.Query

interface IMediator {
    fun registerService(classType: Class<*>, objectInstance: Any)
    fun send( command: Command ): Any?
    fun send( query: Query ): Any?
    fun raiseDomainEvents( aggregate: Aggregate )
    fun publish( integrationEvent: IntegrationEvent )
    fun process( integrationEvent: IntegrationEvent )
}