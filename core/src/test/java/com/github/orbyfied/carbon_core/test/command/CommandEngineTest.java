package com.github.orbyfied.carbon_core.test.command;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Executable;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.impl.SystemParameterType;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class CommandEngineTest {

    final CommandEngine engine = new CommandEngine() { };

        /* ---- 1 ---- */

    @Test
    public void testSimpleOnlyExecuteCommand() {
        // register command "test"
        Node command = new Node("test", null, null);
        command
                .makeExecutable((ctx, cmd) -> System.out.println(ctx.getArg("test:hello").toString()))
                .childParameter("hello",  SystemParameterType.LONG)
                .childParameter("hello2", SystemParameterType.INT)
                .childExecutable("print", (ctx, cmd) -> System.out.println(ctx.getArg("test:hello2").toString()));

        // execute commands
        Context result = engine.register(command).dispatch(null,
                "test 55 0b10 print",
                null,
                null
        );
    }

}
