package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

import java.lang.reflect.Field;
import java.util.*;

public interface ComponentWriter<S, T> extends CWF<S, T> {

    Class<T> getResultType();

    Class<S> getSourceType();

    /////////////////////////////////////////////

    HashMap<EffectKey, ComponentWriter> loadedWriters = new HashMap<>();
    String WRITERS_FIELD_NAME = "WRITERS";

    static ComponentWriter loadWriter(Class cl, ComponentWriter writer) {
        loadedWriters.put(new EffectKey(cl, writer.getResultType()), writer);
        return writer;
    }

    static void loadWriters(Class sClass, Class tClass) {
        // try to load writers from class
        try {
            // get field
            Field f = sClass.getDeclaredField(WRITERS_FIELD_NAME);
            f.setAccessible(true);

            // get content
            Map<Class, ComponentWriter> writerMap = (Map<Class, ComponentWriter>) f.get(null);

            // load all
            writerMap.forEach((targ, writ) ->
                    loadWriter(sClass, writ)
            );
        } catch (NoSuchFieldException e) {
            // class does not define writers
            throw new IllegalArgumentException(
                    "Component type '" + sClass.getName() + "' does not define any writers, " +
                            "should be defined in field: " + WRITERS_FIELD_NAME
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static <S, T> ComponentWriter<S, T> getWriter(Class<T> resType,
                                                  Class<S> sClass) {
        // create key
        EffectKey<S, T> key = new EffectKey<>(sClass, resType);

        // try and retrieve
        // try to get writer from cache
        ComponentWriter<S, T> cw;
        if ((cw = loadedWriters.get(key)) == null)
            throw new IllegalArgumentException("No writer loaded for key: " + key);

        // return
        return cw;
    }

    @SuppressWarnings("unchecked")
    static <S, T> T serializeStatic(Context context,
                                    Class<T> resType,
                                    S src,
                                    MessageWriter<T, ?> writer) {
        // get source type
        final Class sClass = src.getClass();

        // get component writer
        final ComponentWriter<S, T> cw = getWriter(resType, sClass);

        if (cw == null)
            return null; // couldnt find serializer

        // serialize and return
        T res = cw.serialize(context, writer, src);
        return res;
    }

    static <S> Map<EffectKey<S, ?>, ComponentWriter<S, ?>> writers(Class<S> sourceClass,
                                                                   Object... params) {
        Class<Object> resType = null;
        Map<EffectKey<S, ?>, ComponentWriter<S, ?>> acc = new HashMap<>();

        int l = params.length;
        int p = 0;
        for (int i = 0; i < l; i++) {
            Object v = params[i];
            switch (p) {
                // is first in tuple of parameters
                // specifies the target type
                case 0 -> resType = (Class<Object>) v;
                // second in the tuple of parameters
                // specifies the function and registers it
                case 1 -> {
                    // get component writer function
                    final CWF cwf = (CWF) v;

                    final Class<Object> fResType = resType;
                    ComponentWriter<S, ?> writer = new ComponentWriter<>() {
                        @Override
                        public Class<Object> getResultType() {
                            return fResType;
                        }

                        @Override
                        public Class<S> getSourceType() {
                            return sourceClass;
                        }

                        @Override
                        public Object serialize(Context ctx, MessageWriter<Object, ?> writer, S source) {
                            return cwf.serialize(ctx, writer, source);
                        }
                    };

                    // load everything right now
                    EffectKey key = new EffectKey(sourceClass, resType);
                    loadWriter(sourceClass, writer);
                    acc.put(key, writer);

                    p = 0;
                    continue;
                }
            }

            p++;
        }

        return Collections.unmodifiableMap(acc);
    }

}
