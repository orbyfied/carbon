package net.orbyfied.carbon.util;

import net.orbyfied.carbon.util.message.Context;

public interface Provider<T> {

    T provide(Context context, String key);

}
