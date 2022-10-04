package com.cillu.mediator

import com.cillu.mediator.configuration.Mediator
import com.cillu.mediator.configuration.MediatorConfig
import com.cillu.mediator.registry.ServiceRegistry
import com.cillu.mediator.messagebrokers.MessageBrokerFactory
import com.cillu.mediator.services.*
import com.sksamuel.hoplite.ConfigLoader


open class TestBase()
{
    internal var COMMAND_CONFIG_FILE_SUCCESS = "/mediatork-command.yml"
    internal var COMMAND_CONFIG_FILE_SUCCESS_NOSERVICE = "/mediatork-command-noservice.yml"
    internal var COMMAND_CONFIG_FILE_MISSING = "/mediatork-command-missing.yml"
    internal var COMMAND_CONFIG_FILE_DUPLICATE = "/mediatork-command-duplicate.yml"
    internal var COMMAND_CONFIG_FILE_MISSING_SERVICE = "/mediatork-command-missing-service.yml"
    internal val QUERY_CONFIG_FILE_SUCCESS = "/mediatork-query.yml"
    internal val QUERY_CONFIG_FILE_MISSING_SERVICE = "/mediatork-query-missing-service.yml"
    internal val QUERY_CONFIG_FILE_NOSERVICE = "/mediatork-query-noservice.yml"
    internal val QUERY_CONFIG_FILE_MISSING = "/mediatork-query-missing.yml"
    internal val QUERY_CONFIG_FILE_EXCEPTION = "/mediatork-query-exception.yml"
    internal val QUERY_CONFIG_FILE_DUPLICATE = "/mediatork-query-duplicate.yml"
    internal var DOMAIN_EVENTS_CONFIG_FILE_SUCCESS = "/mediatork-domain.yml"
    internal var DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE = "/mediatork-domain-noservice.yml"
    internal var DOMAIN_EVENTS_CONFIG_FILE_EXCEPTION= "/mediatork-domain-exception.yml"
    internal var DOMAIN_EVENTS_CONFIG_FILE_SUCCESS_MISSING = "/mediatork-domain-missing-service.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS = "/mediatork-integration.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_MISSING_SERVICE = "/mediatork-integration-missing-service.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_MISSING = "/mediatork-integration-missing.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_EXCEPTION = "/mediatork-integration-exception.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_DUPLICATE = "/mediatork-integration-duplicate.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS_NOSERVICE = "/mediatork-integration-noservice.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_LOCAL = "/mediatork-integration-local.yml"
    internal var FULL_PATH = "/mediatork-fullpath.yml"

    fun getMediatorK(configFile: String, registerService: Boolean = true): IMediator {
        val mediatorConfiguration = ConfigLoader().loadConfigOrThrow<MediatorConfig>(configFile)
        val messageBroker = MessageBrokerFactory.getMessageBroker(mediatorConfiguration);
        if (registerService) {
            val servicesRegistry = ServiceRegistry()
            servicesRegistry.register(ITestService::class.java, TestService())
            servicesRegistry.register(TestService2::class.java, TestService2())
            servicesRegistry.register(IAnotherService::class.java, AnotherService())
            return MediatorK.create(mediatorConfiguration.mediator.paths, messageBroker, servicesRegistry);
        } else {
            return MediatorK.create(mediatorConfiguration.mediator.paths, messageBroker);
        }
    }
}