package com.github.orbyfied.carbon.command.exception;

import com.github.orbyfied.carbon.command.Node;
import net.md_5.bungee.api.ChatColor;

import java.util.StringJoiner;

public class CommandException extends RuntimeException {

    protected final Node rootCommand;

    public CommandException(Node rootCommand, String message) {
        super(message);
        this.rootCommand = rootCommand;
    }

    public CommandException(Node rootCommand, Exception e) {
        super(e);
        this.rootCommand = rootCommand;
    }

    public CommandException(Node rootCommand, String msg, Exception e) {
        super(msg, e);
        this.rootCommand = rootCommand;
    }

    public Node getRootCommand() {
        return rootCommand;
    }

    public String getFormattedPrefix() {
        boolean isWarning = this instanceof Warning;
        ChatColor c = ChatColor.RED;
        if (isWarning)
            c = ChatColor.GOLD;
        return c + (isWarning ? "⚠" : ChatColor.BOLD + "×") + " " + getClass().getSimpleName() +
                (getCause() != null ? "(" + getCause().getClass().getSimpleName() + ")" : "")
                + ChatColor.DARK_GRAY + " in command(" + rootCommand.getName() + ")";
    }

    public String getFormattedSuffix() {
        return ": " + ChatColor.YELLOW + getMessage() + (getCause() != null ? ": " + getCause().getMessage() : "");
    }

    public String getFormattedString() {
        return getFormattedPrefix() + getFormattedSuffix();
    }

}
