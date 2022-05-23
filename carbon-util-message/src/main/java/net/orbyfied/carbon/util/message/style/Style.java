package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.Slice;
import net.orbyfied.carbon.util.message.writer.CTF;
import net.orbyfied.carbon.util.message.writer.ComponentTransform;
import net.orbyfied.carbon.util.message.writer.EffectKey;

import java.util.Map;

public interface Style {

    static BasicFormattedStyle formatted() {
        return new BasicFormattedStyle();
    }

    static ShaderStyle shader(TextShader shader) {
        return new ShaderStyle().shader(shader);
    }

    static ShaderStyle shader() {
        return new ShaderStyle();
    }

    Style EMPTY = new Empty();

    class Empty implements Style {
        // transforms
        public static final Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> TRANSFORMS =
                ComponentTransform.transforms(
                        // to string
                        Empty.class, String.class, Slice.class, (CTF<Slice, Empty, String>)
                                (ctx, writ, comp, eff, curr) -> ChatColor.RESET + curr
                );
    }

    Style KEEP = new Keep();

    class Keep implements Style {
        // transforms
        public static final Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> TRANSFORMS =
                ComponentTransform.transforms(
                        // to string
                        Keep.class, String.class, Slice.class, (CTF<Slice, Keep, String>)
                                (ctx, writ, comp, eff, curr) -> curr
                );
    }

}
