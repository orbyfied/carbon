package net.orbyfied.carbon.util.message;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.slice.Literal;
import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.Styled;
import net.orbyfied.carbon.util.message.writer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Sliced implements Slice {

    // writer map
    public static final Map<EffectKey<Sliced, ?>, ComponentWriter<Sliced, ?>> WRITERS = ComponentWriter.writers(Sliced.class,
            String.class, (CWF<Sliced, String>) Sliced::writeString
    );

    //////////////////////////////////////

    /**
     * The parts that this sliced message
     * is made up of.
     */
    protected List<Slice> slices = new ArrayList<>();

    public Sliced() { }

    public Sliced(List<Slice> slices) {
        this.slices = slices;
    }

    /* ---- Sliced ---- */

    public <T extends Slice> Sliced append(Supplier<T> supplier, Consumer<T> consumer) {
        T it = supplier.get();
        slices.add(it);
        consumer.accept(it);
        return this;
    }

    /////////////////////////////////

    public static String writeString(Context ctx, MessageWriter<String, ?> writer,
                                     Sliced sliced) {
        StringBuilder b = new StringBuilder();
        for (Slice slice : sliced.slices)
            b.append(slice.serialize(ctx, writer, String.class));
        return b.toString() + ChatColor.RESET;

    }

}
