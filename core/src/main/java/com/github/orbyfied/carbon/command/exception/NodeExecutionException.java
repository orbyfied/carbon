package com.github.orbyfied.carbon.command.exception;

import com.github.orbyfied.carbon.command.Node;
import net.md_5.bungee.api.ChatColor;

public class NodeExecutionException extends CommandExecutionException {

    protected final Node node;

    public NodeExecutionException(Node rootCommand, Node node, String message) {
        super(rootCommand, message);
        this.node = node;
    }

    public NodeExecutionException(Node rootCommand, Node node, Throwable e) {
        super(rootCommand, e);
        this.node = node;
    }

    public NodeExecutionException(Node rootCommand, Node node, String msg, Throwable e) {
        super(rootCommand, msg, e);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String getFormattedPrefix() {
        return super.getFormattedPrefix() + ChatColor.GRAY + " @ " + node.getName();
    }

    @Override
    public String getErrorName() {
        return "node execution error";
    }

}
