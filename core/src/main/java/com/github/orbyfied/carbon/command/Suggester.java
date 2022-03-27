package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public interface Suggester extends NodeComponent {

    void suggestNext(Context ctx,
                     Suggestions builder,
                     StringReader reader,
                     Node next);

}
