package net.orbyfied.carbon.command;

import net.orbyfied.carbon.util.StringReader;

public interface Selecting extends NodeComponent {

    boolean selects(Context ctx, StringReader reader);

}
