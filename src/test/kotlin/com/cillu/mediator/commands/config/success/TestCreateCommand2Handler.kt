package com.cillu.mediator.commands.config.success

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestCreateCommand2
import com.cillu.mediator.services.ITestRepository
import com.cillu.mediator.services.ITestService

@CommandHandler
class TestCreateCommand2Handler: ICommandHandler<TestCreateCommand2>, Exception(){

    @Inject
    lateinit var testRepository: ITestRepository

    override fun handle(command: TestCreateCommand2): TestItem {
        var item = TestItem.create(command.name);
        testRepository.sayhello()
        return item
    }
}