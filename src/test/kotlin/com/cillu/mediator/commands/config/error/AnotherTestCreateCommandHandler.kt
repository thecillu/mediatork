package com.cillu.mediator.commands.config.error

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.TestCreateCommand
import com.cillu.mediator.services.AnotherService
import com.cillu.mediator.services.ITestService
import com.cillu.mediator.services.TestService2

@CommandHandler
class AnotherTestCreateCommandHandler(var testService2: TestService2, var anotherService: AnotherService): ICommandHandler<TestCreateCommand>, Exception(){

    override fun handle(command: TestCreateCommand): TestItem {
        var item = TestItem.create(command.name);
        testService2.sayhello()
        anotherService.sayhello()
        //repository.save(order)
        return item
    }
}