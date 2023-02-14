package com.cillu.mediator.commands

import com.cillu.mediator.Base
import com.cillu.mediator.Item
import com.cillu.mediator.commands.config.success.CreateCommandHandler
import com.cillu.mediator.commands.config.success.CreateCommand2Handler
import com.cillu.mediator.commands.domain.CreateCommand
import com.cillu.mediator.exceptions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class TestCommands: Base(){

    internal val TEST_COMMAND_CLASS = "com.cillu.mediator.commands.domain.CreateCommand"
    internal val TEST_COMMAND2_CLASS = "com.cillu.mediator.commands.domain.CreateCommand2"
    internal val TEST_COMMAND_NOSERVICE_CLASS = "com.cillu.mediator.commands.domain.TestNoServiceCommand"

    @Test
    fun testSuccessConfig() {
        val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getCommandsHandlers()
        assert( handlers.size == 2)
        assert( handlers[TEST_COMMAND_CLASS]!!::class.java == CreateCommandHandler::class.java)
        assert( handlers[TEST_COMMAND2_CLASS]!!::class.java ==  CreateCommand2Handler::class.java)
    }

    @Test
     fun testMissingConfig() {
         assertThrows<CommandHandlerNotFoundException> {
             //scan the wrong packages, without any commandHandler
             val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_MISSING)
             mediatorK.send(CreateCommand( UUID.randomUUID(), "TestCommand"))
         }
     }

     @Test
     fun testDuplicateConfig() {
         assertThrows<MultipleCommandHandlerConfigurationException> {
             getMediatorK(COMMAND_CONFIG_FILE_DUPLICATE)
         }
     }

     @Test
     fun testCommandTest() {
         val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
         val item: Item = mediatorK.send(CreateCommand( UUID.randomUUID(), "TestCommand")) as Item
         assert( item.name == "TestCommand")
     }

    @Test
    fun testMissingService() {
        assertThrows<MissingComponentException> {
            getMediatorK(COMMAND_CONFIG_FILE_MISSING_SERVICE)
            //mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
        }
    }

    @Test
    fun testWrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            getMediatorK(COMMAND_CONFIG_FILE_WRONG_CONSTRUCTOR)
            //mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
        }
    }
}