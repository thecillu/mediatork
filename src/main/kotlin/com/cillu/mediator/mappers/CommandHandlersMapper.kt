package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.exceptions.CommandHandlerConfigurationException
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class CommandHandlersMapper(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper() {
    private val logger = KotlinLogging.logger {}
    private var commandHandlers: MutableMap<String, Class<CommandHandler>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers():  MutableMap<String, Class<CommandHandler>> {
        return commandHandlers;
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering CommandHandlers
        val annotatedClasses: Set<Class<CommandHandler>> =
            reflections.getTypesAnnotatedWith(CommandHandler()) as Set<Class<CommandHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw CommandHandlerConfigurationException(annotatedClass.name)
            var command = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (commandHandlers.containsKey(command)) throw CommandHandlerConfigurationException(command)
            //check registered services
            checkServices(annotatedClass, servicesRegistry)
            commandHandlers[command] = annotatedClass
            logger.info("Registered handler ${annotatedClass} for command ${command}")
        }
    }

}