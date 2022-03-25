package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;

import java.util.ArrayList;

public class TypeIdentifier extends Identifier implements Cloneable {

    public static TypeIdentifier parse(String in, TypeIdentifier out) {
        Identifier.parse(in, out);
        StringReader reader = new StringReader(in, 0);
        reader.collect(c -> c != '<');
        if (reader.next() == StringReader.DONE)
            return out;
        char c;
        while ((c = reader.current()) != '>' && c != StringReader.DONE) {
            TypeIdentifier ident = new TypeIdentifier(null, null);
            String s = reader.collect(c1 -> c1 != ',');
            parse(s, ident);
            out.typeParams.add(ident);
        }
        return out;
    }

    public static TypeIdentifier of(String in) {
        return parse(in, new TypeIdentifier(null, null));
    }

    ////////////////////////////////////////////////

    private ArrayList<TypeIdentifier> typeParams = new ArrayList<>();

    public TypeIdentifier(String namespace,
                          String path) {
        super(namespace, path);
    }

    public ArrayList<TypeIdentifier> getTypeParams() {
        return typeParams;
    }

    public GenericParameterType<?> transfer(GenericParameterType<?> type) {
        int l = typeParams.size();
        for (int i = 0; i < l; i++)
            type.setTypeParameter(i, (ParameterType<?>) typeParams.get(i));
        return type;
    }

    @Override
    public TypeIdentifier clone() {
        try {
            return (TypeIdentifier) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
