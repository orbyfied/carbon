package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.CompoundTag;

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

}
