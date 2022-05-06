package net.orbyfied.carbon.command;

import net.orbyfied.carbon.util.StringReader;

public interface Suggester extends NodeComponent {

    void suggestNext(Context ctx,
                     SuggestionAccumulator builder,
                     StringReader reader,
                     Node next);

}
