package com.cillu.mediator.commands.config.missing

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.CreateCommand
import com.cillu.mediator.services.*

@CommandHandler
class MissingServiceCommandHandler(): ICommandHandler<CreateCommand>, Exception(){

    @Inject
    lateinit var missingService: IMissingService

    @Inject
    lateinit var anotherService: AnotherService

    override fun handle(command: CreateCommand): Item {
        var item = Item.create(command.name);
        missingService.sayhello()
        anotherService.sayhello();
        return item
    }
}