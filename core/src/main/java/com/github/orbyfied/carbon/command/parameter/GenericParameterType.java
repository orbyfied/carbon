package com.github.orbyfied.carbon.command.parameter;

import java.util.*;

public abstract class GenericParameterType<B> implements ParameterType<B> {

    public GenericParameterType(List<TypeParameter> params) {
        for (TypeParameter param : params) {
            parameters.add(param);
            parametersByName.put(param.getName(), param);
        }
    }

    public GenericParameterType(TypeParameter... params) {
        this(Arrays.asList(params));
    }

    HashMap<String, TypeParameter> parametersByName = new HashMap<>();

    ArrayList<TypeParameter> parameters = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Class<B> getBaseType() {
        return (Class<B>) getType();
    }

    public TypeIdentifier getGenericIdentifier() {
        TypeIdentifier id = getBaseIdentifier().clone();
        for (TypeParameter param : parameters)
            id.getTypeParams().add(param.getType().getBaseIdentifier());
        return id;
    }

    public GenericParameterType<B> setTypeParameter(String s, ParameterType<?> type) {
        parametersByName.get(s).setType(type);
        return this;
    }

    public GenericParameterType<B> setTypeParameter(int i, ParameterType<?> type) {
        parameters.get(i).setType(type);
        return this;
    }

    public TypeParameter getTypeParameter(String s) {
        return parametersByName.get(s);
    }

    public TypeParameter getTypeParameter(int i) {
        return parameters.get(i);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GenericParameterType.class.getSimpleName() + "[", "]")
                .add("parametersByName=" + parametersByName)
                .add("parameters=" + parameters)
                .add("identifier=" + getGenericIdentifier())
                .toString();
    }

}
