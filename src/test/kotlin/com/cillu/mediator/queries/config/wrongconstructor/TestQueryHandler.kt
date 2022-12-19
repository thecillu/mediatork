package com.cillu.mediator.queries.config.wrongconstructor

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.*

@QueryHandler
class TestQueryHandler(var wrongParam: Any): IQueryHandler<TestQuery> {

    @Inject
    lateinit var testService: ITestService

    @Inject
    lateinit var anotherService: IAnotherService

    override fun handle(query: TestQuery): List<TestItem> {
        var items = listOf(TestItem.create("Item1"));
        testService.sayhello()
        anotherService.sayhello()
        return items
    }
}