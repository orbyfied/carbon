package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.StringJoiner;

/**
 * A runtime reference to an object which is
 * converted to a compound tag with the data
 * prefixed by the class name to save it persistently.
 */
public class CompoundObjectTag<T> implements Tag {

    public static final String SERIALIZER_FIELD_NAME = "NBT_TAG_SERIALIZER";
    public static final String CLASS_NAME_TAG        = "_class";

    private static CompoundTagSerializer getSerializer(Class<?> klass) {
        CompoundTagSerializer<?> serializer = serializerCache.get(klass);
        if (serializer != null)
            return serializer;

        try {
            Field f = klass.getField("NBT_TAG_SERIALIZER");
            f.setAccessible(true);
            serializer = (CompoundTagSerializer<?>) f.get(null);
        } catch (Exception e) {
            System.out.println("For class: " + klass);
            e.printStackTrace();
            return null;
        }

        serializerCache.put(klass, serializer);

        return serializer;
    }

    public static CompoundObjectTag<?> loadFromCompound(CompoundTag ctag) {
        try {
            // get class
            String className = ctag.getString(CLASS_NAME_TAG);
            Class<?> klass;
            try {
                klass = Class.forName(className);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            // get serializer
            CompoundTagSerializer<?> serializer = getSerializer(klass);

            Object obj = serializer.read(null, ctag);

            // create and return tag
            CompoundObjectTag<?> tag = new CompoundObjectTag(obj);
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
            return "CompoundObjectTag";
        }

        @Override
        public String getPrettyName() {
            return "CompoundObject";
        }
    };

    ///////////////////////////////////////

    private static final HashMap<Class<?>, CompoundTagSerializer<?>> serializerCache = new HashMap<>();
    T obj;

    public CompoundObjectTag(T obj) {
        this.obj  = obj;
    }

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

    @SuppressWarnings("unchecked")
    public CompoundTag writeCompoundTag(CompoundTag tag) {
        try {
            getSerializer(obj.getClass()).write(null, tag, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tag;
    }

    @SuppressWarnings("unchecked")
    public CompoundTag writeCompoundTag() {
        CompoundTag tag = new CompoundTag();

        try {
            getSerializer(obj.getClass()).write(null, tag, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(DataOutput output) throws IOException {
        // get class
        Class<?> klass = obj.getClass();

        // write class name
        String name = klass.getName();
        output.writeUTF(name);

        // get serializer
        CompoundTagSerializer<T> serializer = (CompoundTagSerializer<T>) getSerializer(klass);

        // compile object
        CompoundTag tag = new CompoundTag();
        serializer.write(output, tag, obj);

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
    public @NotNull Tag copy() {
        return new CompoundObjectTag<>(obj);
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
        return "0x" + Integer.toHexString(System.identityHashCode(obj)) + " '" + obj.toString() + "'";
    }

}
