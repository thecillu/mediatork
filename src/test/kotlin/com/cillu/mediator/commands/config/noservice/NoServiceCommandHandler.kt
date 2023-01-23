package com.cillu.mediator.commands.config.noservice

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.NoServiceCommand

@CommandHandler
class NoServiceCommandHandler(): ICommandHandler<NoServiceCommand>, Exception(){

    override fun handle(command: NoServiceCommand): Item {
        var item = Item.create(command.name);
        return item
    }
}