package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public interface Functional extends NodeComponent {

    void walked(Invocation ctx, StringReader reader);

    void execute(Invocation ctx);

}
