package com.cillu.mediator.commands.config.success

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestCreateCommand2

@CommandHandler
class TestCreateCommand2Handler: ICommandHandler<TestCreateCommand2>, Exception(){

    override fun handle(command: TestCreateCommand2): TestItem {
        var item = TestItem.create(command.name);
        //repository.save(order)
        return item
    }
}