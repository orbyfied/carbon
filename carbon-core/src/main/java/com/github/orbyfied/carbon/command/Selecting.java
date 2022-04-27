package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public interface Selecting extends NodeComponent {

    boolean selects(Context ctx, StringReader reader);

}
