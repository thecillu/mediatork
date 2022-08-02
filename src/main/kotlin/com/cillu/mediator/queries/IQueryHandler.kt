package com.cillu.mediator.queries

interface IQueryHandler<T: Query> {
    fun handle(query: T): Any
}