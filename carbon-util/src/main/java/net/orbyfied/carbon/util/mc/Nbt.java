package net.orbyfied.carbon.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.orbyfied.carbon.util.nbt.CompoundObjectTag;

import java.util.function.Supplier;

/**
 * Utilities for working with
 * Minecraft NBT tags.
 */
public class Nbt {

    /**
     * Tries to get an object tag with the provided
     * key from the source tag.
     *
     * If doesn't exist, it gets the object from the supplier
     * and creates a new {@link CompoundObjectTag}
     * with it. Then it puts it into the source tag
     * under the correct key and returns.
     *
     * If a tag with that key already exists, it will
     * first check if it has already been loaded as an object,
     * and if so, return it immediately. Otherwise, it will load
     * the object tag, replace the existent one with the new
     * object tag and return afterwards.
     *
     * @param sourceTag The source tag to get from.
     * @param key The key in the source tag.
     * @param objectSupplier The object value supplier.
     * @param <T> The object type.
     * @return The newly created or retrieved tag.
     */
    @SuppressWarnings({"unchecked", "typesRaw"})
    public static <T> CompoundObjectTag<T> getOrCreateObject(CompoundTag sourceTag,
                                                             String key,
                                                             Supplier<Object> objectSupplier) {
        if (!sourceTag.contains(key)) {
            // get object
            Object obj = objectSupplier.get();
            // create and put tag
            CompoundObjectTag tag = new CompoundObjectTag(obj);
            sourceTag.put(key, tag);
            // return
            return tag;
        } else {
            // try to get tag
            Tag tag = sourceTag.get(key);

            // if we already loaded this tag, we return it
            if (tag instanceof CompoundObjectTag cot)
                return cot;

            // otherwise we need to load the object tag
            if (!(tag instanceof CompoundTag))
                throw new IllegalStateException("Tag '" + key + "' is not a compound tag and has to be loaded.");
            CompoundTag ctag = (CompoundTag) tag;
            CompoundObjectTag cot;
            try {
                cot = CompoundObjectTag.loadFromCompound(ctag);
            } catch (Exception e) {
                // pass through error
                throw new RuntimeException("Exception while loading object tag '" + key + "'", e);
            }

            if (cot == null) // error occurred
                return null;
            sourceTag.put(key, cot); // put newly loaded tag
            return cot;
        }
    }

    /**
     * Tries to get a compound tag with the provided
     * key from the source tag.
     *
     * If it doesn't exist, it creates a new one,
     * puts it in the source tag and then returns it.
     * Otherwise it just returns the tag.
     *
     * @param sourceTag The source tag.
     * @param key The key in the source tag.
     * @return The newly created or retrieved tag.
     */
    public static CompoundTag getOrCreateCompound(CompoundTag sourceTag, String key) {
        if (!sourceTag.contains(key, CompoundTag.TAG_COMPOUND)) {
            CompoundTag newTag = new CompoundTag();
            sourceTag.put(key, newTag);
            return newTag;
        } else {
            return sourceTag.getCompound(key);
        }
    }

    /**
     * Tries to get a list tag with the provided
     * key from the source tag.
     *
     * If it doesn't exist, it creates a new one,
     * puts it in the source tag and then returns it.
     * Otherwise it just returns the tag.
     *
     * @param sourceTag The source tag.
     * @param key The key in the source tag.
     * @return The newly created or retrieved tag.
     */
    public static ListTag getOrCreateList(CompoundTag sourceTag, String key, int type) {
        if (!sourceTag.contains(key, CompoundTag.TAG_COMPOUND)) {
            ListTag newTag = new ListTag();
            sourceTag.put(key, newTag);
            return newTag;
        } else {
            return sourceTag.getList(key, type);
        }
    }

}
