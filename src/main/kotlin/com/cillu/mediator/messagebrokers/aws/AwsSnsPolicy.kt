package com.cillu.mediator.messagebrokers.aws

class AwsSnsPolicy {
    companion object {
        public fun getFilterPolicy(integrationEventNames: List<String>): String {
            var filterPolicy = "{\"routingKey\": [";
            integrationEventNames.forEach {
                filterPolicy = filterPolicy.plus("\"").plus(it).plus("\"").plus(",")
            }
            filterPolicy = filterPolicy.dropLast(1).plus("]}")
            println(filterPolicy)
            return filterPolicy
        }
    }
}