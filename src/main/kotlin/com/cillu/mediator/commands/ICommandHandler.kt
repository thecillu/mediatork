package com.cillu.mediator.commands

interface ICommandHandler<T: Command> {
    fun handle(command: T): Any?
}