package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.CompoundTag;

public interface CompoundTagSerializer<T> {

    void write(CompoundTag tag, T v);

    T read(CompoundTag tag);

}
