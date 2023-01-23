package com.cillu.mediator.commands.config.error

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.CommandHandler
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.commands.ICommandHandler
import com.cillu.mediator.commands.domain.CreateCommand
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.MyTestService2

@CommandHandler
class AnotherCreateCommandHandler(): ICommandHandler<CreateCommand>, Exception(){

    @Inject
    lateinit var testService2: MyTestService2

    @Inject
    lateinit var anotherService: IAnotherService

    override fun handle(command: CreateCommand): Item {
        var item = Item.create(command.name);
        testService2.sayhello()
        anotherService.sayhello()
        //repository.save(order)
        return item
    }
}