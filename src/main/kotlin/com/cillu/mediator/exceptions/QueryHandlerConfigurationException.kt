package com.cillu.mediator.exceptions

class QueryHandlerConfigurationException(val query: String):
    Exception("Found multiple QueryHandlers for the query ${query}\")") {
}