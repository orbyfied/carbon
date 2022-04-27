package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.command.parameter.*;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.functional.QuadConsumer;
import com.github.orbyfied.carbon.util.functional.TriConsumer;
import com.github.orbyfied.carbon.util.functional.TriFunction;
import com.github.orbyfied.carbon.util.functional.TriPredicate;
import com.github.orbyfied.carbon.util.math.Vec3i;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Standard, 'system' parameter types that
 * can be applied to Java and common values
 * as a whole. This includes things like
 * integers, floats, strings, vectors, etc.
 * Also contains methods for creating variable
 * type lists and maps.
 */
public class SystemParameterType {

    /**
     * Class for safely resolving system types.
     */
    public static final class SystemTypeResolver implements TypeResolver {

        protected HashMap<String, ParameterType<?>> types = new HashMap<>();

        @Override
        public ParameterType<?> resolve(Identifier identifier) {
            return types.get(identifier.getPath());
        }

    }

    /** UTILITY CLASS */
    private SystemParameterType() { }

    /**
     * The singleton type resolver.
     */
    public static final SystemTypeResolver typeResolver = new SystemTypeResolver();

    /**
     * Function to quickly create simple
     * parameter types with lambdas.
     * @see ParameterType
     */
    @SuppressWarnings("unchecked")
    static <T> ParameterType<T> of(final Class<T> klass,
                                   final String baseId,
                                   final BiPredicate<Context, StringReader> acceptor,
                                   final BiFunction<Context, StringReader, T> parser,
                                   final TriConsumer<Context, StringBuilder, T> writer,
                                   final Object... optional) {
        // parse identifier
        final TypeIdentifier bid = TypeIdentifier.of(baseId);

        // parse optionals
        BiConsumer<Context, SuggestionAccumulator> suggester = null;

        for (Object o : optional) {
            if (o instanceof BiConsumer)
                suggester = (BiConsumer<Context, SuggestionAccumulator>) o;
        }

        final BiConsumer<Context, SuggestionAccumulator> finalSuggester = suggester;

        // create type
        ParameterType<T> type = new ParameterType<>() {
            @Override
            public TypeIdentifier getBaseIdentifier() {
                return bid;
            }

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
            public void suggest(Context context, SuggestionAccumulator suggestions) {
                if (finalSuggester != null)
                    finalSuggester.accept(context, suggestions);
            }

            @Override
            public String toString() {
                return bid.toString();
            }
        };

        // register type
        typeResolver.types.put(bid.getPath(), type);

        // return
        return type;
    }
    /**
     * Function to quickly create generic
     * parameter types with lambdas.
     * @see ParameterType
     */
    @SuppressWarnings("unchecked")
    static <T> GenericParameterType<T> ofGeneric(final Class<T> klass,
                                                 final String baseId,
                                                 final String paramsStr,
                                                 final TriPredicate<Context, StringReader, LinkedHashMap<String, ParameterType>> acceptor,
                                                 final TriFunction<Context, StringReader, LinkedHashMap<String, ParameterType>, T> parser,
                                                 final QuadConsumer<Context, StringBuilder, T, LinkedHashMap<String, ParameterType>> writer,
                                                 final Object... optional) {
        // parse identifier
        final TypeIdentifier bid = TypeIdentifier.of(baseId);

        // parse type parameters
        final String[] params = paramsStr.split(" ");

        // parse optionals
        TriConsumer<Context, SuggestionAccumulator, LinkedHashMap<String, ParameterType>> suggester = null;

        for (Object o : optional) {
            if (o instanceof BiConsumer)
                suggester = (TriConsumer<Context, SuggestionAccumulator, LinkedHashMap<String, ParameterType>>) o;
        }

        final TriConsumer<Context, SuggestionAccumulator, LinkedHashMap<String, ParameterType>> finalSuggester = suggester;

        // create type
        GenericParameterType<T> type = new GenericParameterType<>(params) {
            @Override
            public boolean accepts(Context context, StringReader reader, LinkedHashMap<String, ParameterType> types) {
                return acceptor.test(context, reader, types);
            }

            @Override
            public T parse(Context context, StringReader reader, LinkedHashMap<String, ParameterType> types) {
                return parser.apply(context, reader, types);
            }

            @Override
            public void write(Context context, StringBuilder builder, T v, LinkedHashMap<String, ParameterType> types) {
                writer.accept(context, builder, v, types);
            }

            @Override
            public void suggest(Context context, SuggestionAccumulator suggestions, LinkedHashMap<String, ParameterType> types) {
                if (finalSuggester != null)
                    finalSuggester.accept(context, suggestions, types);
            }

            @Override
            public TypeIdentifier getBaseIdentifier() { return bid; }

            @Override
            public Class<?> getType() { return klass; }
        };

        // register type
        typeResolver.types.put(bid.getPath(), type);

        // return
        return type;
    }

    /**
     * Checks if a character is a digit.
     * TODO: account for radix
     * @param c The character to check.
     * @param radix The radix.
     * @return If the number is a digit.
     */
    private static boolean isDigit(char c, int radix) {
        return (c >= '0' && c <= '9')  ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z') ||
                c == '.' || c == '_';
    }

    /**
     * Parses a floating point number.
     * @see SystemParameterType#parseNumber(StringReader, BiFunction)
     * @param reader The string reader.
     * @param parser The number parser.
     * @param <T> The number type.
     * @return The number/value.
     */
    private static <T extends Number> T parseNumberFloat(StringReader reader,
                                                         Function<String, T> parser) {
        return parseNumber(reader, (str, __) -> parser.apply(str));
    }

    /**
     * Parses a floating or non-floating point
     * number. For non-floating point numbers
     * the parser needs to be configured to
     * not accept any radix specifications.
     * @param reader The string reader.
     * @param parser The number parser.
     * @param <T> The number type.
     * @return The number/value.
     */
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
                default  -> c = false;
            }
            if (c)
                reader.next(2);
        }

        final int rdx = radix;

        return parser.apply(reader.collect(c -> isDigit(c, rdx), c -> c == '_'), radix);
    }

    /* ----------------------------------------------- */

    /**
     * Accepts formats as:
     * - {@code 14}
     * - {@code 0x06}
     * - {@code 0o93}
     * - {@code 0b101101}
     * {@link Byte}
     */
    public static final ParameterType<Byte> BYTE = of(Byte.class, "system:byte",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Byte::parseByte),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * Accepts formats as:
     * - {@code 14}
     * - {@code 0x06}
     * - {@code 0o93}
     * - {@code 0b101101}
     * {@link Short}
     */
    public static final ParameterType<Short> SHORT = of(Short.class, "system:short",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Short::parseShort),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * Accepts formats as:
     * - {@code 14}
     * - {@code 0x06}
     * - {@code 0o93}
     * - {@code 0b101101}
     * {@link Integer}
     */
    public static final ParameterType<Integer> INT = of(Integer.class, "system:int",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Integer::parseInt),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * Accepts formats as:
     * - {@code 14}
     * - {@code 0x06}
     * - {@code 0o93}
     * - {@code 0b101101}
     * {@link Long}
     */
    public static final ParameterType<Long> LONG = of(Long.class, "system:long",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumber(reader, Long::parseLong),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * {@link Float}
     */
    public static final ParameterType<Float> FLOAT = of(Float.class, "system:float",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumberFloat(reader, Float::parseFloat),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * {@link Double}
     */
    public static final ParameterType<Double> DOUBLE = of(Double.class, "system:double",
            (context, reader) -> isDigit(reader.current(), 10),
            (context, reader) -> parseNumberFloat(reader, Double::parseDouble),
            (context, builder, value) -> builder.append(value)
    );

    /**
     * {@link String}
     * Either parses the string until it is
     * met with a space, or if the string starts
     * with a {@code "}, it parses until the end
     * of the string. (the closing quote)
     */
    public static final ParameterType<String> STRING = of(String.class, "system:string",
            (context, reader) -> true,
            ((context, reader) -> {
                if (reader.current() == '"') {
                    reader.next();
                    return reader.collect(c -> c != '"', 1);
                }
                return reader.collect(c -> c != ' ');
            }),
            (context, builder, s) -> builder.append("\"").append(s).append("\"")
    );

    /**
     * {@link Character}
     */
    public static final ParameterType<Character> CHAR = of(Character.class, "system:char",
            (context, reader) -> true,
            ((context, reader) -> {
                if (reader.current() == '\'') {
                    reader.next();
                    return reader.collect(c -> c != '\'', 1).charAt(0);
                }
                return reader.collect(c -> c != ' ').charAt(0);
            }),
            (context, builder, s) -> builder.append("'").append(s).append("'")
    );

    /**
     * {@link Path}
     * Parses a path from a string.
     * Utilizes {@link SystemParameterType#STRING}
     * for reading actual data and then parses it
     * using {@link Path#of(String, String...)}
     */
    public static final ParameterType<Path> FILE_PATH = of(Path.class, "system:filepath",
            (context, reader) -> true,
            ((context, reader) -> Path.of(STRING.parse(context, reader))),
            (context, builder, v) -> builder.append("\"").append(v.toString()).append("\n")
    );

    /**
     * {@link Vec3i}
     * Parses a vector 3 of integers.
     * Allowed notations are:
     * - {@code 0 1 2} and,
     * - {@code (0, 1, 2)}
     */
    public static final ParameterType<Vec3i> VEC_3_INT = of(Vec3i.class, "system:vec3i",
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

    /**
     * A list of any other parameter type (generic).
     * Notation: {@code [elem1, elem2, ...]}
     */
    public static final GenericParameterType<List> LIST = ofGeneric(List.class, "system:list", "T",
            (context, reader, types) -> true,
            ((context, reader, types) -> {
                // get type
                ParameterType<?> type = types.get("T");

                // construct empty list
                List<Object> list = new ArrayList<>();

                char c1;
                while ((c1 = reader.next()) != ']' && c1 != StringReader.DONE) { // already skips over first [
                    reader.collect(c -> c == ' '); // to skip whitespace
                    // parse value using type and add that to the list
                    list.add(type.parse(context, reader));
                }

                return list;
            }),
            ((context, builder, v, types) -> {
                // get type
                ParameterType type = types.get("T");

                // start with [
                builder.append("[");
                int l = v.size();
                for (int i = 0; i < l; i++) {
                    if (i != 0) // append comma
                        builder.append(", ");
                    // write value using type
                    type.write(context, builder, v.get(i));
                }

                // end with ]
                builder.append("]");
            }),

            /* suggester */
            ((TriConsumer<Context, SuggestionAccumulator, LinkedHashMap<String, ParameterType>>)(context, suggestions, types) -> {
                StringReader reader = context.getReader();
                suggestions.suggest("]");
                suggestions.suggest(",");

                // just suggest a value
                types.get("T").suggest(context, suggestions);
            })
    );

    public static final ParameterType<TypeIdentifier> TYPE_IDENTIFIER = of(TypeIdentifier.class, "system:type_identifier",
            (context, reader) -> true,
            (context, reader) -> TypeIdentifier.of(reader.collect(c -> c != ' ')),
            (context, builder, identifier) -> builder.append(identifier)
    );

    public static final ParameterType<ParameterType> TYPE = of(ParameterType.class, "system:type",
            (context, reader) -> true,
            (context, reader) -> context.getEngine().getTypeResolver().compile(TYPE_IDENTIFIER.parse(context, reader)),
            (context, builder, o) -> builder.append(o.getIdentifier())
    );

    public static final ParameterType<UUID> UUID = of(java.util.UUID.class, "system:uuid",
            (context, reader) -> true,
            (context, reader) -> java.util.UUID.fromString(reader.collect(c -> c != ' ')),
            (context, builder, uuid) -> builder.append(uuid)
    );

    public static final ParameterType<Class> CLASS = of(Class.class, "system:class",
            (context, reader) -> true,
            ((context, reader) -> {
                String n = reader.collect(c -> c != ' ');
                try {
                    return Class.forName(n);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }),
            (context, builder, aClass) -> builder.append(aClass.getName())
    );

}
