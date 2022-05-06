package net.orbyfied.carbon.command.parameter;

import net.orbyfied.carbon.registry.Identifier;

import java.util.ArrayList;
import java.util.List;

public interface TypeResolver {

    ParameterType<?> resolve(Identifier identifier);

    default ParameterType<?> compile(TypeIdentifier identifier) {
        ParameterType<?> base = resolve(identifier);
        if (!(base instanceof GenericParameterType<?> g))
            return base;

        List<TypeIdentifier> params = identifier.getTypeParams();
        List<ParameterType>  types  = new ArrayList<>(params.size());
        int l = params.size();
        for (int i = 0; i < l; i++)
            types.add(compile(params.get(i)));

        return g.instance(types);
    }

}
