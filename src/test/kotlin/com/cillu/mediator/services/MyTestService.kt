package com.cillu.mediator.services

import com.cillu.mediator.annotations.Inject

class MyTestService: ITestService {

    @Inject
    lateinit var testRepository: ITestRepository

    override fun sayhello(){
        testRepository.sayhello()
    }
}