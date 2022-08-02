package com.cillu.mediator.queries.config.success

import com.cillu.mediator.TestItem
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery2

@QueryHandler
class TestQuery2Handler: IQueryHandler<TestQuery2> {

    override fun handle(query: TestQuery2): List<TestItem> {
        var items = listOf(TestItem.create("Item1"), TestItem.create("Item2"));
        return items
    }
}