package com.github.orbyfied.carbon_core.test.command;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Executable;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.impl.SystemParameterType;
import org.junit.jupiter.api.Test;

public class CommandEngineTest {

    final CommandEngine engine = new CommandEngine() { };

        /* ---- 1 ---- */

    @Test
    public void testSimpleOnlyExecuteCommand() {
        // register command "test"
        Node command = new Node("test", null);
        command
                .addComponent(new Executable(command)
                        .setExecutor((ctx, cmd) -> {
                            System.out.println(
                                    ctx.getArg("test:hello").toString() + " " +
                                    ctx.getArg("test:hello2")
                            );
                        }), null
                )
                .addParameterChild("hello",  SystemParameterType.LONG)
                .addParameterChild("hello2", SystemParameterType.INT);

        // execute commands
        engine.register(command).dispatch(null, "test 55 0b1", null);
    }

}
