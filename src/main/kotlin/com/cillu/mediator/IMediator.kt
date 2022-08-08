package com.cillu.mediator

import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.commands.Command
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.queries.Query

interface IMediator {
//    fun registerService(classType: Class<*>, objectInstance: Any)
    fun send( command: Command ): Any?
    fun send( query: Query ): Any?
    fun raiseDomainEvents( aggregate: Aggregate )
    fun publish( integrationEvent: IntegrationEvent )
    fun process( integrationEvent: IntegrationEvent )
    fun getCommandsHandlers(): MutableMap<String, Class<CommandHandler>>
    fun getDomainEventsHandlers(): MutableMap<String, MutableSet<Class<DomainEventHandler>>>
    fun getIntegrationEventsHandlers(): MutableMap<String, Class<IntegrationEventHandler>>
    fun getQueryHandlers(): MutableMap<String, Class<QueryHandler>>
}