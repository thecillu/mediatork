package com.cillu.mediator.queries

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.exceptions.QueryHandlerConfigurationException
import com.cillu.mediator.exceptions.QueryHandlerNotFoundException
import com.cillu.mediator.queries.config.success.TestQuery2Handler
import com.cillu.mediator.queries.config.success.TestQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.queries.domain.TestQuery2
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows



class TestQueries: TestBase() {

    private val TEST_QUERY_CLASS = "com.cillu.mediator.queries.domain.TestQuery"
    private val TEST_QUERY2_CLASS = "com.cillu.mediator.queries.domain.TestQuery2"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getQueryHandlers();
        assert( handlers.size == 2)
        assert( handlers[TEST_QUERY_CLASS] == TestQueryHandler::class.java)
        assert( handlers[TEST_QUERY2_CLASS] ==  TestQuery2Handler::class.java)
    }

    @Test
    fun missingConfig() {
        assertThrows<QueryHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_MISSING)
            mediatorK.send(TestQuery())
        }
    }

    @Test
    fun duplicateConfig() {
        assertThrows<QueryHandlerConfigurationException> {
            getMediatorK(QUERY_CONFIG_FILE_DUPLICATE)
        }
    }

    @Test
    fun queryTest() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<TestItem> = mediatorK.send(TestQuery()) as List<TestItem>
        assert( items.size == 1)
    }

    @Test
    fun queryTest2() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<TestItem> = mediatorK.send(TestQuery2()) as List<TestItem>
        assert( items.size == 2)
    }
}