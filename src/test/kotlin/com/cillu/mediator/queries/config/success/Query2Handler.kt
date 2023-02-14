package com.cillu.mediator.queries.config.success

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.IQueryHandler
import com.cillu.mediator.queries.domain.TestQuery2

@QueryHandler
class Query2Handler: IQueryHandler<TestQuery2> {

    override fun handle(query: TestQuery2): List<Item> {
        var items = listOf(Item.create("Item1"), Item.create("Item2"))
        return items
    }
}