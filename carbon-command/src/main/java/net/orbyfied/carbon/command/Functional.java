package net.orbyfied.carbon.command;

import net.orbyfied.carbon.util.StringReader;

public interface Functional extends NodeComponent {

    void walked(Context ctx, StringReader reader);

    void execute(Context ctx);

}
