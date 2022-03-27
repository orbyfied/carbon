package com.github.orbyfied.carbon_core.test.command;

import com.github.orbyfied.carbon.command.*;
import com.github.orbyfied.carbon.command.impl.SystemParameterType;
import org.junit.jupiter.api.Test;

public class CommandEngineTest {

    final CommandEngine engine = new CommandEngine() {
        @Override
        protected void registerPlatform(Node root) { }

        @Override
        protected void unregisterPlatform(Node root) { }
    };

    final Suggestions suggestions = new Suggestions() {
        @Override
        public Suggestions suggest(Object o) {
            System.out.println("| Suggestion: \"" + o + "\"");
            return this;
        }

        @Override
        public Suggestions unsuggest(Object o) {
            return this;
        }
    };

        /* ---- 1 ---- */

    @Test
    public void testSimpleOnlyExecuteCommand() {
        // register command "test"
        Node command = new Node("test", null, null);
        command
                .makeExecutable((ctx, cmd) -> System.out.println(ctx.getArg("ghello:hello").toString()))
                .childExecutable("ghello", (ctx, cmd) -> System.out.println(ctx.getArgs()))
                .childParameter("hello",  SystemParameterType.LONG)
                .childParameter("hello2", SystemParameterType.INT)
                .childExecutable("print", (ctx, cmd) -> System.out.println(ctx.getArg("ghello:hello2").toString()));

        CommandDebug.traverseAndPrintChildren(command, 0);

        // execute commands
        Context result = engine.register(command).dispatch(
                null,
                "test ghello 55 0b10 print",
                null,
                null
        );

        System.out.println("+ MESSAGE: " + result.getIntermediateText());
    }

}
