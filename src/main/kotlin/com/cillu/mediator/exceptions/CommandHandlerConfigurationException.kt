package com.cillu.mediator.exceptions

class CommandHandlerConfigurationException(val handler: String):
    Exception("Annotated @CommandHandler $handler Class must implement ICommandHandler interface")