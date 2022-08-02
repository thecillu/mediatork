package com.cillu.mediator.commands

import com.cillu.mediator.TestBase
import com.cillu.mediator.TestItem
import com.cillu.mediator.commands.config.success.TestCreateCommandHandler
import com.cillu.mediator.commands.config.success.TestCreateCommand2Handler
import com.cillu.mediator.commands.domain.TestCreateCommand
import com.cillu.mediator.exceptions.CommandHandlerConfigurationException
import com.cillu.mediator.exceptions.CommandHandlerNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class TestCommands: TestBase(){

    internal val TEST_COMMAND_CLASS = "com.cillu.mediator.commands.domain.TestCreateCommand"
    internal val TEST_COMMAND2_CLASS = "com.cillu.mediator.commands.domain.TestCreateCommand2"

    @Test
    fun successConfig() {
        val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
        val handlers = mediatorK.getCommandsHandlers();
        assert( handlers.size == 2)
        assert( handlers[TEST_COMMAND_CLASS] == TestCreateCommandHandler::class.java)
        assert( handlers[TEST_COMMAND2_CLASS] ==  TestCreateCommand2Handler::class.java)
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
         assertThrows<CommandHandlerConfigurationException> {
             getMediatorK(COMMAND_CONFIG_FILE_DUPLICATE)
         }
     }

     @Test
     fun commandTest() {
         val mediatorK = getMediatorK(COMMAND_CONFIG_FILE_SUCCESS)
         val item: TestItem = mediatorK.send(TestCreateCommand( UUID.randomUUID(), "TestCommand")) as TestItem;
         assert( item.name == "TestCommand")
     }

}