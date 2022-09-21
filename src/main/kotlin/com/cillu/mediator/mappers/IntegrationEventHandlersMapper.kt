package com.cillu.mediator.mappers

import com.cillu.mediator.MediatorK
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.exceptions.IntegrationEventHandlerConfigurationException
import com.cillu.mediator.exceptions.MutipleIntegrationEventHandlerConfigurationException
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging

class IntegrationEventHandlersMapper(mediatorK: MediatorK, servicesRegistry: ServiceRegistry): HandlerMapper() {
    private val logger = KotlinLogging.logger {}
    private var integrationEventHandlers: MutableMap<String, Class<IntegrationEventHandler>> = HashMap()

    init {
        register(mediatorK, servicesRegistry)
    }

    internal fun getHandlers(): MutableMap<String, Class<IntegrationEventHandler>> {
        return integrationEventHandlers;
    }

    private fun register(mediatorK: MediatorK, servicesRegistry: ServiceRegistry)
    {
        // Registering IntegrationEventHandlers
        val annotatedClasses: Set<Class<IntegrationEventHandler>> =
            mediatorK.reflections.getTypesAnnotatedWith(IntegrationEventHandler()) as Set<Class<IntegrationEventHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw IntegrationEventHandlerConfigurationException(annotatedClass.name)
            var integrationEventName = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (integrationEventHandlers.containsKey(integrationEventName)) throw MutipleIntegrationEventHandlerConfigurationException(integrationEventName)
            //check registered services
            checkServices(annotatedClass, servicesRegistry)
            integrationEventHandlers[integrationEventName] = annotatedClass
            logger.info("Registered handler ${annotatedClass} for IntegrationEvent ${integrationEventName}")
            // Bind the integration event with routingKey = integrationEventName
            mediatorK.serviceBus.bind(integrationEventName)
        }
        // Listen for binded IntegrationEvents
        mediatorK.serviceBus.consume( mediatorK )
    }

}