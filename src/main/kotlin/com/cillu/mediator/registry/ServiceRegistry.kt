package com.cillu.mediator.registry

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
}