package net.orbyfied.carbon.command.exception;

import net.orbyfied.carbon.command.ErrorLocation;
import net.orbyfied.carbon.command.Node;

public class NodeParseWarning extends NodeParseException implements Warning {

    public NodeParseWarning(Node rootCommand, Node node, ErrorLocation loc, String message) {
        super(rootCommand, node, loc, message);
    }

    public NodeParseWarning(Node rootCommand, Node node, ErrorLocation loc, Exception e) {
        super(rootCommand, node, loc, e);
    }

    public NodeParseWarning(Node rootCommand, Node node, ErrorLocation loc, String msg, Exception e) {
        super(rootCommand, node, loc, msg, e);
    }

    @Override
    public String getErrorName() {
        return "node parse warning";
    }
}
