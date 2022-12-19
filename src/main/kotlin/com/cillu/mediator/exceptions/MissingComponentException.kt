package com.cillu.mediator.exceptions

class MissingComponentException(val componentType: String):
    Exception( "Impossible to found a component instance for ${componentType}. Please add the component to the serviceRegistry") {
}