package net.orbyfied.carbon.command.parameter;

import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.StringReader;

import java.util.ArrayList;
import java.util.Objects;

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

    @Override
    public TypeIdentifier clone() {
        try {
            return (TypeIdentifier) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(super.toString());
        if (typeParams.size() == 0) return b.toString();
        b.append("<");
        int l = typeParams.size();
        for (int i = 0; i < l; i++) {
            if (i != 0)
                b.append(", ");
            TypeIdentifier param = typeParams.get(i);
            b.append(param);
        }
        return b.append(">").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeIdentifier that = (TypeIdentifier) o;
        return Objects.equals(typeParams, that.typeParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), typeParams);
    }

}
