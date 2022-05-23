package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

import java.lang.reflect.Field;
import java.util.*;

public interface ComponentWriter<S, T> extends CWF<S, T> {

    Class<T> getResultType();

    Class<S> getSourceType();

    /////////////////////////////////////////////

    HashMap<Class, ComponentWriter> loadedWriters = new HashMap<>();
    String WRITERS_FIELD_NAME = "WRITERS";

    static ComponentWriter loadWriter(Class cl, ComponentWriter writer) {
        loadedWriters.put(cl, writer);
        return writer;
    }

    @SuppressWarnings("unchecked")
    static <S, T> T serializeStatic(Context context,
                                    Class<T> resType,
                                    S src,
                                    MessageWriter<T, ?> writer) {
        final Class sClass = src.getClass();

        // try to get writer from cache
        ComponentWriter<S, T> cw = null;
        if ((cw = loadedWriters.get(sClass)) == null) {
            // try to load writer from class
            try {
                // get field
                Field f = sClass.getDeclaredField(WRITERS_FIELD_NAME);
                f.setAccessible(true);

                // get content
                Map<Class, ComponentWriter> writerMap = (Map<Class, ComponentWriter>) f.get(null);

                // try to retrieve writer
                if ((cw = writerMap.get(resType)) == null)
                    throw new IllegalArgumentException(
                            "Component type '" + sClass.getName() +
                            "' does not define a writer for target: " + resType.getName()
                    );

            } catch (NoSuchFieldException e) {
                // class does not define writers
                throw new IllegalArgumentException(
                        "Component type '" + sClass.getName() + "' does not define any writers, " +
                        "should be defined in field: " + WRITERS_FIELD_NAME
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if (cw == null)
            return null; // couldnt find serializer

        // serialize and return
        T res = cw.serialize(context, writer, src);
        return res;
    }

    static <S> Map<Class, ComponentWriter<S, ?>> writers(Class<S> sourceClass,
                                                         Object... params) {
        Class<Object> resType = null;
        Map<Class, ComponentWriter<S, ?>> acc = new HashMap<>();

        int l = params.length;
        for (int i = 0; i < l; i++) {
            Object v = params[i];
            if (i % 2 == 0) { // type spec
                resType = (Class<Object>) v;
            } else {
                final CWF cwf = (CWF) v;

                final Class<Object> fResType = resType;
                acc.put(resType, new ComponentWriter<S, Object>() {
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
                });
            }
        }

        return Collections.unmodifiableMap(acc);
    }

}
