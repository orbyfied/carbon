package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.util.StringReader;

import java.util.*;

/**
 * @param <B> The base type (without the generics, for example {@link List})
 */
public abstract class GenericParameterType<B> implements ParameterType<B> {

    public GenericParameterType(List<String> params) {
        this.parameters = new ArrayList<>(params);
    }

    public GenericParameterType(String... params) {
        this(Arrays.asList(params));
    }

    /**
     * The type parameters available.
     */
    final ArrayList<String> parameters;

    @SuppressWarnings("unchecked")
    public Class<B> getBaseType() {
        return (Class<B>) getType();
    }

    public TypeIdentifier getGenericIdentifier(LinkedHashMap<String, ParameterType<?>> typeParams) {
        TypeIdentifier id = getBaseIdentifier().clone();
        for (ParameterType<?> pt : typeParams.values())
            id.getTypeParams().add(pt.getIdentifier());
        return id;
    }

    public List<String> getTypeParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public String getTypeParameter(int i) {
        return parameters.get(i);
    }

    @Override
    public TypeIdentifier getIdentifier() {
        return getBaseIdentifier();
    }

    @Override
    public boolean accepts(Context context, StringReader reader) {
        throw new IllegalArgumentException("Raw use of parameterized type " + getBaseIdentifier());
    }

    @Override
    public B parse(Context context, StringReader reader) {
        throw new IllegalArgumentException("Raw use of parameterized type " + getBaseIdentifier());
    }

    @Override
    public void write(Context context, StringBuilder builder, B v) {
        throw new IllegalArgumentException("Raw use of parameterized type " + getBaseIdentifier());
    }

    @Override
    public void suggest(Context context, SuggestionAccumulator suggestions) {
        throw new IllegalArgumentException("Raw use of parameterized type " + getBaseIdentifier());
    }

    public GenericTypeInstance<B> instance(ParameterType... types) {
        return new GenericTypeInstance<>(this, types);
    }

    public GenericTypeInstance<B> instance(List<ParameterType> types) {
        return new GenericTypeInstance<>(this, types);
    }

    /* actual parameter methods */

    public abstract boolean accepts(Context context, StringReader reader, LinkedHashMap<String, ParameterType> types);
    public abstract B parse(Context context, StringReader reader, LinkedHashMap<String, ParameterType> types);
    public abstract void write(Context context, StringBuilder builder, B v, LinkedHashMap<String, ParameterType> types);
    public abstract void suggest(Context context, SuggestionAccumulator suggestions, LinkedHashMap<String, ParameterType> types);

}
