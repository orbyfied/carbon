package net.orbyfied.carbon.command.annotation;

import net.orbyfied.carbon.command.*;
import net.orbyfied.carbon.command.parameter.Parameter;
import net.orbyfied.carbon.command.parameter.ParameterType;
import net.orbyfied.carbon.command.parameter.TypeIdentifier;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.StringReader;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public class SubcommandParser {

    /**
     * The command engine.
     */
    protected final CommandEngine engine;

    /**
     * The root command node.
     */
    protected final Node root;

    /**
     * The raw string to parse.
     */
    protected final String raw;

    /**
     * The base annotation processor.
     */
    protected final BaseAnnotationProcessor bap;

    public SubcommandParser(BaseAnnotationProcessor bap,
                            CommandEngine engine,
                            Node root,
                            String raw) {
        this.bap    = bap;
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

    /**
     * Parses the descriptor string into
     * an actual node tree, returning the last executable node.
     * @return The last executable node.
     */
    public Node parse() {
        // create string reader
        StringReader reader = new StringReader(raw, 0);

        // iterate
        Node current = root;
        Node last    = root; // the last executable node
        while (reader.current() != StringReader.DONE) {
            char c1;

            // parse executable (subcommand)
            if ((c1 = reader.current()) != '[' && c1 != '<') {
                // collect name
                String component = reader.collect(c -> c != ' ', 1);

                // create and set node
                current = current.getOrCreateSubnode(component,
                        parent -> new Node(component, parent, parent.getRoot())
                        .makeExecutable(null));

                // store state
                last = current;
            } else { // parse parameter
                // check if it is required
                boolean isReq = reader.current() == '<';
                // TODO: required handling

                // skip to next character and check for EOF
                reader.next();
                if (reader.current() == StringReader.DONE)
                    throw new AnnotationProcessingException("Unexpected EOF while parsing parameter @ idx: " + reader.index());

                // collect type and name
                String type = reader.collect(c -> c != ' ', 1);
                String name = reader.collect(c -> c != '>' && c != ']', 1);

                // get or create node
                Node paramNode = current.getSubnode(name);
                if (paramNode == null) {
                    // parse type into type identifier and resolve
                    TypeIdentifier tid = TypeIdentifier.of(type);
                    ParameterType<?> pt = engine.getTypeResolver().compile(tid);

                    // create parameter id
                    Identifier pid = new Identifier(null, name);

                    // create node
                    paramNode = current.getOrCreateSubnode(name,
                            parent -> new Node(name, parent, parent.getRoot())
                                    .makeParameter(pt).getComponent(Parameter.class)
                                    .setIdentifier(pid)
                                    .getNode());
                }

                // skip to next character
                reader.next();

                // store state
                current = paramNode;
            }
        }

        // make sure to always set executable
        if (current.getComponentOf(Selecting.class) == null)
            current.makeExecutable(null);

        // return
        return last;
    }

}
