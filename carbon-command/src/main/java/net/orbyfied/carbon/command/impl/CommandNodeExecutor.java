package net.orbyfied.carbon.command.impl;

import net.orbyfied.carbon.command.Context;
import net.orbyfied.carbon.command.Node;

public interface CommandNodeExecutor {

    void execute(Context ctx, Node cmd);

}
