package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.StringReader;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Message extends Sliced {

    public <T extends Slice> Message append(Supplier<T> supplier, Consumer<T> consumer) {
        super.append(supplier, consumer);
        return this;
    }

    /////////////////////////////////////////////

    public static Message compile(String text) {

        // initialize/prepare for parsing
        StringReader reader = new StringReader(text, 0);

        return null; // TODO

    }

}
