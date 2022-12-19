package com.cillu.mediator.exceptions

class ComponentInjectionException(field: String):
    Exception("Wrong Configuration for field ${field}. Did you inject the interface?") {
}