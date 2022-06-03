package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

/**
 * A runtime reference to an object which is
 * converted to a compound tag with the data
 * prefixed by the class name to save it persistently.
 */
public class CompoundObjectTag<T> implements Tag {

    /**
     * The name of the field that compound tag serializable
     * classes should define and initialize with their serializer.
     */
    public static final String SERIALIZER_FIELD_NAME = "NBT_TAG_SERIALIZER";

    /**
     * The key used to store the class name in the
     * serialized compound tags.
     */
    public static final String CLASS_NAME_TAG = "_class";

    /**
     * The serializers loaded mapped by class.
     * Used mainly as a cache, but can also be
     * used to define or overwrite serializers
     * for other classes.
     */
    public static final HashMap<Class<?>, CompoundTagSerializer<?>> LOADED_SERIALIZERS = new HashMap<>();

    /**
     * Tries to retrieve the serializer for the
     * given class. First checks the loaded serializers.
     * If it contains it, no further logic is done and
     * the result is returned. Otherwise, the serializer
     * is gotten from the static serializer field in the class.
     * @see CompoundObjectTag#SERIALIZER_FIELD_NAME
     * @see CompoundObjectTag#LOADED_SERIALIZERS
     * @param klass The class to get a serializer for.
     * @return The serializer instance or null if something failed.
     */
    private static CompoundTagSerializer getSerializer(Class<?> klass) {
        CompoundTagSerializer<?> serializer = LOADED_SERIALIZERS.get(klass);
        if (serializer != null)
            return serializer;

        try {
            // try to get field
            Field f = klass.getField(SERIALIZER_FIELD_NAME);
            f.setAccessible(true);

            // try to get serializer from field
            serializer = (CompoundTagSerializer<?>) f.get(null);
        } catch (Exception e) {
            // handle exception
            throw new IllegalStateException(
                    "Failed to load serializer for: " + klass,
                    e
            );
        }

        // check if a serializer could be
        // loaded real quick
        if (serializer == null)
            throw new IllegalStateException(
                    "No serializer was provided for: " + klass
            );

        // cache for later
        LOADED_SERIALIZERS.put(klass, serializer);

        return serializer;
    }

    /**
     * Tries to load an object from the
     * given compound tag.
     * @param ctag The compound tag to load.
     * @return The object tag or null if it failed.
     */
    public static CompoundObjectTag<?> loadFromCompound(CompoundTag ctag) {
        try {
            // get class
            String className = ctag.getString(CLASS_NAME_TAG);
            if ("null".equals(className)) // handle null
                return new CompoundObjectTag<>(null);
            Class<?> klass;
            try {
                klass = Class.forName(className);
            } catch (Exception e) {
                System.out.println("Invalid class name: " + className);
                return null;
            }

            // get serializer
            CompoundTagSerializer<?> serializer = getSerializer(klass);

            Object obj = serializer.read(ctag);

            // create and return tag
            CompoundObjectTag<?> tag = new CompoundObjectTag(obj);
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the provided compound object tag to
     * the specified compound tag and returns it
     * (the compound tag) back to you.
     * @param otag The object tag to write.
     * @param ctag The compound tag to write to.
     * @return The (same) compound tag.
     */
    @SuppressWarnings("unchecked")
    public static CompoundTag saveToCompound(CompoundObjectTag<?> otag, CompoundTag ctag) {
        // get object
        Object obj = otag.getObject();
        // handle null
        if (obj == null) {
            ctag.putString(CLASS_NAME_TAG, "null");
            return ctag;
        }

        // get class
        Class<?> objKlass = obj.getClass();

        // set class name
        ctag.putString(CLASS_NAME_TAG, objKlass.getName());

        try {
            // serialize
            CompoundTagSerializer serializer = getSerializer(objKlass);
            serializer.write(ctag, obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return ctag;
    }

    /**
     * Tag type.
     */
    public static final TagType<CompoundObjectTag<?>> TAG_TYPE = new TagType<>() {
        @Override
        public CompoundObjectTag<?> load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            CompoundTag ctag = CompoundTag.TYPE.load(input, depth, tracker);
            return loadFromCompound(ctag);
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            return visitor.visitEnd();
        }

        @Override
        public void skip(DataInput input, int count) throws IOException {
            for (int i = 0; i < count; i++)
                skip(input);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            // skip compound tag
            CompoundTag.TYPE.skip(input);
        }

        @Override
        public String getName() {
            return CompoundTag.TYPE.getName();
        }

        @Override
        public String getPrettyName() {
            return CompoundTag.TYPE.getPrettyName();
        }
    };

    ///////////////////////////////////////

    /**
     * The instance this tag holds.
     */
    T obj;

    public CompoundObjectTag(T obj) {
        this.obj  = obj;
    }

    /* Getters and setters. */

    public Class<?> getObjectType() {
        return obj.getClass();
    }

    public T getObject() {
        return obj;
    }

    public CompoundObjectTag<T> setObject(T o) {
        this.obj = o;
        return this;
    }

    /**
     * Attempts to serialize this object tag
     * into the provided compound tag.
     * @param tag The output compound tag.
     * @return The output compound tag.
     */
    @SuppressWarnings("unchecked")
    public CompoundTag writeCompoundTag(CompoundTag tag) {
        try {
            getSerializer(obj.getClass()).write(tag, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tag;
    }

    /**
     * Attempts to serialize this object tag
     * into a new compound tag.
     * @see CompoundObjectTag#writeCompoundTag(CompoundTag)
     * @return The output compound tag.
     */
    @SuppressWarnings("unchecked")
    public CompoundTag writeCompoundTag() {
        CompoundTag tag = new CompoundTag();

        try {
            getSerializer(obj.getClass()).write(tag, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tag;
    }

    /* ---- NBT ---- */

    @Override
    @SuppressWarnings("unchecked")
    public void write(DataOutput output) throws IOException {
        // get class
        Class<?> klass = obj.getClass();
        // compile object
        CompoundTag tag = new CompoundTag();
        saveToCompound(this, tag);

        // write object
        tag.write(output);
    }

    @Override
    public byte getId() {
        return CompoundTag.TAG_COMPOUND;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TAG_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Tag copy() {
        return new CompoundObjectTag<>(getSerializer(obj.getClass()).copy(obj));
    }

    @Override
    public void accept(TagVisitor visitor) {
        // TODO: what
    }

    @Override
    public StreamTagVisitor.@NotNull ValueResult accept(StreamTagVisitor visitor) {
        return visitor.visitEnd(); // TODO: what
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundObjectTag<?> that = (CompoundObjectTag<?>) o;
        return Objects.equals(obj, that.obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }

}
