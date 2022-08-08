package com.cillu.mediator

import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.commands.Command
import com.cillu.mediator.domain.Aggregate
import com.cillu.mediator.domainevents.DomainEvent
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.integrationevents.IntegrationEvent
import com.cillu.mediator.mappers.CommandHandlersMapper
import com.cillu.mediator.mappers.DomainEventHandlersMapper
import com.cillu.mediator.mappers.IntegrationEventHandlersMapper
import com.cillu.mediator.mappers.QueryHandlersMapper
import com.cillu.mediator.queries.Query
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.servicebus.IServiceBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

class MediatorK private constructor(mediatorPaths: List<String>, internal val serviceBus: IServiceBus,
                                         private val servicesRegistry: ServiceRegistry): IMediator {
    private val logger = KotlinLogging.logger {}
    private var queryHandlersMapper: QueryHandlersMapper
    private var commandHandlersMapper: CommandHandlersMapper
    private var domainEventHandlersMapper: DomainEventHandlersMapper
    private var integrationEventHandlersMapper: IntegrationEventHandlersMapper
    internal var reflections: Reflections

    companion object {
        fun create(mediatorPaths: List<String>, serviceBus: IServiceBus, servicesRegistry: ServiceRegistry = ServiceRegistry()): IMediator {
            return MediatorK(mediatorPaths, serviceBus, servicesRegistry);
        }
    }

    init
    {
        reflections = getReflections(mediatorPaths)
        queryHandlersMapper = QueryHandlersMapper(reflections, servicesRegistry)
        commandHandlersMapper = CommandHandlersMapper(reflections, servicesRegistry)
        domainEventHandlersMapper = DomainEventHandlersMapper(reflections, servicesRegistry)
        integrationEventHandlersMapper = IntegrationEventHandlersMapper( this, servicesRegistry)
    }

    private fun getReflections(mediatorPaths: List<String>): Reflections{
        val paths = mediatorPaths.toTypedArray()
        return if (paths.isNullOrEmpty() || paths.size == 1 && (paths.contains("*") || paths[0].isNullOrEmpty())) {
            logger.info("Scanning all packages" )
            Reflections(
                ConfigurationBuilder()
                    .addUrls(ClasspathHelper.forJavaClassPath())
            )
        } else {
            paths.forEach {  logger.info("Scanning package $it" ) }
            Reflections(*paths)
        }
    }

    override fun send(command: Command): Any? {
        val annotatedClass = commandHandlersMapper.getHandlers()[command::class.java.name] ?: throw CommandHandlerNotFoundException(command::class.java.name)
        val methodHandler = annotatedClass.getMethod("handle", Command::class.java)
        var constructorArgs = getConstructorArgs(annotatedClass)
        logger.info("Invoking ${annotatedClass.name} with params: ${constructorArgs.toTypedArray()} for command $command");
        val result = methodHandler?.invoke(annotatedClass.constructors[0].newInstance(*constructorArgs.toTypedArray()), command)
        logger.info("Invoked ${annotatedClass.name} for command $command")
        return result
    }

    override fun send(query: Query): Any? {
        val annotatedClass = queryHandlersMapper.getHandlers()[query::class.java.name] ?: throw QueryHandlerNotFoundException(query::class.java.name)
        val methodHandler = annotatedClass.getMethod("handle", Query::class.java)
        var constructorArgs = getConstructorArgs(annotatedClass)
        logger.info("Invoking ${annotatedClass.name} with params: ${constructorArgs.toTypedArray()} for query $query");
        val result = methodHandler?.invoke(annotatedClass.constructors[0].newInstance(*constructorArgs.toTypedArray()), query)
        logger.info("Invoked ${annotatedClass.name} for query $query")
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

    fun invoke(domainEvent: DomainEvent) {
        val domainEventSet = getDomainEventHandlers(domainEvent)
        logger.info("Invoking all DomainEventHandlers for domain event $domainEvent")
        runBlocking {
            domainEventSet?.forEach {
                val methodHandler = it.getMethod("handle", DomainEvent::class.java)
                logger.info("Invoking ${it.name} for $domainEvent")
                launch(Dispatchers.Default) {
                    var constructorArgs = getConstructorArgs(it)
                    logger.info("Invoking ${it.name} with params: ${constructorArgs.toTypedArray()} for event $domainEvent");
                    methodHandler?.invoke(it.constructors[0].newInstance(*constructorArgs.toTypedArray()), domainEvent)
                    logger.info("Invoked ${it.name} for event ${domainEvent::class.java.name}")
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
        serviceBus.publish(integrationEvent)
        logger.info("Published $integrationEventName")
    }

    override fun process(integrationEvent: IntegrationEvent){
        val integrationEventName: String = integrationEvent::class.java.name
        val annotatedClass =
            integrationEventHandlersMapper.getHandlers()[integrationEventName] ?: throw IntegrationEventHandlerNotFoundException(
                integrationEventName
            )
        logger.info("Processing $integrationEventName with Handler $annotatedClass")
        val methodHandler = annotatedClass.getMethod("handle", IntegrationEvent::class.java)
        var constructorArgs = getConstructorArgs(annotatedClass)
        logger.info("Invoking ${annotatedClass.name} with params: ${constructorArgs.toTypedArray()} for event $integrationEventName");
        methodHandler?.invoke(annotatedClass.constructors[0].newInstance(*constructorArgs.toTypedArray()), integrationEvent)
        logger.info("Invoked $annotatedClass for event $integrationEventName")
    }

    override fun getQueryHandlers(): MutableMap<String, Class<QueryHandler>>{
        return queryHandlersMapper.getHandlers();
    }

    override fun getCommandsHandlers(): MutableMap<String, Class<CommandHandler>>{
        return commandHandlersMapper.getHandlers();
    }

    override fun getDomainEventsHandlers(): MutableMap<String, MutableSet<Class<DomainEventHandler>>>{
        return domainEventHandlersMapper.getHandlers();
    }

    override fun getIntegrationEventsHandlers(): MutableMap<String, Class<IntegrationEventHandler>>{
        return integrationEventHandlersMapper.getHandlers();
    }

    private fun getDomainEventHandlers(domainEvent: DomainEvent): MutableSet<Class<DomainEventHandler>>? {
        return domainEventHandlersMapper.getHandlers()[domainEvent::class.java.name];
    }
}

