package com.cillu.mediator.queries.config.wrongconstructor

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.*

@QueryHandler
class QueryHandler(var wrongParam: Any): IQueryHandler<TestQuery> {

    @Inject
    lateinit var testService: ITestService

    @Inject
    lateinit var anotherService: IAnotherService

    override fun handle(query: TestQuery): List<Item> {
        var items = listOf(Item.create("Item1"))
        testService.sayhello()
        anotherService.sayhello()
        return items
    }
}