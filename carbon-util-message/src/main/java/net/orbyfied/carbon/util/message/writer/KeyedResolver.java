package net.orbyfied.carbon.util.message.writer;

// TODO
public interface KeyedResolver<V> {

    V get(Class<?> effectClass, Class<?> targetClass);

}
