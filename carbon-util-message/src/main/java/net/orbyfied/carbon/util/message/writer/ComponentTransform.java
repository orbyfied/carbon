package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface ComponentTransform<C, E, T> extends CTF<C, E, T>  {

    Class<C> getComponentType();

    Class<E> getEffectType();

    Class<T> getTargetType();

    ////////////////////////////////////////////////

    HashMap<EffectKey<?, ?>, ComponentTransform> loadedTransforms = new HashMap<>();
    String TRANSFORMS_FIELD_NAME = "TRANSFORMS";

    static <C, E, T> ComponentTransform<C, E, T> loadTransform(Class<E> effKlass,
                                                               Class<T> targetKlass,
                                                               ComponentTransform<C, E, T> transform) {
        loadedTransforms.put(new EffectKey<>(effKlass, targetKlass), transform);
        return transform;
    }

    @SuppressWarnings("unchecked")
    static <C, E, T> ComponentTransform<C, E, T> getTransform(Class<E> eff,
                                                              Class<T> targ) {
        // create effect key
        EffectKey<E, T> key = new EffectKey<>(eff, targ);

        // try to get writer from cache
        ComponentTransform<C, E, T> ct;
        if ((ct = loadedTransforms.get(key)) == null)
            throw new IllegalArgumentException("No transform loaded for key: " + key);

        // return
        return ct;
    }

    @SuppressWarnings("unchecked")
    static <C, E, T> T transformStatic(Context context,
                                       MessageWriter<T, ?> writer,
                                       C component,
                                       E effect,
                                       T current) {
        // get classes
        Class<C> compClass = (Class<C>) component.getClass();
        Class<E> effClass  = (Class<E>) effect.getClass();
        Class<T> targClass = (Class<T>) current.getClass();

        // get transform
        ComponentTransform<C, E, T> transform = getTransform(effClass, targClass);
        if (transform == null)
            return null;

        // check component type
        if (!transform.getComponentType().isAssignableFrom(compClass))
            throw new IllegalArgumentException(
                    "Unsupported component provided to transform '" + new EffectKey<>(effClass, targClass) + "'" +
                    ": expected ? extends " + transform.getComponentType().getName() + ", got " + compClass.getName()
            );

        // apply transform
        return transform.transform(
                context,
                writer,
                component,
                effect,
                current
        );
    }

    @SuppressWarnings("unchecked")
    static Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> transforms(Object... params) {
        Class<Object> effType = null;
        Class<Object> resType = null;
        Class<Object> comType = null;
        Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> acc = new HashMap<>();

        int l = params.length;
        int p = 0;
        for (int i = 0; i < l; i++) {
            Object v = params[i];
            switch (p) {
                case 0 -> effType = (Class<Object>) v;
                case 1 -> resType = (Class<Object>) v;
                case 2 -> comType = (Class<Object>) v;
                case 3 -> {
                    final CTF cwf = (CTF) v;

                    final Class<Object> fResType = resType;
                    final Class<Object> fEffType = effType;
                    final Class<Object> fComType = comType;

                    ComponentTransform<Object, Object, Object> transform = new ComponentTransform<>() {
                        @Override
                        public Class<Object> getComponentType() {
                            return fComType;
                        }

                        @Override
                        public Class<Object> getEffectType() {
                            return fEffType;
                        }

                        @Override
                        public Class<Object> getTargetType() {
                            return fResType;
                        }

                        @Override
                        public Object transform(Context context, MessageWriter<Object, ?> writer, Object component, Object effect, Object serialized) {
                            return cwf.transform(context,
                                    writer,
                                    component,
                                    effect,
                                    serialized);
                        }
                    };

                    // load everything right now
                    EffectKey<?, ?> key = new EffectKey<>(effType, resType);
                    loadTransform(effType, resType, transform);
                    acc.put(key, transform);

                    p = 0;
                    continue;
                }
            }

            p++;
        }

        return Collections.unmodifiableMap(acc);
    }

}
