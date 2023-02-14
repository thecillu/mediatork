package com.cillu.mediator.exceptions

class QueryHandlerNotFoundException(val query: String):
Exception("No QueryHandlers found for the query ${query}\")")
