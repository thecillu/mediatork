package com.cillu.mediator

import com.cillu.mediator.commands.Command
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.domainevents.DomainEvent
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.mappers.CommandHandlersMapper
import com.cillu.mediator.mappers.DomainEventHandlersMapper
import com.cillu.mediator.mappers.IntegrationEventHandlersMapper
import com.cillu.mediator.mappers.QueryHandlersMapper
import com.cillu.mediator.queries.Query
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.queries.IQueryHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.Scanners

class MediatorK private constructor(
    mediatorPaths: List<String>,
    internal val messageBroker: IMessageBroker?,
    private var servicesRegistry: ServiceRegistry
) : IMediator {
    private val logger = KotlinLogging.logger {}
    private var queryHandlersMapper: QueryHandlersMapper
    private var commandHandlersMapper: CommandHandlersMapper
    private var domainEventHandlersMapper: DomainEventHandlersMapper
    private var integrationEventHandlersMapper: IntegrationEventHandlersMapper
    internal var reflections: Reflections

    companion object {
        internal fun create(
            mediatorPaths: List<String>,
            messageBroker: IMessageBroker?,
            servicesRegistry: ServiceRegistry
        ): IMediator {
            return MediatorK(mediatorPaths, messageBroker, servicesRegistry)
        }
    }

    init {
        reflections = getReflections(mediatorPaths)
        queryHandlersMapper = QueryHandlersMapper(reflections, servicesRegistry)
        commandHandlersMapper = CommandHandlersMapper(reflections, servicesRegistry)
        domainEventHandlersMapper = DomainEventHandlersMapper(reflections, servicesRegistry)
        integrationEventHandlersMapper = IntegrationEventHandlersMapper(this, servicesRegistry)
        servicesRegistry.injectField(reflections, this)
    }

    private fun getReflections(mediatorPaths: List<String>): Reflections {
        val paths = mediatorPaths.toTypedArray()
        return Reflections(*paths,Scanners.FieldsAnnotated, Scanners.TypesAnnotated, Scanners.ConstructorsAnnotated, Scanners.MethodsAnnotated )
    }

    override fun send(command: Command): Any? {
        var classInstance: ICommandHandler<*>? = commandHandlersMapper.getHandlers()[command::class.java.name]
            ?: throw CommandHandlerNotFoundException(command::class.java.name)
        val methodHandler = classInstance?.javaClass?.getMethod("handle", Command::class.java)
        logger.info(">>> Invoking ${classInstance?.javaClass?.name} for command ${command::class.java} [id:${command.idEvent}]")
        val result = methodHandler?.invoke(classInstance, command)
        logger.info("<<< Invoked ${classInstance?.javaClass?.name} for command  ${command::class.java} [id:${command.idEvent}]")
        return result
    }

    override fun send(query: Query): Any? {
         val classInstance = queryHandlersMapper.getHandlers()[query::class.java.name]
            ?: throw QueryHandlerNotFoundException(query::class.java.name)
        val methodHandler = classInstance.javaClass.getMethod("handle", Query::class.java)
        logger.info(">>> Invoking ${classInstance.javaClass.name} for query  ${query::class.java} [id:${query.idEvent}]")
        val result = methodHandler.invoke(classInstance, query)
        logger.info("<<< Invoked ${classInstance.javaClass.name} for query ${query::class.java} [id:${query.idEvent}]\"")
        return result
    }

    private fun invoke(domainEvent: DomainEvent) {
        val domainEventSet = getDomainEventHandlers(domainEvent)
        runBlocking {
            domainEventSet?.forEach {
                val methodHandler = it.javaClass.getMethod("handle", DomainEvent::class.java)
                launch(Dispatchers.Default) {
                    logger.info(">>> Invoking ${it.javaClass.name} for event ${domainEvent::class.java} [id:${domainEvent.idEvent}]")
                    methodHandler.invoke(it, domainEvent)
                    logger.info("<<< Invoked ${it.javaClass.name} for event ${domainEvent::class.java} [id:${domainEvent.idEvent}]")
                }
            }
        }
        logger.info("Invoked all DomainEventHandlers for domain event $domainEvent")
    }

    override fun raiseDomainEvents(aggregate: Aggregate) {
        var domainEvents: List<DomainEvent> = aggregate.getDomainEvents()
        if (domainEvents.isEmpty()) {
            logger.info("There aren't DomainEvents for Aggregate ${aggregate::class.java}")
            return
        }
        logger.info("Raising ${domainEvents.size} DomainEvents for Aggregate ${aggregate::class.java}")
        runBlocking {
            domainEvents.forEach {
                launch(Dispatchers.Default) { invoke(it) }
            }
        }
        logger.info("Raised ${domainEvents.size} DomainEvents for Aggregate ${aggregate::class.java}")
        aggregate.removeDomainEvents()
        logger.info("Removed DomainEvents for Aggregate ${aggregate::class.java} (new size is ${domainEvents.size})")
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        val integrationEventName: String = integrationEvent::class.java.name
        logger.info(">>> Publishing $integrationEventName  [id:${integrationEvent.idEvent}]")
        if (messageBroker == null) {
            throw Exception("You need to configure a MessageBroker! [id:${integrationEvent.idEvent}]")
        }
        messageBroker.publish(integrationEvent)
        logger.info("<<< Published $integrationEventName [id:${integrationEvent.idEvent}]")
    }

    override fun process(integrationEvent: IntegrationEvent) {
        val integrationEventName: String = integrationEvent::class.java.name
        val classInstance = integrationEventHandlersMapper.getHandlers()[integrationEventName]
            ?: throw IntegrationEventHandlerNotFoundException(integrationEventName)
        val methodHandler = classInstance.javaClass.getMethod("handle", IntegrationEvent::class.java)
        logger.info(">>> Invoking ${classInstance::class.java.name} for event $integrationEventName [id:${integrationEvent.idEvent}]")
        methodHandler.invoke(classInstance, integrationEvent)
        logger.info("<<< Invoked ${classInstance::class.java.name} for event $integrationEventName [id:${integrationEvent.idEvent}]")
    }

    override fun getQueryHandlers(): MutableMap<String, IQueryHandler<*>> {
        return queryHandlersMapper.getHandlers()
    }

    override fun getCommandsHandlers(): MutableMap<String, ICommandHandler<*>> {
        return commandHandlersMapper.getHandlers()
    }

    override fun getDomainEventsHandlers(): MutableMap<String, MutableSet<IDomainEventHandler<*>>> {
        return domainEventHandlersMapper.getHandlers()
    }

    override fun getIntegrationEventsHandlers(): MutableMap<String, IIntegrationEventHandler<*>> {
        return integrationEventHandlersMapper.getHandlers()
    }

    private fun getDomainEventHandlers(domainEvent: DomainEvent): MutableSet<IDomainEventHandler<*>>? {
        return domainEventHandlersMapper.getHandlers()[domainEvent::class.java.name]
    }

    override fun getComponent(componentClass: Class<*>): Any?{
        return servicesRegistry.getService(componentClass)
    }

    override fun getMessageBroker(): IMessageBroker? {
        return messageBroker
    }

}

