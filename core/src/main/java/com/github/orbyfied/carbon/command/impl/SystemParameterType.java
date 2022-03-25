package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.ParameterType;
import com.github.orbyfied.carbon.command.Suggestions;
import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.TriConsumer;
import com.github.orbyfied.carbon.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * Standard, 'system' parameter types that
 * can be applied to Java and common values
 * as a whole. This includes things like
 * integers, floats, strings, vectors, etc.
 * Also contains methods for creating variable
 * type lists and maps.
 */
public class SystemParameterType {

    private SystemParameterType() { }

    static <T> ParameterType<T> of(final Class<T> klass,
                                   final BiPredicate<Context, StringReader> acceptor,
                                   final BiFunction<Context, StringReader, T> parser,
                                   final TriConsumer<Context, StringBuilder, T> writer) {
        return new ParameterType<T>() {
            @Override
            public Class<T> getType() {
                return klass;
            }

            @Override
            public boolean accepts(Context context, StringReader reader) {
                return acceptor.test(context, reader);
            }

            @Override
            public T parse(Context context, StringReader reader) {
                return parser.apply(context, reader);
            }

            @Override
            public void write(Context context, StringBuilder builder, T v) {
                writer.accept(context, builder, v);
            }

            @Override
            public void suggest(Context context, Suggestions suggestions) { }
        };
    }

    private static boolean isDigit(char c, int radix) {
        return (c >= '0' && c <= '9')  ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z') ||
                c == '.' || c == '_';
    }

    private static <T extends Number> T parseNumberFloat(StringReader reader,
                                                         Function<String, T> parser) {
        return parseNumber(reader, (str, __) -> parser.apply(str));
    }

    private static <T extends Number> T parseNumber(StringReader reader,
                                                    BiFunction<String, Integer, T> parser) {
        reader.collect(c -> c == ' ');

        int radix = 10;
        if (reader.current() == '0') {
            boolean c = true;
            switch (reader.peek(1)) {
                case 'x' -> radix = 16;
                case 'b' -> radix = 2;
                case 'o' -> radix = 8;
                default -> c = false;
            }
            if (c)
                reader.next(2);
        }

        final int rdx = radix;

        return parser.apply(reader.collect(c -> isDigit(c, rdx), c -> c == '_'), radix);
    }

    /* ----------------------------------------------- */

    public static final ParameterType<Byte> BYTE = of(Byte.class,
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Byte::parseByte),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<Short> SHORT = of(Short.class,
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Short::parseShort),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<Integer> INT = of(Integer.class,
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Integer::parseInt),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<Long> LONG = of(Long.class,
            (context, reader) -> isDigit(reader.current(), 10) || reader.current() == '.',
            (context, reader) -> parseNumber(reader, Long::parseLong),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<Float> FLOAT = of(Float.class,
            (context, reader) -> isDigit(reader.current(), 10) || reader.current() == '.',
            (context, reader) -> parseNumberFloat(reader, Float::parseFloat),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<Double> DOUBLE = of(Double.class,
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumberFloat(reader, Double::parseDouble),
            (context, builder, value) -> builder.append(value)
    );

    public static final ParameterType<String> STRING = of(String.class,
            (context, reader) -> true,
            ((context, reader) -> {
                if (reader.current() == '"') {
                    reader.next();
                    return reader.collect(c -> c != '"', 1);
                }
                return reader.collect(c -> c != ' ');
            }),
            (context, builder, s) -> builder.append("\"" + s + "\"")
    );

    public static final ParameterType<Character> CHAR = of(Character.class,
            (context, reader) -> true,
            ((context, reader) -> {
                if (reader.current() == '\'') {
                    reader.next();
                    return reader.collect(c -> c != '\'', 1).charAt(0);
                }
                return reader.collect(c -> c != ' ').charAt(0);
            }),
            (context, builder, s) -> builder.append("\"" + s + "\"")
    );

    public static final ParameterType<Vec3i> VEC_3_INT = of(Vec3i.class,
            (context, reader) -> true,
            ((context, reader) -> {
                boolean bracketed = false;
                if (reader.current() == '(') {
                    bracketed = true;
                    reader.next();
                }

                int[] c = new int[3];
                for (int i = 0; i < 3; i++) {
                    c[i] = SystemParameterType.INT.parse(context, reader);
                    if (!bracketed)
                        reader.collect(c1 -> c1 != ' ', 1);
                    else
                        reader.collect(c1 -> c1 != ',' && c1 != ')', 1);
                }

                return new Vec3i(c);
            }),
            (context, builder, v) -> builder.append(v.toString())
    );

    public static <T> ParameterType<List<T>> listOf(ParameterType<T> type) {
        return new ParameterType<>() {
            @Override
            public Class<?> getType() {
                return List.class;
            }

            @Override
            public boolean accepts(Context context, StringReader reader) {
                return true; // TODO
            }

            @Override
            public List<T> parse(Context context, StringReader reader) {
                List<T> list = new ArrayList<>();
                char c1;
                while ((c1 = reader.next()) != ']' && c1 != StringReader.DONE) { // already skips over first [
//                    System.out.println("b" + reader.index() + ": '" + reader.current() + "'");
                    reader.collect(c -> c == ' ');
                    list.add(type.parse(context, reader));
//                    System.out.println("a" + reader.index() + ": '" + reader.current() + "'");
//                    System.out.println("l: " + list);
                }

                return list;
            }

            @Override
            public void write(Context context, StringBuilder builder, List<T> v) {
                builder.append("[");
                int l = v.size();
                for (int i = 0; i < l; i++) {
                    if (i != 0)
                        builder.append(", ");
                    type.write(context, builder, v.get(i));
                }
                builder.append("]");
            }

            @Override
            public void suggest(Context context, Suggestions suggestions) { }
        };
    }

}
