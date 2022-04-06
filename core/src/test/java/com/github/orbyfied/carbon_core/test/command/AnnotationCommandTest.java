package com.github.orbyfied.carbon_core.test.command;

import com.github.orbyfied.carbon.command.*;
import com.github.orbyfied.carbon.command.annotation.*;
import org.junit.jupiter.api.Test;

public class AnnotationCommandTest {

    final CommandEngine engine = new CommandEngine() {
        @Override
        protected void registerPlatform(Node root) { }

        @Override
        protected void unregisterPlatform(Node root) { }
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
    public void testBasicAnnotationCommand() {
        Node n = new BaseAnnotationProcessor(engine, new MyCommand())
                .compile()
                .register()
                .getBase();

        CommandDebug.traverseAndPrintChildren(n, 0);

        engine.dispatch(
                null,
                "test sussy \"a b c\" 69 ",
                suggestions,
                null
        );
    }

    @BaseCommand(name = "test")
    public static class MyCommand {

        @Subcommand("sussy <system:string s>")
        public void sussy(Context ctx, Node cmd,

                          @CommandParameter("s") String s) {
            System.out.println("fucking string: " + s);
        }

        @Subcommand("sussy <system:string s> <system:int sus> baka")
        public void baka(Context ctx, Node cmd,

                         @CommandParameter("s") String s,
                         @CommandParameter("sus") Integer sus) {
            System.out.println("fucking string: " + s + ", sus: " + sus);
        }

        @SubInitializer
        public void sussy(Node cmd) {
            System.out.println("INITIALIZED: " + cmd.getName());
        }

    }

}
