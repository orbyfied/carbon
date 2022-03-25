package com.github.orbyfied.carbon.command.parameter;

public class TypeParameter {

    private String name;
    private ParameterType<?> type;

    public TypeParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ParameterType<?> getType() {
        return type;
    }

    public TypeParameter setType(ParameterType<?> type) {
        this.type = type;
        return this;
    }

}
