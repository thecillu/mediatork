package com.cillu.mediator

import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.registry.ServiceRegistry

class MediatorFactory {
    companion object {
        fun getDefaultMediator(
            configPaths: List<String>,
            serviceRegistry: ServiceRegistry?,
            messageBroker: IMessageBroker?): IMediator {
            if  (serviceRegistry == null) return MediatorK.create(configPaths, messageBroker, ServiceRegistry())
            return MediatorK.create(configPaths, messageBroker, serviceRegistry)
        }

        fun getDefaultMediator(configPaths: List<String>, messageBroker: IMessageBroker): IMediator {
            return getDefaultMediator(configPaths, serviceRegistry = null, messageBroker)
        }

        fun getDefaultMediator(configPaths: List<String>, serviceRegistry: ServiceRegistry): IMediator {
            return getDefaultMediator(configPaths, serviceRegistry, messageBroker = null)
        }
    }
}