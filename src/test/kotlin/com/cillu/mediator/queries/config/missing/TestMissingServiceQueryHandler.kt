package com.cillu.mediator.queries.config.missing

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.services.*

@QueryHandler
class TestMissingServiceQueryHandler(): IQueryHandler<TestQuery> {

    @Inject lateinit var missingService: IMissingService

    override fun handle(query: TestQuery): List<TestItem> {
        var items = listOf(TestItem.create("Item1"));
        return items
    }
}