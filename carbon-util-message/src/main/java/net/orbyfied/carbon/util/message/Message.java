package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.StringReader;
import net.orbyfied.carbon.util.message.slice.Literal;
import net.orbyfied.carbon.util.message.slice.Placeholder;
import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.writer.CWF;
import net.orbyfied.carbon.util.message.writer.ComponentWriter;
import net.orbyfied.carbon.util.message.writer.EffectKey;

import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Message extends Sliced {

    // writer map
    public static final Map<EffectKey<Message, ?>, ComponentWriter<Message, ?>> WRITERS = ComponentWriter.writers(Message.class,
            String.class, (CWF<Message, String>) Sliced::writeString
    );

    /////////////////////////////////////

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
