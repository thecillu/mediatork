package com.cillu.mediator.mappers

import com.cillu.mediator.MediatorK
import com.cillu.mediator.annotations.IntegrationEventHandler
import com.cillu.mediator.exceptions.IntegrationEventHandlerConfigurationException
import com.cillu.mediator.exceptions.MultipleIntegrationEventHandlerConfigurationException
import com.cillu.mediator.exceptions.NoEmptyHandlerConstructor
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging

class IntegrationEventHandlersMapper internal constructor(mediatorK: MediatorK, servicesRegistry: ServiceRegistry): HandlerMapper() {
    private val logger = KotlinLogging.logger {}
    private var integrationEventHandlers: MutableMap<String, IIntegrationEventHandler<*>> = HashMap()

    init {
        register(mediatorK, servicesRegistry)
    }

    internal fun getHandlers(): MutableMap<String, IIntegrationEventHandler<*>> {
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
            if (annotatedClass.constructors[0].parameters.isNotEmpty())
                throw NoEmptyHandlerConstructor(annotatedClass::class.java.name)
            var integrationEventName = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (integrationEventHandlers.containsKey(integrationEventName)) throw MultipleIntegrationEventHandlerConfigurationException(integrationEventName)
            var classInstance = annotatedClass.constructors[0].newInstance() as IIntegrationEventHandler<*>
            integrationEventHandlers[integrationEventName] = classInstance
            logger.info("Registered handler ${annotatedClass} for IntegrationEvent ${integrationEventName}")
            servicesRegistry.register(annotatedClass, classInstance)
            // Bind the integration event with routingKey = integrationEventName
            if (mediatorK.messageBroker != null) mediatorK.messageBroker.bind(integrationEventName)
        }
        // Listen for binded IntegrationEvents
        if (mediatorK.messageBroker != null) mediatorK.messageBroker.consume( mediatorK )
    }

}