package com.cillu.mediator.queries.config.success

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.AnotherService
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService

@QueryHandler
class TestQueryHandler(var testService: ITestService, var anotherService: IAnotherService): IQueryHandler<TestQuery> {

    override fun handle(query: TestQuery): List<TestItem> {
        var items = listOf(TestItem.create("Item1"));
        testService.sayhello()
        anotherService.sayhello()
        return items
    }
}