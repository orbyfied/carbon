package com.github.orbyfied.carbon.command.exception;

import com.github.orbyfied.carbon.command.Node;

public class CommandExecutionException extends CommandException {

    public CommandExecutionException(Node rootCommand, String message) {
        super(rootCommand, message);
    }

    public CommandExecutionException(Node rootCommand, Exception e) {
        super(rootCommand, e);
    }

    public CommandExecutionException(Node rootCommand, String msg, Exception e) {
        super(rootCommand, msg, e);
    }

    @Override
    public String getErrorName() {
        return "execution error";
    }
}
