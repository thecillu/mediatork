package com.cillu.mediator.exceptions

class MutipleQueryHandlerConfigurationException(val query: String):
    Exception("Found multiple QueryHandlers for the query ${query}\")") {
}