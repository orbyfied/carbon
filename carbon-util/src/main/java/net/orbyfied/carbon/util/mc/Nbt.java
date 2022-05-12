package net.orbyfied.carbon.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.orbyfied.carbon.util.nbt.CompoundObjectTag;

import java.util.function.Supplier;

public class Nbt {

    @SuppressWarnings({"unchecked", "typesRaw"})
    public static <T> CompoundObjectTag<T> getOrCreateObject(CompoundTag stag,
                                                             String key,
                                                             Supplier<Object> objectSupplier) {
        if (!stag.contains(key)) {
            Object obj = objectSupplier.get();
            CompoundObjectTag tag = new CompoundObjectTag(obj);
            stag.put(key, tag);
            return tag;
        } else {
            Tag tag = stag.get(key);
            if (tag instanceof CompoundObjectTag cot)
                return cot;
            // we need to load the object tag
            if (!(tag instanceof CompoundTag))
                throw new IllegalStateException("Tag '" + key + "' is not a compound tag and has to be loaded.");
            CompoundTag ctag = (CompoundTag) tag;
            CompoundObjectTag cot = null;
            try {
                cot = CompoundObjectTag.loadFromCompound(ctag);
            } catch (Exception e) {
                throw new RuntimeException("Exception while loading object tag '" + key + "'", e);
            }

            if (cot == null)
                return null;
            stag.put(key, cot); // put newly loaded tag
            return cot;
        }
    }

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
