package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public interface ParameterType<T> {

    Class<T> getType();

    boolean accepts(Context context, StringReader reader);

    T parse(Context context, StringReader reader);

    void write(Context context, StringBuilder builder, T v);

    void suggest(Context context, Suggestions suggestions);

}
