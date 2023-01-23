package com.cillu.mediator.queries.config.error

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.MyTestService

@QueryHandler
class AnotherQueryHandler: IQueryHandler<TestQuery> {

    @Inject
    lateinit var testService: MyTestService

    @Inject
    lateinit var anotherService: IAnotherService


    override fun handle(query: TestQuery): List<Item> {
        var items = listOf(Item.create("Test"));
        testService.sayhello()
        anotherService.sayhello()
        return items
    }
}