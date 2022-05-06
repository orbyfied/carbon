package net.orbyfied.carbon.command.exception;

import net.orbyfied.carbon.command.ErrorLocation;
import net.orbyfied.carbon.command.Node;
import net.md_5.bungee.api.ChatColor;

public class NodeParseException extends CommandParseException {

    protected final Node node;

    public NodeParseException(Node rootCommand, Node node, ErrorLocation loc, String message) {
        super(rootCommand, loc, message);
        this.node = node;
    }

    public NodeParseException(Node rootCommand, Node node, ErrorLocation loc, Exception e) {
        super(rootCommand, loc, e);
        this.node = node;
    }

    public NodeParseException(Node rootCommand, Node node, ErrorLocation loc, String msg, Exception e) {
        super(rootCommand, loc, msg, e);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String getErrorName() {
        return "node parse error";
    }

    @Override
    public String getFormattedPrefix() {
        return super.getFormattedPrefix() + ChatColor.GRAY + " @ " + node.getName();
    }

}
