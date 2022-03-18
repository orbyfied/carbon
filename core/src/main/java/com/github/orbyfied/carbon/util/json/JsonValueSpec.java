package com.github.orbyfied.carbon.util.json;

import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.StructStringBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface JsonValueSpec<T> {

    T fromJson(StringReader iter);

    String toJson(Object it, int indentLevel);

    /* ----------- PRIMITIVES ----------- */

    private static boolean isDigit(char c) {
        return c <= '9' && c >= '0' || c == '.';
    }

    static JsonValueSpec<?> getPrimitiveFor(char c) {
        JsonValueSpec<?> spec;
        switch (c) {
            case '{'  -> spec = JsonValueSpec.JSON_OBJECT_PRIMITIVE;
            case '['  -> spec = JsonValueSpec.JSON_ARRAY_PRIMITIVE;
            case '"'  -> spec = JsonValueSpec.STRING_PRIMITIVE;
            case '\'' -> spec = JsonValueSpec.CHAR_PRIMITIVE;
            case 'n'  -> spec = JsonValueSpec.NULL_PRIMITIVE;
            default -> {
                if (isDigit(c))
                    spec = JsonValueSpec.NUMBER_PRIMITIVE;
                else if (c == 'f' || c == 't')
                    spec = JsonValueSpec.BOOLEAN_PRIMITIVE;
                else
                    throw new IllegalArgumentException("for character: '" + c + "'");
            }
        }

        return spec;
    }

    static JsonValueSpec<?> getPrimitiveFor(Object o) {
        if (o == null) return JsonValueSpec.NULL_PRIMITIVE;
        if (o instanceof Number) return JsonValueSpec.NUMBER_PRIMITIVE;
        else if (o instanceof Boolean) return JsonValueSpec.BOOLEAN_PRIMITIVE;
        else if (o instanceof String) return JsonValueSpec.STRING_PRIMITIVE;
        else if (o instanceof Character) return JsonValueSpec.CHAR_PRIMITIVE;
        else if (o instanceof JsonArray) return JsonValueSpec.JSON_ARRAY_PRIMITIVE;
        else if (o instanceof JsonObject) return JsonValueSpec.JSON_OBJECT_PRIMITIVE;
        throw new IllegalArgumentException();
    }

    static <T> JsonValueSpec<T> createPrimitive(final Function<StringReader, T> parser,
                                                final BiFunction<T, Integer, String> toJson) {
        return new JsonValueSpec<>() {
            @Override
            public T fromJson( StringReader iter) {
                return parser.apply(iter);
            }

            @SuppressWarnings("unchecked")
            @Override
            public String toJson(Object it, int b) {
                if (it instanceof Number n)
                    it = n.doubleValue();
                return toJson.apply((T) it, b);
            }
        };
    }

    JsonValueSpec<JsonArray> JSON_ARRAY_PRIMITIVE = createPrimitive(
            reader -> {
                reader.next();
                JsonArray array = new JsonArray();
                while (reader.current() != ']') {
                    reader.collect(c -> c != ']' && (c == ' ' || c == '\n' || c == '\t'));
                    if (reader.current() == ']')
                        break;
                    System.out.println("[a] " + reader.index() + ": '" + reader.current() + "', prog: " + array);
                    array.add(getPrimitiveFor(reader.current()).fromJson(reader));
                }
                return array;
            },
            (val, sb) -> {
                StructStringBuilder b = new StructStringBuilder(sb);
                b.indent(1);
                b.append("[\n");
                List<Object> list = val.list;
                int l = list.size();
                for (int i = 0; i < l; i++) {
                    Object o = list.get(i);
                    JsonValueSpec<?> spec = getPrimitiveFor(o);
                    if (i != 0)
                        b.append(",\n");
                    b.append(spec.toJson(o, b.getIndentLevel()));
                }
                b.indent(-1);
                return b.append("\n").append("]").toString();
            }
    );

    JsonValueSpec<JsonObject> JSON_OBJECT_PRIMITIVE = createPrimitive(
            reader -> {
                reader.next();
                JsonObject obj = new JsonObject();
                String key;
                while (reader.current() != ']') {
                    reader.collect(c -> c == ' ' || c == '\n' || c == '\t');
                    if (reader.current() == ']')
                        break;
                    reader.next();
                    key = reader.collect(c -> c != '"');
                    reader.collect(c -> c != ':');
                    reader.next();
                    reader.collect(c -> c == ' ');
                    System.out.println("[o] " + reader.index() + ": '" + reader.current() + "', key: " + key + ", prog: " + obj);
                    obj.set(key, getPrimitiveFor(reader.current()).fromJson(reader));
                    if (reader.current() == ',')
                        reader.next();
                }
                return obj;
            },
            (val, sb) -> {
                StructStringBuilder b = new StructStringBuilder(sb);
                b.indent(1);
                b.append("{\n");
                Set<Map.Entry<String, Object>> entries = val.map.entrySet();
                int i = 0;
                for (Map.Entry<String, Object> entry : entries) {
                    Object o = entry.getValue();
                    String k = entry.getKey();
                    JsonValueSpec<?> spec = getPrimitiveFor(o);
                    if (i != 0)
                        b.append(",\n");
                    b.append("\"").append(k).append("\" : ");
                    b.append(spec.toJson(o, b.getIndentLevel()));
                    i++;
                }
                b.indent(-1);
                return b.append("\n").append("}").toString();
            }
    );

    JsonValueSpec<Character> CHAR_PRIMITIVE = createPrimitive(
            reader -> {
                reader.next();
                String s = reader.collect(c -> c != '\'' && c != '\n', 1);
                if (reader.current() == ',')
                    reader.next();
                return s.charAt(0);
            },
            (val, sb) -> '\'' + Character.toString(val) + '\''
    );

    JsonValueSpec<Boolean> BOOLEAN_PRIMITIVE = createPrimitive(
            reader -> Boolean.parseBoolean(reader.collect(c -> c != ',' && c != '\n', 1)),
            (val, sb) -> Boolean.toString(val)
    );

    JsonValueSpec<Double> NUMBER_PRIMITIVE = createPrimitive(
            reader -> Double.parseDouble(reader.collect(c -> c != ',' && c != '\n', c -> c == '_', 1)),
            (val, sb) -> Double.toString(val)
    );

    JsonValueSpec<String> STRING_PRIMITIVE = createPrimitive(
            reader -> {
                reader.next();
                String str = reader.collect(c -> c != '"' && c != '\n', 1);
                if (reader.current() == ',')
                    reader.next();
                return str;
            },
            (val, sb) -> '"' + val + '"'
    );

    JsonValueSpec<Void> NULL_PRIMITIVE = createPrimitive(
            reader -> {
                reader.collect(c -> c != ',' && c != '\n', 1);
                return null;
            },
            (val, sb) -> "null"
    );

}
