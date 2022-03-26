package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.parameter.TypeResolver;

public abstract class CommandEngine {

    TypeResolver typeResolver;

    public CommandEngine withTypeResolver(TypeResolver resolver) {
        this.typeResolver = resolver;
        return this;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

}
