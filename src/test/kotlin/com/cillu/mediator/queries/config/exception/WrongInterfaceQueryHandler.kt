package com.cillu.mediator.queries.config.exception

import com.cillu.mediator.Item
import com.cillu.mediator.annotations.QueryHandler
import com.cillu.mediator.queries.domain.TestQuery

@QueryHandler
class WrongInterfaceQueryHandler() {

   fun handle(query: TestQuery): List<Item> {
        var items = listOf(Item.create("Item1"));
        return items
    }
}