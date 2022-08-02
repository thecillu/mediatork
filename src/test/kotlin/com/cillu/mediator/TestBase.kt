package com.cillu.mediator

import com.cillu.mediator.services.*


open class TestBase()
{
    internal var COMMAND_CONFIG_FILE_SUCCESS = "/mediatork-command.yml"
    internal var COMMAND_CONFIG_FILE_MISSING = "/mediatork-command-missing.yml"
    internal var COMMAND_CONFIG_FILE_DUPLICATE = "/mediatork-command-duplicate.yml"
    internal val QUERY_CONFIG_FILE_SUCCESS = "/mediatork-query.yml"
    internal val QUERY_CONFIG_FILE_MISSING = "/mediatork-query-missing.yml"
    internal val QUERY_CONFIG_FILE_DUPLICATE = "/mediatork-query-duplicate.yml"
    internal var DOMAIN_EVENTS_CONFIG_FILE_SUCCESS = "/mediatork-domain.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_SUCCESS = "/mediatork-integration.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_MISSING = "/mediatork-integration-missing.yml"
    internal var INTEGRATION_EVENTS_CONFIG_FILE_DUPLICATE = "/mediatork-integration-duplicate.yml"

    fun getMediatorK(configFile: String): MediatorK {
        var mediator = MediatorK(configFile);
        mediator.registerService(ITestService::class.java, TestService())
        mediator.registerService(TestService2::class.java, TestService2())
        mediator.registerService(IAnotherService::class.java, AnotherService())
        return mediator
    }
}