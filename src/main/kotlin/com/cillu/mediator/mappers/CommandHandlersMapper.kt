package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.exceptions.CommandHandlerConfigurationException
import com.cillu.mediator.exceptions.MultipleCommandHandlerConfigurationException
import com.cillu.mediator.exceptions.NoEmptyHandlerConstructor
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class CommandHandlersMapper internal constructor(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper() {
    private val logger = KotlinLogging.logger {}
    private var commandHandlers: MutableMap<String, ICommandHandler<*>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers():  MutableMap<String, ICommandHandler<*>> {
        return commandHandlers
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering CommandHandlers
        val annotatedClasses: Set<Class<CommandHandler>> =
            reflections.getTypesAnnotatedWith(CommandHandler()) as Set<Class<CommandHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw CommandHandlerConfigurationException(annotatedClass.name)
            if (annotatedClass.constructors[0].parameters.isNotEmpty())
                throw NoEmptyHandlerConstructor(annotatedClass::class.java.name)
            var command = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (commandHandlers.containsKey(command)) throw MultipleCommandHandlerConfigurationException(command)
            var classInstance = annotatedClass.constructors[0].newInstance() as ICommandHandler<*>
            commandHandlers[command] = classInstance
            servicesRegistry.register(annotatedClass, classInstance)
            logger.info("Registered handler ${annotatedClass} for command ${command}")
        }
    }

}