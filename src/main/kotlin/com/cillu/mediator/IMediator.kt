package com.cillu.mediator

import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.commands.Command
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.Query
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging

interface IMediator {
    fun send(command: Command): Any?
    fun send(query: Query): Any?
    fun raiseDomainEvents(aggregate: Aggregate)
    fun publish(integrationEvent: IntegrationEvent)
    fun process(integrationEvent: IntegrationEvent)
    fun getCommandsHandlers(): MutableMap<String, ICommandHandler<*>>
    fun getDomainEventsHandlers(): MutableMap<String, MutableSet<IDomainEventHandler<*>>>
    fun getIntegrationEventsHandlers(): MutableMap<String, IIntegrationEventHandler<*>>
    fun getQueryHandlers(): MutableMap<String, IQueryHandler<*>>
    fun getComponent(componentClass: Class<*>): Any?
    fun getMessageBroker(): IMessageBroker?
}
