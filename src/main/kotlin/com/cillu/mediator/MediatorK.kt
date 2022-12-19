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
            return MediatorK(mediatorPaths, messageBroker, servicesRegistry);
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
        logger.info("Invoking ${classInstance?.javaClass?.name} for command $command");
        val result = methodHandler?.invoke(classInstance, command)
        logger.info("Invoked ${classInstance?.javaClass?.name} for command $command")
        return result
    }

    override fun send(query: Query): Any? {
        var classInstance = queryHandlersMapper.getHandlers()[query::class.java.name]
            ?: throw QueryHandlerNotFoundException(query::class.java.name)
        val methodHandler = classInstance?.javaClass?.getMethod("handle", Query::class.java)
        logger.info("Invoking ${classInstance?.javaClass?.name} for query $query");
        val result = methodHandler?.invoke(classInstance, query)
        logger.info("Invoked ${classInstance?.javaClass?.name} for query $query")
        return result
    }

    private fun getConstructorArgs(annotatedClass: Class<*>): MutableList<Any> {
        var constructorArgs = mutableListOf<Any>()
        for (constructor in annotatedClass.constructors) {
            val parameters = constructor.parameters
            for (parameter in parameters) {
                var parameterInstance = servicesRegistry.getService(parameter.type)
                logger.info("Found instance ${parameterInstance} for parameter type ${parameter.type}");
                constructorArgs.add(parameterInstance!!)
            }
        }
        return constructorArgs
    }

    private fun invoke(domainEvent: DomainEvent) {
        val domainEventSet = getDomainEventHandlers(domainEvent)
        logger.info("Invoking all DomainEventHandlers for domain event $domainEvent")
        runBlocking {
            domainEventSet?.forEach {
                val methodHandler = it?.javaClass?.getMethod("handle", DomainEvent::class.java)
                logger.info("Invoking ${it?.javaClass?.name} for $domainEvent")
                launch(Dispatchers.Default) {
                    //var constructorArgs = getConstructorArgs(it)
                    logger.info("Invoking ${it?.javaClass?.name} for event $domainEvent");
                    methodHandler?.invoke(it, domainEvent)
                    logger.info("Invoked ${it?.javaClass?.name} for event $domainEvent");
                }
            }
        }
        logger.info("Invoked all DomainEventHandlers for domain event $domainEvent")
    }

    override fun raiseDomainEvents(aggregate: Aggregate) {
        var domainEvents: List<DomainEvent> = aggregate.getDomainEvents()
        if (domainEvents.isEmpty()) {
            logger.info("No DomainEvents for Aggregate ${aggregate::class.java}")
            return
        }
        logger.info("Raising ${domainEvents.size} DomainEvents for Aggregate ${aggregate::class.java}")
        runBlocking {
            domainEvents.forEach {
                logger.info("Invoking all DomainEventsHandlers for DomainEvent ${it::class.java}")
                launch(Dispatchers.Default) { invoke(it) }
            }
        }
        logger.info("Raised ${domainEvents.size} DomainEvents")
        aggregate.removeDomainEvents();
        logger.info("Removed raised DomainEvents (new size is ${domainEvents.size})")
    }

    override fun publish(integrationEvent: IntegrationEvent) {
        val integrationEventName: String = integrationEvent::class.java.name
        logger.info("Publishing $integrationEventName")
        if (messageBroker == null) {
            throw Exception("You need to define a MessageBroker")
        }
        messageBroker.publish(integrationEvent)
        logger.info("Published $integrationEventName")
    }

    override fun process(integrationEvent: IntegrationEvent) {
        val integrationEventName: String = integrationEvent::class.java.name
        var classInstance = integrationEventHandlersMapper.getHandlers()[integrationEventName]
            ?: throw IntegrationEventHandlerNotFoundException(integrationEventName)
        logger.info("Processing $integrationEventName with Handler ${classInstance::class.java.name}")
        val methodHandler = classInstance?.javaClass?.getMethod("handle", IntegrationEvent::class.java)
        logger.info("Invoking ${classInstance::class.java.name} for event $integrationEventName");
        methodHandler?.invoke(classInstance, integrationEvent)
        logger.info("Invoked ${classInstance::class.java.name} for event $integrationEventName")
    }

    override fun getQueryHandlers(): MutableMap<String, IQueryHandler<*>> {
        return queryHandlersMapper.getHandlers();
    }

    override fun getCommandsHandlers(): MutableMap<String, ICommandHandler<*>> {
        return commandHandlersMapper.getHandlers();
    }

    override fun getDomainEventsHandlers(): MutableMap<String, MutableSet<IDomainEventHandler<*>>> {
        return domainEventHandlersMapper.getHandlers();
    }

    override fun getIntegrationEventsHandlers(): MutableMap<String, IIntegrationEventHandler<*>> {
        return integrationEventHandlersMapper.getHandlers();
    }

    private fun getDomainEventHandlers(domainEvent: DomainEvent): MutableSet<IDomainEventHandler<*>>? {
        return domainEventHandlersMapper.getHandlers()[domainEvent::class.java.name];
    }

    override fun getComponent(componentClass: Class<*>): Any?{
        return servicesRegistry.getService(componentClass)
    }

    override fun getMessageBroker(): IMessageBroker? {
        return messageBroker
    }

}

