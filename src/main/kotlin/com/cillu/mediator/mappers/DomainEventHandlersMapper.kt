package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.exceptions.DomainEventHandlerConfigurationException
import com.cillu.mediator.exceptions.NoEmptyHandlerConstructor
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class DomainEventHandlersMapper internal constructor(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper()  {
    private val logger = KotlinLogging.logger {}
    private var domainEventHandlers: MutableMap<String, MutableSet<IDomainEventHandler<*>>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers(): MutableMap<String, MutableSet<IDomainEventHandler<*>>> {
        return domainEventHandlers;
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering DomainEventHandlers
        val annotatedClasses: Set<Class<DomainEventHandler>> =
            reflections.getTypesAnnotatedWith(DomainEventHandler()) as Set<Class<DomainEventHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw DomainEventHandlerConfigurationException(annotatedClass.name)
            if (annotatedClass.constructors[0].parameters.isNotEmpty())
                throw NoEmptyHandlerConstructor(annotatedClass::class.java.name)
            var domainEvent = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            var classInstance = annotatedClass.constructors[0].newInstance() as IDomainEventHandler<*>
            val domainEventSet = domainEventHandlers.getOrDefault(domainEvent, mutableSetOf())
            domainEventSet.add(classInstance)
            domainEventHandlers[domainEvent] = domainEventSet
            servicesRegistry.register(annotatedClass, classInstance)
            logger.info("Registered handler ${annotatedClass} for domainEvent ${domainEvent}")
        }
    }
}