package com.cillu.mediator.exceptions

class QueryHandlerConfigurationException(val handler: String):
    Exception("Annotated @QueryHandler $handler Class must implement IQueryHandler interface") {
}