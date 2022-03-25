package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;

public interface CommandNodeExecutor {

    void execute(Context ctx, Node cmd);

}
