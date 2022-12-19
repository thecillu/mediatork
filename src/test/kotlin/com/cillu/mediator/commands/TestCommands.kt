package com.cillu.mediator.commands

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.commands.config.noservice.TestNoServiceCommandHandler
import com.cillu.mediator.commands.config.success.TestCreateCommandHandler
import com.cillu.mediator.commands.config.success.TestCreateCommand2Handler
import com.cillu.mediator.commands.domain.TestCreateCommand
import com.cillu.mediator.commands.domain.TestNoServiceCommand
import com.cillu.mediator.exceptions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class TestCommands: TestBase(){

    internal val TEST_COMMAND_CLASS = "com.cillu.mediator.commands.domain.TestCreateCommand"
    internal val TEST_COMMAND2_CLASS = "com.cillu.mediator.commands.domain.TestCreateCommand2"
    internal val TEST_COMMAND_NOSERVICE_CLASS = "com.cillu.mediator.commands.domain.TestNoServiceCommand"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getCommandsHandlers();
        assert( handlers.size == 2)
        assert( handlers[TEST_COMMAND_CLASS]!!::class.java == TestCreateCommandHandler::class.java)
        assert( handlers[TEST_COMMAND2_CLASS]!!::class.java ==  TestCreateCommand2Handler::class.java)
    }

    @Test
     fun missingConfig() {
         assertThrows<CommandHandlerNotFoundException> {
             //scan the wrong packages, without any commandHandler
             val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_MISSING)
             mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
         }
     }

     @Test
     fun duplicateConfig() {
         assertThrows<MultipleCommandHandlerConfigurationException> {
             getMediatorK(COMMAND_CONFIG_FILE_DUPLICATE)
         }
     }

     @Test
     fun commandTest() {
         val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
         val item: TestItem = mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand")) as TestItem;
         assert( item.name == "TestCommand")
     }

    @Test
    fun missingService() {
        assertThrows<MissingComponentException> {
            val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_MISSING_SERVICE)
            //mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
        }
    }

    @Test
    fun wrongConstructor() {
        assertThrows<NoEmptyHandlerConstructor> {
            val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_WRONG_CONSTRUCTOR)
            //mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand"))
        }
    }
}