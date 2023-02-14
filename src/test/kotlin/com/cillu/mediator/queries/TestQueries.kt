@file:Suppress("UNCHECKED_CAST")

package com.cillu.mediator.queries

import com.cillu.mediator.Base
import com.cillu.mediator.Item
import com.cillu.mediator.exceptions.*
import com.cillu.mediator.queries.config.success.Query2Handler
import com.cillu.mediator.queries.config.success.QueryHandler
import com.cillu.mediator.queries.domain.TestQuery
import com.cillu.mediator.queries.domain.TestQuery2
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class TestQueries: Base() {

    private val TEST_QUERY_CLASS = "com.cillu.mediator.queries.domain.TestQuery"
    private val TEST_QUERY2_CLASS = "com.cillu.mediator.queries.domain.TestQuery2"

    @Test
    fun testSuccessConfig() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getQueryHandlers()
        assert( handlers.size == 2)
        assert( handlers[TEST_QUERY_CLASS]!!::class.java == QueryHandler::class.java)
        assert( handlers[TEST_QUERY2_CLASS]!!::class.java  ==  Query2Handler::class.java)
    }

    @Test
    fun testMissingConfig() {
        assertThrows<QueryHandlerNotFoundException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_MISSING)
            mediatorK.send(TestQuery(UUID.randomUUID()))
        }
    }

    @Test
    fun testDuplicateConfig() {
        assertThrows<MultipleQueryHandlerConfigurationException> {
            getMediatorK(QUERY_CONFIG_FILE_DUPLICATE)
        }
    }

    @Test
    fun testQuery() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<Item> = mediatorK.send(TestQuery(UUID.randomUUID())) as List<Item>
        assert( items.size == 1)
    }

    @Test
    fun testQuery2() {
        val mediatorK = getMediatorK(QUERY_CONFIG_FILE_SUCCESS)
        val items: List<Item> = mediatorK.send(TestQuery2(UUID.randomUUID())) as List<Item>
        assert( items.size == 2)
    }

    @Test
    fun testMissingService() {
        assertThrows<MissingComponentException> {
            getMediatorK(QUERY_CONFIG_FILE_MISSING_SERVICE)
        }
    }


    @Test
    fun testWrongInterface() {
        assertThrows<QueryHandlerConfigurationException> {
            //scan the wrong packages, without any commandHandler
            val mediatorK = getMediatorK(QUERY_CONFIG_FILE_EXCEPTION)
            mediatorK.send(TestQuery(UUID.randomUUID()))
        }
    }

    @Test
    fun testWrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
             getMediatorK(QUERY_CONFIG_WRONG_CONSTRUCTOR)
        }
    }
}