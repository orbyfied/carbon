package com.github.orbyfied.carbon.command.minecraft;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.command.parameter.ParameterType;
import com.github.orbyfied.carbon.command.parameter.TypeIdentifier;
import com.github.orbyfied.carbon.command.parameter.TypeResolver;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.functional.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class MinecraftParameterType {

    public static final class MinecraftTypeResolver implements TypeResolver {

        protected HashMap<String, ParameterType<?>> types = new HashMap<>();

        @Override
        public ParameterType<?> resolve(Identifier identifier) {
            return types.get(identifier.getPath());
        }

    }

    /** UTILITY CLASS */
    private MinecraftParameterType() { }

    /**
     * The singleton type resolver.
     */
    public static final MinecraftTypeResolver typeResolver = new MinecraftTypeResolver();

    /**
     * Function to quickly create simple
     * parameter types with lambdas.
     * @see ParameterType
     */
    static <T> ParameterType<T> of(final Class<T> klass,
                                   final String baseId,
                                   final BiPredicate<Context, StringReader> acceptor,
                                   final BiFunction<Context, StringReader, T> parser,
                                   final TriConsumer<Context, StringBuilder, T> writer,
                                   final BiConsumer<Context, SuggestionAccumulator> suggester) {
        // parse identifier
        final TypeIdentifier bid = TypeIdentifier.of(baseId);

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
                suggester.accept(context, suggestions);
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

    //////////////////////////////////////////////

    public static final ParameterType<Player> ONLINE_PLAYER_DIRECT = of(Player.class, "minecraft:online_player_direct",
            (context, reader) -> true,
            ((context, reader) -> {
                String s = reader.collect(c -> c != ' ');
                Player player;
                if ((player = Bukkit.getPlayer(s)) != null)
                    return player;
                return Bukkit.getPlayer(UUID.fromString(s));
            }),
            (context, builder, s) -> builder.append(s.getUniqueId()),
            ((context, suggestions) -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.suggest(player.getName());
                    suggestions.suggest(player.getUniqueId());
                }
            })
    );

}
