package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.DomainEventHandler
import mu.KotlinLogging
import org.reflections.Reflections

class DomainEventHandlersMapper(reflections: Reflections) {
    private val logger = KotlinLogging.logger {}
    private var domainEventHandlers: MutableMap<String, MutableSet<Class<DomainEventHandler>>> = HashMap()

    init {
        register(reflections)
    }

    internal fun getHandlers(): MutableMap<String, MutableSet<Class<DomainEventHandler>>> {
        return domainEventHandlers;
    }

    private fun register(reflections: Reflections) {
        // Registering DomainEventHandlers
        val annotatedClasses: Set<Class<DomainEventHandler>> =
            reflections.getTypesAnnotatedWith(DomainEventHandler()) as Set<Class<DomainEventHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1) throw Exception(
                "Annotated @DomainEventHandler Class must be implement IDomainEventHandler interface"
            )
            var domainEvent = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            val domainEventSet = domainEventHandlers.getOrDefault(domainEvent, mutableSetOf())
            domainEventSet.add(annotatedClass)
            domainEventHandlers[domainEvent] = domainEventSet
            logger.info("Registered handler ${annotatedClass} for domainEvent ${domainEvent}")
        }
    }
}