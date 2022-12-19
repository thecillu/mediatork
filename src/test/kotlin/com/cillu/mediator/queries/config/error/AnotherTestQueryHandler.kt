package com.cillu.mediator.queries.config.error

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.AnotherService
import com.cillu.mediator.services.IAnotherService
import com.cillu.mediator.services.ITestService
import com.cillu.mediator.services.TestService

@QueryHandler
class AnotherTestQueryHandler: IQueryHandler<TestQuery> {

    @Inject
    lateinit var testService: TestService

    @Inject
    lateinit var anotherService: IAnotherService


    override fun handle(query: TestQuery): List<TestItem> {
        var items = listOf(TestItem.create("Test"));
        testService.sayhello()
        anotherService.sayhello()
        return items
    }
}