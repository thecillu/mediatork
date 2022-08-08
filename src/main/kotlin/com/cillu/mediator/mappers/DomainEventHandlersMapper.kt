package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.DomainEventHandler
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class DomainEventHandlersMapper(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper()  {
    private val logger = KotlinLogging.logger {}
    private var domainEventHandlers: MutableMap<String, MutableSet<Class<DomainEventHandler>>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers(): MutableMap<String, MutableSet<Class<DomainEventHandler>>> {
        return domainEventHandlers;
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering DomainEventHandlers
        val annotatedClasses: Set<Class<DomainEventHandler>> =
            reflections.getTypesAnnotatedWith(DomainEventHandler()) as Set<Class<DomainEventHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1) throw Exception(
                "Annotated @DomainEventHandler Class must be implement IDomainEventHandler interface"
            )
            var domainEvent = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            //check registered services
            checkServices(annotatedClass, servicesRegistry)
            val domainEventSet = domainEventHandlers.getOrDefault(domainEvent, mutableSetOf())
            domainEventSet.add(annotatedClass)
            domainEventHandlers[domainEvent] = domainEventSet
            logger.info("Registered handler ${annotatedClass} for domainEvent ${domainEvent}")
        }
    }
}