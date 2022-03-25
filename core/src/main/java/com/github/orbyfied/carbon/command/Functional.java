package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public interface Functional extends NodeComponent {

    void walked(Context ctx, StringReader reader);

    void execute(Context ctx);

}
