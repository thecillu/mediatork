package com.cillu.mediator.queries

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.queries.config.noservice.TestNoServiceQueryHandler
import com.cillu.mediator.queries.config.success.TestQuery2Handler
import com.cillu.mediator.queries.config.success.TestQueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.queries.domain.TestQuery2
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class TestQueries: TestBase() {

    private val TEST_QUERY_CLASS = "com.cillu.mediator.queries.domain.TestQuery"
    private val TEST_QUERY2_CLASS = "com.cillu.mediator.queries.domain.TestQuery2"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getQueryHandlers();
        assert( handlers.size == 2)
        assert( handlers[TEST_QUERY_CLASS]!!::class.java == TestQueryHandler::class.java)
        assert( handlers[TEST_QUERY2_CLASS]!!::class.java  ==  TestQuery2Handler::class.java)
    }

    @Test
    fun missingConfig() {
        assertThrows<QueryHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_MISSING)
            mediatorK.send(TestQuery(UUID.randomUUID()))
        }
    }

    @Test
    fun duplicateConfig() {
        assertThrows<MultipleQueryHandlerConfigurationException> {
            getMediatorK(QUERY_CONFIG_FILE_DUPLICATE)
        }
    }

    @Test
    fun queryTest() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<TestItem> = mediatorK.send(TestQuery(UUID.randomUUID())) as List<TestItem>
        assert( items.size == 1)
    }

    @Test
    fun queryTest2() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<TestItem> = mediatorK.send(TestQuery2(UUID.randomUUID())) as List<TestItem>
        assert( items.size == 2)
    }

    @Test
    fun missingService() {
        assertThrows<MissingComponentException> {
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_MISSING_SERVICE)
        }
    }


    @Test
    fun wrongInterface() {
        assertThrows<QueryHandlerConfigurationException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_EXCEPTION)
            mediatorK.send(TestQuery(UUID.randomUUID()))
        }
    }

    @Test
    fun wrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            val mediatorK = getMediatorK(QUERY_CONFIG_WRONG_CONSTRUCTOR)
        }
    }
}