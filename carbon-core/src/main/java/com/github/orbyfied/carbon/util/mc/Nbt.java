package com.github.orbyfied.carbon.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class Nbt {

    public static CompoundTag getOrCreateCompound(CompoundTag tag, String key) {
        if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
            CompoundTag newTag = new CompoundTag();
            tag.put(key, newTag);
            return newTag;
        } else {
            return tag.getCompound(key);
        }
    }

    public static ListTag getOrCreateList(CompoundTag tag, String key, int type) {
        if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
            ListTag newTag = new ListTag();
            tag.put(key, newTag);
            return newTag;
        } else {
            return tag.getList(key, type);
        }
    }

}
