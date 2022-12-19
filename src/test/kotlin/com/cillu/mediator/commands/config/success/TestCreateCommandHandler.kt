package com.cillu.mediator.commands.config.success

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestCreateCommand
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService
import com.cillu.mediator.services.TestService2

@CommandHandler
class TestCreateCommandHandler: ICommandHandler<TestCreateCommand>, Exception(){


    @Inject
    lateinit var testService: ITestService

    override fun handle(command: TestCreateCommand): TestItem {
        var item = TestItem.create(command.name);
        testService.sayhello()
        return item
    }
}