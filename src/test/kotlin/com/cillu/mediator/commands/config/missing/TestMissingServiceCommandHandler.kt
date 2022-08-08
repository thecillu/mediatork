package com.cillu.mediator.commands.config.missing

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestCreateCommand
import com.cillu.mediator.services.*

@CommandHandler
class TestMissingServiceCommandHandler(var missingService: IMissingService, var anotherService: AnotherService): ICommandHandler<TestCreateCommand>, Exception(){

    override fun handle(command: TestCreateCommand): TestItem {
        var item = TestItem.create(command.name);
        missingService.sayhello()
        anotherService.sayhello();
        return item
    }
}