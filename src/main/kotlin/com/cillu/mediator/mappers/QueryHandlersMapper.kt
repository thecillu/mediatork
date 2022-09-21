package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.exceptions.MutipleQueryHandlerConfigurationException
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class QueryHandlersMapper(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper(){
    private val logger = KotlinLogging.logger {}
    private var queryHandlers: MutableMap<String, Class<QueryHandler>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers():  MutableMap<String, Class<QueryHandler>> {
        return queryHandlers;
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering QueryHandlers
        val annotatedQueryClasses: Set<Class<QueryHandler>> =
            reflections.getTypesAnnotatedWith(QueryHandler()) as Set<Class<QueryHandler>>
        for (annotatedClass in annotatedQueryClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw QueryHandlerConfigurationException(annotatedClass.name)
            var query = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (queryHandlers.containsKey(query)) throw MutipleQueryHandlerConfigurationException(query)
            //check registered services
            checkServices(annotatedClass, servicesRegistry)
            queryHandlers[query] = annotatedClass
            logger.info("Registered handler ${annotatedClass} for query ${query}")
        }
    }
}