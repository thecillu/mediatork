package com.cillu.mediator.commands.config.success

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.CreateCommand
import com.cillu.mediator.services.ITestService

@CommandHandler
class CreateCommandHandler: ICommandHandler<CreateCommand>, Exception(){


    @Inject
    lateinit var testService: ITestService

    override fun handle(command: CreateCommand): Item {
        var item = Item.create(command.name);
        testService.sayhello()
        return item
    }
}