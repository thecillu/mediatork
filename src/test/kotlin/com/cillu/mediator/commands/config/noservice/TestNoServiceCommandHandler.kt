package com.cillu.mediator.commands.config.noservice

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestNoServiceCommand

@CommandHandler
class TestNoServiceCommandHandler(): ICommandHandler<TestNoServiceCommand>, Exception(){

    override fun handle(command: TestNoServiceCommand): TestItem {
        var item = TestItem.create(command.name);
        return item
    }
}