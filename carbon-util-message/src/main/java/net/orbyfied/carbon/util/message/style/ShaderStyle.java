package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.Context;
import net.orbyfied.carbon.util.message.style.color.Shaders;
import net.orbyfied.carbon.util.message.writer.CTF;
import net.orbyfied.carbon.util.message.writer.ComponentTransform;
import net.orbyfied.carbon.util.message.writer.EffectKey;
import net.orbyfied.carbon.util.message.writer.MessageWriter;

import java.awt.*;
import java.util.Map;
import java.util.function.Consumer;

public class ShaderStyle implements Style {

    // transforms
    public static final Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> TRANSFORMS =
            ComponentTransform.transforms(
                    // to string
                    ShaderStyle.class, String.class, Styled.class, (CTF<Styled, ShaderStyle, String>)
                            ShaderStyle::transformString
            );

    ////////////////////////////////

    protected TextShader shader;
    protected BasicFormattedStyle formatting;

    public ShaderStyle shader(TextShader shader) {
        this.shader = shader;
        return this;
    }

    public ShaderStyle formatting(BasicFormattedStyle format) {
        this.formatting = format;
        return this;
    }

    public ShaderStyle formatting(Consumer<BasicFormattedStyle> consumer) {
        if (formatting == null)
            formatting = new BasicFormattedStyle();

        consumer.accept(formatting);
        return this;
    }

    public BasicFormattedStyle formatting() {
        return formatting;
    }

    public ShaderStyle oneColor(Color color) {
        shader = Shaders.oneColor(color);
        return this;
    }

    public ShaderStyle oneColor(ChatColor color) {
        shader = Shaders.oneColor(color);
        return this;
    }

    public TextShader shader() {
        return shader;
    }

    ////////////////////////////////

    private static final float ESTIMATED_GROWTH_FACTOR = 1.7f;

    public static String transformString(Context ctx, MessageWriter<String, ?> writ, Styled comp, ShaderStyle eff, String curr) {

        StringBuilder b = new StringBuilder((int) (curr.length() * ESTIMATED_GROWTH_FACTOR));

        final TextShader shader = eff.shader;
        final String     fpref  = BasicFormattedStyle.prefixString(eff.formatting);

        int w = 0;
        int h = 0;

        String[] lines = curr.split("\n");
        int ll = lines.length;
        for (int i = 0; i < ll; i++, h++) {
            String line = lines[i];
            if (line.length() > w)
                w = line.length();
        }

        // iterate
        int x = 0;
        int y = 0;

        int l = curr.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = curr.charAt(i);

            // get special characters
            if (c == '\n' || c == '\r') {
                x = 0;
                y++;
                continue;
            }

            // get color
            ChatColor col = shader.getColor(x, y, w, h, c);

            x++;

            if (col == null) { // special: dont do anything
                b.append(c);
                continue;
            }

            // append everything
            b.append(col).append(fpref).append(c);

        }

        return b.toString();

    }
}