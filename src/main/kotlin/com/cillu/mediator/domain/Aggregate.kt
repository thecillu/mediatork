package com.cillu.mediator.domain

import com.cillu.mediator.domainevents.DomainEvent


open class Aggregate() {

    @Transient
    private var domainEvents: MutableList<DomainEvent> = mutableListOf();


    fun addDomainEvent(domainEvent: DomainEvent){
        domainEvents.add(domainEvent);
    }

    fun getDomainEvents(): List<DomainEvent> {
        return  domainEvents;
    }

    fun removeDomainEvents() {
        domainEvents.clear()
    }
}


