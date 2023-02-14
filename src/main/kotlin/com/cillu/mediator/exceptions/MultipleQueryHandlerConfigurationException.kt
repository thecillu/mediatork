package com.cillu.mediator.exceptions

class MultipleQueryHandlerConfigurationException(val query: String):
    Exception("Found multiple QueryHandlers for the query ${query}\")")