package com.cillu.mediator.mappers

import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.exceptions.MultipleQueryHandlerConfigurationException
import com.cillu.mediator.exceptions.NoEmptyHandlerConstructor
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.registry.ServiceRegistry
import mu.KotlinLogging
import org.reflections.Reflections

class QueryHandlersMapper internal constructor(reflections: Reflections, servicesRegistry: ServiceRegistry): HandlerMapper(){
    private val logger = KotlinLogging.logger {}
    private var queryHandlers: MutableMap<String, IQueryHandler<*>> = HashMap()

    init {
        register(reflections, servicesRegistry)
    }

    internal fun getHandlers():  MutableMap<String, IQueryHandler<*>> {
        return queryHandlers
    }

    private fun register(reflections: Reflections, servicesRegistry: ServiceRegistry) {
        // Registering QueryHandlers
        val annotatedQueryClasses: Set<Class<QueryHandler>> =
            reflections.getTypesAnnotatedWith(QueryHandler()) as Set<Class<QueryHandler>>
        for (annotatedClass in annotatedQueryClasses) {
            if (annotatedClass.genericInterfaces.isEmpty() || annotatedClass.genericInterfaces.size > 1)
                throw QueryHandlerConfigurationException(annotatedClass.name)
            if (annotatedClass.constructors[0].parameters.isNotEmpty())
                throw NoEmptyHandlerConstructor(annotatedClass::class.java.name)
            var query = annotatedClass.genericInterfaces[0].typeName.substringAfter("<").substringBefore(">")
            if (queryHandlers.containsKey(query)) throw MultipleQueryHandlerConfigurationException(query)
            var classInstance = annotatedClass.constructors[0].newInstance() as IQueryHandler<*>
            queryHandlers[query] = classInstance
            servicesRegistry.register(annotatedClass, classInstance)
            logger.info("Registered handler ${annotatedClass} for query ${query}")
        }
    }
}