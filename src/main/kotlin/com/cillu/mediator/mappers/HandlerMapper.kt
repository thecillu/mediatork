package com.cillu.mediator.mappers

import com.cillu.mediator.exceptions.MissingServiceException
import com.cillu.mediator.registry.ServiceRegistry

abstract class HandlerMapper {

    fun checkServices(annotatedClass: Class<*>, servicesRegistry: ServiceRegistry)
    {
        //check registered services
        val parameters =  annotatedClass.constructors[0].parameters
        for (parameter in parameters) {
            if (!servicesRegistry.isRegistered(parameter.type))
                throw MissingServiceException(parameter.type.name, annotatedClass::class.java.name)
        }
    }
}