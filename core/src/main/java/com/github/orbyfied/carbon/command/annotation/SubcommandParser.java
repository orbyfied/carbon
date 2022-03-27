package com.github.orbyfied.carbon.command.annotation;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.parameter.ParameterType;
import com.github.orbyfied.carbon.command.parameter.TypeIdentifier;
import com.github.orbyfied.carbon.util.StringReader;
import org.checkerframework.checker.units.qual.N;

public class SubcommandParser {

    protected final CommandEngine engine;

    protected final Node root;

    protected final String raw;

    public SubcommandParser(CommandEngine engine,
                            Node root,
                            String raw) {
        this.engine = engine;
        this.root   = root;
        this.raw    = raw;
    }

    public CommandEngine getEngine() {
        return engine;
    }

    public Node getRoot() {
        return root;
    }

    public String getRaw() {
        return raw;
    }

    public Node parse() {
        StringReader reader = new StringReader(raw, 0);
        Node current = root;
        while (reader.current() != StringReader.DONE) {
            String component = reader.collect(c -> c != ' ', 1);
            if (component.length() == 0)
                continue;
            char c1;
            StringReader creader = new StringReader(component, 0);
            if ((c1 = creader.current()) != '[' && c1 != '<') {
                current = current.getOrCreateSubnode(component,
                        parent -> new Node(component, parent, parent.getRoot())
                        .makeExecutable(null));
            }  else {
                boolean isReq = creader.current() == '<';
                // TODO: required handling
                reader.next();
                String name = creader.collect(c -> c != '=');
                Node paramNode = current.getSubnode(name);
                if (paramNode == null) {
                    String type = creader.collect(c -> c != '>' && c != ']');
                    TypeIdentifier tid = TypeIdentifier.of(type);
                    ParameterType<?> pt = engine.getTypeResolver().compile(tid);
                    paramNode = current.getOrCreateSubnode(name,
                            parent -> new Node(name, parent, parent.getRoot()).makeParameter(pt));
                }

                current = paramNode;
            }
        }

        return current;
    }

}
