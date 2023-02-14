package com.cillu.mediator.commands.config.success

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.CreateCommand2
import com.cillu.mediator.services.*

@CommandHandler
class CreateCommand2Handler: ICommandHandler<CreateCommand2>, Exception(){

    @Inject
    lateinit var testRepository: ITestRepository

    override fun handle(command: CreateCommand2): Item {
        var item = Item.create(command.name)
        testRepository.sayhello()
        return item
    }
}