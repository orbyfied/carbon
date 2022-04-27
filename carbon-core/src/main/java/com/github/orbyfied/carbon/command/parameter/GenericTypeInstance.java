package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.util.StringReader;

import java.util.LinkedHashMap;
import java.util.List;

public class GenericTypeInstance<B> implements ParameterType<B> {

    public GenericTypeInstance(GenericParameterType<B> base,
                               ParameterType... types) {
        this.base = base;

        // set type parameter values
        int l = types.length;
        System.out.println(base.getTypeParameters());
        if (l != base.getTypeParameters().size())
            throw new IllegalArgumentException("Invalid amount of type parameters.");
        List<String> paramNames = base.getTypeParameters();
        for (int i = 0; i < l; i++) {
            params.put(paramNames.get(i), types[i]);
        }
    }

    public GenericTypeInstance(GenericParameterType<B> base,
                               List<ParameterType> types) {
        this.base = base;

        // set type parameter values
        int l = types.size();
        if (l != base.getTypeParameters().size())
            throw new IllegalArgumentException("Invalid amount of type parameters.");
        List<String> paramNames = base.getTypeParameters();
        for (int i = 0; i < l; i++) {
            params.put(paramNames.get(i), types.get(i));
        }
    }

    final LinkedHashMap<String, ParameterType> params = new LinkedHashMap<>();
    final GenericParameterType<B> base;

    public GenericParameterType<B> getBase() {
        return base;
    }

    @Override
    public TypeIdentifier getIdentifier() {
        TypeIdentifier id = getBaseIdentifier().clone();
        for (ParameterType<?> pt : params.values())
            id.getTypeParams().add(pt.getIdentifier());
        return id;
    }

    @Override
    public TypeIdentifier getBaseIdentifier() {
        return base.getBaseIdentifier();
    }

    @Override
    public Class<?> getType() {
        return base.getType();
    }

    @Override
    public boolean accepts(Context context, StringReader reader) {
        return base.accepts(context, reader, params);
    }

    @Override
    public B parse(Context context, StringReader reader) {
        return base.parse(context, reader, params);
    }

    @Override
    public void write(Context context, StringBuilder builder, B v) {
        base.write(context, builder, v, params);
    }

    @Override
    public void suggest(Context context, SuggestionAccumulator suggestions) {
        base.suggest(context, suggestions, params);
    }

}
