package com.cillu.mediator.exceptions

class MissingServiceException(val parameterType: String, handler:String):
    Exception( "Found any registered service for parameter ${parameterType} in handler ${handler}. Add the service to the serviceRegistry map") {
}