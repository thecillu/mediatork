package com.cillu.mediator

import com.cillu.mediator.messagebrokers.IMessageBroker
import com.cillu.mediator.registry.ServiceRegistry

class MediatorFactory {
    companion object {
        /*fun getDefaultMediator(configFile: String): IMediator {
            val mediatorConfig = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
            val messageBroker = MessageBrokerFactory.getMessageBroker(mediatorConfig)
            return MediatorK.create(mediatorConfig.mediator.paths, messageBroker, servicesRegistry)
        }*/

        fun getDefaultMediator(
            configPaths: List<String>,
            serviceRegistry: ServiceRegistry,
            messageBroker: IMessageBroker): IMediator {
            if  (serviceRegistry == null) MediatorK.create(configPaths, messageBroker, ServiceRegistry())
            return MediatorK.create(configPaths, messageBroker, serviceRegistry!!)
        }

        fun getDefaultMediator(mediatorPaths: List<String>): IMediator {
            return MediatorK.create(mediatorPaths, null, ServiceRegistry())
        }

        fun getDefaultMediator(mediatorPaths: List<String>, serviceRegistry: ServiceRegistry): IMediator {
            return MediatorK.create(mediatorPaths, null, serviceRegistry)
        }

        fun getDefaultMediator(mediatorPaths: List<String>, messageBroker: IMessageBroker): IMediator {
            return MediatorK.create(mediatorPaths, messageBroker, ServiceRegistry())
        }
    }
}