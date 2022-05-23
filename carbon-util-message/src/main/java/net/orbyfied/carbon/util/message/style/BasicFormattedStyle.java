package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.Context;
import net.orbyfied.carbon.util.message.writer.CTF;
import net.orbyfied.carbon.util.message.writer.ComponentTransform;
import net.orbyfied.carbon.util.message.writer.EffectKey;
import net.orbyfied.carbon.util.message.writer.MessageWriter;

import java.util.Map;

public class BasicFormattedStyle implements Style {

    public static final Map<EffectKey<?, ?>, ComponentTransform<?, ?, ?>> TRANSFORMS =
            ComponentTransform.transforms(
                    // to string
                    BasicFormattedStyle.class, String.class, Styled.class,
                        (CTF<Styled, BasicFormattedStyle, String>) BasicFormattedStyle::transformString
            );

    /////////////////////////////////////

    protected Boolean bold;
    protected Boolean italic;
    protected Boolean underline;
    protected Boolean magic;

    public Boolean getBold() {
        return bold;
    }

    public BasicFormattedStyle setBold(Boolean bold) {
        this.bold = bold;
        return this;
    }

    public Boolean getItalic() {
        return italic;
    }

    public BasicFormattedStyle setItalic(Boolean italic) {
        this.italic = italic;
        return this;
    }

    public Boolean getUnderline() {
        return underline;
    }

    public BasicFormattedStyle setUnderline(Boolean underline) {
        this.underline = underline;
        return this;
    }

    public Boolean getMagic() {
        return magic;
    }

    public BasicFormattedStyle setMagic(Boolean magic) {
        this.magic = magic;
        return this;
    }

    /////////////////////////////////////

    private static boolean is(Boolean bool) {
        return bool != null && bool;
    }

    private static boolean isn(Boolean bool) {
        return bool == null || !bool;
    }

    public static String prefixString(BasicFormattedStyle eff) {
        StringBuilder pre = new StringBuilder();
        if (is(eff.bold))
            pre.append(ChatColor.BOLD);
        if (is(eff.italic))
            pre.append(ChatColor.ITALIC);
        if (is(eff.underline))
            pre.append(ChatColor.UNDERLINE);
        if (is(eff.magic))
            pre.append(ChatColor.MAGIC);
        return pre.toString();
    }

    public static String transformString(Context ctx, MessageWriter<String, ?> writ, Styled comp, BasicFormattedStyle eff, String curr) {
        return prefixString(eff) + curr;
    }

}
