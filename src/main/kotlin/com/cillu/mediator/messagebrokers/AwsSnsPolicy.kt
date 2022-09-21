package com.cillu.mediator.messagebrokers

class AwsSnsPolicy {
    companion object {
        public fun getFilterPolicy(integrationEventNames: List<String>): String {
            var filterPolicy = "{\"event\": [";
            integrationEventNames.forEach {
                filterPolicy = filterPolicy.plus("\"").plus(it).plus("\"").plus(",")
            }
            filterPolicy = filterPolicy.dropLast(1).plus("]}")
            println(filterPolicy)
            return filterPolicy
        }
    }
}