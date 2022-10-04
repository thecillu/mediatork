package com.cillu.mediator.queries.config.exception

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.AnotherService
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService
import com.cillu.mediator.services.MissingService

@QueryHandler
class TestWrongInterfaceQueryHandler() {

   fun handle(query: TestQuery): List<TestItem> {
        var items = listOf(TestItem.create("Item1"));
        return items
    }
}