package net.orbyfied.carbon.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.orbyfied.carbon.util.nbt.CompoundObjectTag;

import java.util.function.Supplier;

public class Nbt {

    @SuppressWarnings({"unchecked", "typesRaw"})
    public static <T> CompoundObjectTag<T> getOrCreateObject(CompoundTag tag,
                                                             String key,
                                                             Supplier<Object> objectSupplier) {
        if (!tag.contains(key)) {
            Object obj = objectSupplier.get();
            CompoundObjectTag tag1 = new CompoundObjectTag(obj.getClass(), obj);
            tag.put(key, tag1);
            return tag1;
        } else {
            return ((CompoundObjectTag<T>) tag.get(key));
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
