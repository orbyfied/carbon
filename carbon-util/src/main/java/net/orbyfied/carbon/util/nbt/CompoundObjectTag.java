package net.orbyfied.carbon.util.nbt;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * A runtime reference to an object which is
 * converted to a compound tag with the data
 * prefixed by the class name to save it persistently.
 */
public class CompoundObjectTag<T> implements Tag {

    private static CompoundTagSerializer<?> getSerializer(Class<?> klass) {
        CompoundTagSerializer<?> serializer = serializerCache.get(klass);
        if (serializer != null)
            return serializer;

        try {
            Field f = klass.getDeclaredField("COMPOUND_TAG_SERIALIZER");
            f.setAccessible(true);
            serializer = (CompoundTagSerializer<?>) f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serializer;
    }

    public static final TagType<CompoundObjectTag<?>> TAG_TYPE = new TagType<>() {
        @Override
        public CompoundObjectTag<?> load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            // get class
            String className = input.readUTF();
            Class<?> klass;
            try {
                klass = Class.forName(className);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            // get serializer
            CompoundTagSerializer<?> serializer = getSerializer(klass);

            // read object
            int refStat = input.readInt();
            if (refStat == 0)
                return new CompoundObjectTag<>(klass);
            CompoundTag ctag = CompoundTag.TYPE.load(input, depth + 1, tracker);
            Object obj = serializer.read(ctag);

            // create and return tag
            CompoundObjectTag<?> tag = new CompoundObjectTag(klass, obj);
            return tag;
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            return null;
        }

        @Override
        public void skip(DataInput input, int count) throws IOException {
            for (int i = 0; i < count; i++)
                skip(input);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            // skip class name
            input.readUTF();

            // skip ref status
            int i = input.readInt();
            if (i == 0)
                return;

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

    final Class<T> klass;
    CompoundTagSerializer<T> serializer;
    T obj;

    public CompoundObjectTag(Class<T> type) {
        this(type, null);
    }

    public CompoundObjectTag(Class<T> type, T obj) {
        this.klass = type;
        this.obj  = obj;
        loadSerializer();
    }

    @SuppressWarnings("unchecked")
    private void loadSerializer() {
        serializer = (CompoundTagSerializer<T>) getSerializer(klass);
    }

    public Class<?> getObjectType() {
        return klass;
    }

    public Object getObject() {
        return obj;
    }

    public CompoundObjectTag<T> setObject(T o) {
        this.obj = o;
        return this;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        // write class name
        String name = klass.getName();
        output.writeUTF(name);

        // write reference status
        if (obj == null) {
            output.writeInt(0);
            return;
        } else output.writeInt(1);

        // compile object
        CompoundTag tag = new CompoundTag();
        serializer.write(tag, obj);

        // write object
        tag.write(output);
    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TAG_TYPE;
    }

    @Override
    public @NotNull Tag copy() {
        return new CompoundObjectTag<>(klass, obj);
    }

    @Override
    public void accept(TagVisitor visitor) {
        // TODO: what
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor visitor) {
        return null; // TODO: what
    }

}
