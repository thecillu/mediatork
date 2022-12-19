package com.cillu.mediator.registry

import com.cillu.mediator.IMediator
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.domainevents.IDomainEventHandler
import com.cillu.mediator.exceptions.ComponentInjectionException
import com.cillu.mediator.exceptions.MissingComponentException
import com.cillu.mediator.exceptions.MissingServiceException
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import com.cillu.mediator.integrationevents.IIntegrationEventHandler
import com.cillu.mediator.queries.IQueryHandler
import org.reflections.Reflections

class ServiceRegistry {
    var servicesRegistry: MutableMap<Class<*>, Any> = mutableMapOf()

    fun register( serviceClass: Class<*>,serviceInstance: Any ){
        servicesRegistry[serviceClass] =  serviceInstance
    }

    fun isRegistered(parameterType: Class<*>): Boolean{
        return servicesRegistry.containsKey(parameterType)
    }

    fun getService(parameterType: Class<*>): Any?{
        return servicesRegistry[parameterType]
    }

    internal fun injectField(reflections: Reflections, mediator: IMediator) {
        servicesRegistry[IMediator::class.java] = mediator
        val annotatedFields = reflections.getFieldsAnnotatedWith(Inject::class.java)
        for (field in annotatedFields) {
            if (field.declaringClass.genericInterfaces.isEmpty() || field.declaringClass.genericInterfaces.size > 1)
                throw ComponentInjectionException(field.name)
            //for the handlers search for class instead of interface
            var classInstance: Any? = when (field.declaringClass.interfaces[0].name) {
                IQueryHandler::class.java.name,
                ICommandHandler::class.java.name,
                IDomainEventHandler::class.java.name,
                IIntegrationEventHandler::class.java.name -> {
                    if (servicesRegistry[field.declaringClass] == null)
                        throw MissingComponentException(field.declaringClass.interfaces[0].typeName)
                    servicesRegistry[field.declaringClass]
                }
                else -> {
                    if (servicesRegistry[field.declaringClass.interfaces[0]] == null)
                        throw MissingComponentException(field.declaringClass.interfaces[0].typeName)
                    servicesRegistry[field.declaringClass.interfaces[0]]
                }
            }

            if (servicesRegistry[field.type] == null)
                throw MissingComponentException(field.type.typeName)
            var fieldInstance = servicesRegistry[field.type]
            field.set(classInstance, fieldInstance)
        }
    }

    fun removeAll(){
        servicesRegistry = mutableMapOf()
    }
}