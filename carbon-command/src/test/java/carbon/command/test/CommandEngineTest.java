package carbon.command.test;

import net.orbyfied.carbon.command.*;
import net.orbyfied.carbon.command.impl.SystemParameterType;
import org.junit.jupiter.api.Test;

public class CommandEngineTest {

    final CommandEngine engine = new CommandEngine() {
        @Override
        protected void registerPlatform(Node root) { }

        @Override
        protected void unregisterPlatform(Node root) { }

        @Override
        public void enablePlatform() { }

        @Override
        public void disablePlatform() { }
    };

    final SuggestionAccumulator suggestions = new SuggestionAccumulator() {
        @Override
        public SuggestionAccumulator suggest(Object o) {
            System.out.println("| Suggestion: \"" + o + "\"");
            return this;
        }

        @Override
        public SuggestionAccumulator unsuggest(Object o) {
            return this;
        }
    };

        /* ---- 1 ---- */

    @Test
    public void testSimpleOnlyExecuteCommand() {
        // register command "test"
        Node command = new Node("test", null, null);
        command
                .makeExecutable((ctx, cmd) -> System.out.println(ctx.getSymbol("ghello:hello").toString()))
                .childExecutable("ghello", (ctx, cmd) -> System.out.println(ctx.getSymbols()))
                .childParameter("hello",  SystemParameterType.LONG)
                .childParameter("hello2", SystemParameterType.INT)
                .childExecutable("print", (ctx, cmd) -> System.out.println(ctx.getSymbol("ghello:hello2").toString()));

        CommandDebug.traverseAndPrintChildren(command, 0);

        // execute commands
        Context result = engine.register(command).dispatch(
                null,
                "test ghello 55 0b10 print",
                null,
                null
        );

        System.out.println("+ MESSAGE: " + result.intermediateText());
    }

}
