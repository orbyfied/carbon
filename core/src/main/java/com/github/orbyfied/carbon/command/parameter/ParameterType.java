package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Suggestions;
import com.github.orbyfied.carbon.util.StringReader;

public interface ParameterType<T> {

    TypeIdentifier getBaseIdentifier();

    default TypeIdentifier getIdentifier() {
        return getBaseIdentifier();
    }

    Class<?> getType();

    boolean accepts(Context context, StringReader reader);

    T parse(Context context, StringReader reader);

    void write(Context context, StringBuilder builder, T v);

    void suggest(Context context, Suggestions suggestions);

}
