package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.registry.Identifier;

public interface TypeResolver {

    ParameterType<?> resolve(Identifier identifier);

    ParameterType<?> compile(TypeIdentifier identifier);

}
