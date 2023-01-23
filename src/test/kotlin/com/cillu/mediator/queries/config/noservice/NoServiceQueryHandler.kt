package com.cillu.mediator.queries.config.noservice

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery

@QueryHandler
class NoServiceQueryHandler(): IQueryHandler<TestQuery> {

    override fun handle(query: TestQuery): List<Item> {
        var items = listOf(Item.create("Item1"));
        return items
    }
}