package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.CompoundTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface CompoundTagSerializer<T> {

    void write(DataOutput out, CompoundTag tag, T v) throws IOException;

    T read(DataInput in, CompoundTag tag) throws IOException;

}
