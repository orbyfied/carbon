package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.io.IOException;

/**
 * A serializer for type T which should define
 * the functionality needed to serialize and
 * deserialize a value of type T to and from a
 * compound tag respectively.
 * @param <T> The value type.
 */
public interface CompoundTagSerializer<T> {

    /**
     * Should write the provided value
     * into the provided output tag.
     * @param tag The output tag.
     * @param v The value.
     */
    void write(CompoundTag tag, T v) throws IOException;

    /**
     * Should read a value of type T
     * from the provided input tag.
     * @param tag The input tag.
     * @return The value.
     */
    T read(CompoundTag tag) throws IOException;

    /**
     * Should (possibly deep-) clone the object
     * to be used in {@link Tag#copy()}.
     * @param original The original object.
     * @return The clone.
     */
    T copy(T original);

}
