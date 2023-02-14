package com.cillu.mediator.services

import com.cillu.mediator.annotations.Inject
import com.cillu.mediator.services.ITestRepository
import com.cillu.mediator.services.ITestService

class MyTestService: ITestService {

    @Inject
    lateinit var testRepository: ITestRepository

    override fun sayhello(){
        testRepository.sayhello()
    }
}