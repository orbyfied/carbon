package net.orbyfied.carbon.command.exception;

import net.orbyfied.carbon.command.ErrorLocation;
import net.orbyfied.carbon.command.Node;
import net.md_5.bungee.api.ChatColor;

public class CommandParseException extends CommandException {

    protected ErrorLocation location;

    public CommandParseException(Node rootCommand, ErrorLocation loc, String message) {
        super(rootCommand, message);
        this.location = loc;
    }

    public CommandParseException(Node rootCommand, ErrorLocation loc, Exception e) {
        super(rootCommand, e);
        this.location = loc;
    }

    public CommandParseException(Node rootCommand, ErrorLocation loc, String msg, Exception e) {
        super(rootCommand, msg, e);
        this.location = loc;
    }

    @Override
    public boolean isSevere() {
        return false;
    }

    public ErrorLocation getLocation() {
        return location;
    }

    @Override
    public String getErrorName() {
        return "command parse error";
    }

    @Override
    public String getFormattedString() {
        return super.getFormattedPrefix() + ChatColor.WHITE + " at " + getLocationString(location) + getFormattedSuffix();
    }

    public static String getLocationString(ErrorLocation loc) {
        // create builder
        StringBuilder b = new StringBuilder();

        // append prefix
        b.append(ChatColor.GREEN).append("...");

        // append index
        b.append(ChatColor.GRAY).append("[").append(loc.getStartIndex()).append(":").append(loc.getEndIndex()).append("]")
                .append(" ");

        // append substrings
        String str = loc.getReader().getString();
        String subPrefix = str.substring(Math.max(0, loc.getStartIndex() - 4), loc.getStartIndex());
        String sub       = str.substring(loc.getStartIndex(), loc.getEndIndex());
        String subSuffix = str.substring(Math.min(str.length() - 1, loc.getEndIndex()), Math.min(str.length() - 1, loc.getEndIndex() + 4));
        b.append(ChatColor.GREEN).append(subPrefix);
        b.append(ChatColor.RED).append(ChatColor.UNDERLINE).append(sub);
        b.append(ChatColor.GREEN).append(subSuffix);

        // append suffix
        b.append(ChatColor.GREEN).append("...");

        // return
        return b.toString();
    }

}
