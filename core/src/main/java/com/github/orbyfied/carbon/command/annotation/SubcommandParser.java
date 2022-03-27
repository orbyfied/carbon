package com.github.orbyfied.carbon.command.annotation;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.parameter.Parameter;
import com.github.orbyfied.carbon.command.parameter.ParameterType;
import com.github.orbyfied.carbon.command.parameter.TypeIdentifier;
import com.github.orbyfied.carbon.registry.Identifier;
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
        Node last = root;
        while (reader.current() != StringReader.DONE) {
            char c1;
            if ((c1 = reader.current()) != '[' && c1 != '<') {
                String component = reader.collect(c -> c != ' ', 1);
                current = current.getOrCreateSubnode(component,
                        parent -> new Node(component, parent, parent.getRoot())
                        .makeExecutable(null));
                last = current;
            } else {
                boolean isReq = reader.current() == '<';
                // TODO: required handling
                reader.next();
                if (reader.current() == StringReader.DONE)
                    break;
                String type = reader.collect(c -> c != ' ', 1);
                String name = reader.collect(c -> c != '>' && c != ']', 1);
                Node paramNode = current.getSubnode(name);
                if (paramNode == null) {
                    TypeIdentifier tid = TypeIdentifier.of(type);
                    ParameterType<?> pt = engine.getTypeResolver().compile(tid);
                    Identifier pid = new Identifier(null, name);
                    paramNode = current.getOrCreateSubnode(name,
                            parent -> new Node(name, parent, parent.getRoot())
                                    .makeParameter(pt).getComponent(Parameter.class)
                                    .setIdentifier(pid)
                                    .getNode());
                }

                reader.next();

                current = paramNode;
            }
        }

        return last;
    }

}
