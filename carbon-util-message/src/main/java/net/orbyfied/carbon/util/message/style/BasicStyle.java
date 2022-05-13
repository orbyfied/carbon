package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;

public class BasicStyle implements Style {

    protected Boolean resetBefore;
    protected Boolean resetAfter;

    protected Style color;

    protected Boolean bold;
    protected Boolean underline;
    protected Boolean italic;
    protected Boolean magic;

    @Override
    public void write(String text, StringBuilder dest) {
        if (resetBefore)
            dest.append(ChatColor.RESET);

        StringBuilder txt = new StringBuilder();

        if (bold)
            txt.append(ChatColor.BOLD);
        if (underline)
            txt.append(ChatColor.UNDERLINE);
        if (italic)
            txt.append(ChatColor.ITALIC);
        if (magic)
            txt.append(ChatColor.MAGIC);

        color.write(txt.toString(), dest);

        if (resetAfter)
            dest.append(ChatColor.RESET);
    }

    //////////////////////////////////


    public Boolean getResetBefore() {
        return resetBefore;
    }

    public BasicStyle setResetBefore(Boolean resetBefore) {
        this.resetBefore = resetBefore;
        return this;
    }

    public Boolean getResetAfter() {
        return resetAfter;
    }

    public BasicStyle setResetAfter(Boolean resetAfter) {
        this.resetAfter = resetAfter;
        return this;
    }

    public Boolean getBold() {
        return bold;
    }

    public BasicStyle setBold(Boolean bold) {
        this.bold = bold;
        return this;
    }

    public Boolean getUnderline() {
        return underline;
    }

    public BasicStyle setUnderline(Boolean underline) {
        this.underline = underline;
        return this;
    }

    public Boolean getItalic() {
        return italic;
    }

    public BasicStyle setItalic(Boolean italic) {
        this.italic = italic;
        return this;
    }

    public Boolean getMagic() {
        return magic;
    }

    public BasicStyle setMagic(Boolean magic) {
        this.magic = magic;
        return this;
    }

    public Style getColor() {
        return color;
    }

    public BasicStyle setColor(Style color) {
        this.color = color;
        return this;
    }

}
