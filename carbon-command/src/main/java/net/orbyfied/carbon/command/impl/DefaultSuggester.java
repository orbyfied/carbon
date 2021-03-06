package net.orbyfied.carbon.command.impl;

import net.orbyfied.carbon.command.*;
import net.orbyfied.carbon.util.StringReader;

public class DefaultSuggester
        extends AbstractNodeComponent
        implements Suggester {

    public DefaultSuggester(Node node) {
        super(node);
    }

    @Override
    public void suggestNext(Context ctx,
                            SuggestionAccumulator builder,
                            StringReader reader,
                            Node next) {
        if (next == null) {
            for (Node c : node.getChildren())
                c.getComponentOf(Completer.class).completeSelf(ctx, node, builder);
        } else {
            next.getComponentOf(Completer.class).completeSelf(ctx, next, builder);
        }
    }

}
