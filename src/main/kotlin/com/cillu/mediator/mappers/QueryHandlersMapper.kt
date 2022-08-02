package com.cillu.mediator.mappers

import com.cillu.mediator.Event
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.exceptions.CommandHandlerConfigurationException
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import mu.KotlinLogging
import org.reflections.Reflections

class QueryHandlersMapper(reflections: Reflections) {
    private val logger = KotlinLogging.logger {}
    private var queryHandlers: MutableMap<String, Class<QueryHandler>> = HashMap()

    init {
        register(reflections)
    }

    internal fun getHandlers():  MutableMap<String, Class<QueryHandler>> {
        return queryHandlers;
    }

    private fun register(reflections: Reflections) {
        // Registering QueryHandlers
        val annotatedQueryClasses: Set<Class<QueryHandler>> =
            reflections.getTypesAnnotatedWith(QueryHandler()) as Set<Class<QueryHandler>>
        for (annotatedClass in annotatedQueryClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1) throw Exception(
                "Annotated @QueryHandler Class must be implement IQueryHandler interface"
            )
            var query = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (queryHandlers.containsKey(query)) throw QueryHandlerConfigurationException(query)
            queryHandlers[query] = annotatedClass
            logger.info("Registered handler ${annotatedClass} for query ${query}")
        }
    }
}