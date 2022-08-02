package com.cillu.mediator.mappers

import com.cillu.mediator.Event
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.exceptions.CommandHandlerConfigurationException
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import mu.KotlinLogging
import org.reflections.Reflections

class CommandHandlersMapper(reflections: Reflections) {
    private val logger = KotlinLogging.logger {}
    private var commandHandlers: MutableMap<String, Class<CommandHandler>> = HashMap()

    init {
        register(reflections)
    }

    internal fun getHandlers():  MutableMap<String, Class<CommandHandler>> {
        return commandHandlers;
    }

    private fun register(reflections: Reflections) {
        // Registering CommandHandlers
        val annotatedClasses: Set<Class<CommandHandler>> =
            reflections.getTypesAnnotatedWith(CommandHandler()) as Set<Class<CommandHandler>>
        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1) throw Exception(
                "Annotated @CommandHandler Class must be implement ICommandHandler interface"
            )
            var command = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (commandHandlers.containsKey(command)) throw CommandHandlerConfigurationException(command)
            commandHandlers[command] = annotatedClass
            logger.info("Registered handler ${annotatedClass} for command ${command}")
        }
    }
}