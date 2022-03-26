package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.impl.DefaultSuggester;
import com.github.orbyfied.carbon.command.parameter.Parameter;
import com.github.orbyfied.carbon.command.parameter.ParameterType;
import com.github.orbyfied.carbon.util.ReflectionUtil;
import com.github.orbyfied.carbon.util.StringReader;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a command node in the
 * command tree. Has components that
 * determine what it is and can do.
 */
public class Node {

    /**
     * The components stored in a linear list.
     */
    protected final ArrayList<AbstractNodeComponent> components = new ArrayList<>();

    /**
     * The components mapped by class.
     * Includes all parent classes of a
     * component which are not annotated
     * with {@link NonComponent}
     */
    protected final HashMap<Class<?>, AbstractNodeComponent> componentsByClass = new HashMap<>();

    /**
     * The children (subnodes/subcommands) of this node.
     */
    protected final ArrayList<Node> children = new ArrayList<>();

    /**
     * NOTE: Only subcommands will be stored here, no
     * parameters as they have nothing to be mapped to.
     */
    protected final HashMap<String, Node> fastMappedChildren = new HashMap<>();

    /**
     * The primary name of this node.
     */
    protected final String name;

    /**
     * The aliases of this node.
     */
    protected final List<String> aliases = new ArrayList<>();

    /**
     * The immediate parent of this node.
     */
    protected final Node parent;

    /** Constructor. */
    public Node(final String name,
                final Node parent) {
        this.name   = name;
        this.parent = parent;
        addComponent(new DefaultSuggester(this));
    }

    /* Getters. */

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Map<String, Node> getFastMappedChildren() {
        return Collections.unmodifiableMap(fastMappedChildren);
    }

    public List<AbstractNodeComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public Map<Class<?>, AbstractNodeComponent> getComponentsByClass() {
        return Collections.unmodifiableMap(componentsByClass);
    }

    public Node getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    /* Aliases. */

    public Node addAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public Node removeAlias(String... aliases) {
        this.aliases.removeAll(Arrays.asList(aliases));
        return this;
    }

    /* Components. */

    public <T extends AbstractNodeComponent> T addComponent(T component) {
        Objects.requireNonNull(component, "component cannot be null");
        components.add(component);
        ReflectionUtil.walkParents(component.getClass(),
                c -> !c.isAssignableFrom(NonComponent.class),
                c -> componentsByClass.put(c, component));
        return component;
    }

    public <T extends AbstractNodeComponent> Node addComponent(T component, Consumer<T> consumer) {
        T c = addComponent(component);
        if (consumer != null)
            consumer.accept(c);
        return this;
    }

    public Node removeComponent(AbstractNodeComponent component) {
        components.remove(component);
        ReflectionUtil.walkParents(component.getClass(),
                c -> !c.isAssignableFrom(NonComponent.class),
                c -> componentsByClass.remove(c, component));
        return this;
    }

    public Node removeComponent(Class<?> klass) {
        return removeComponent(componentsByClass.get(klass));
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponentOf(Class<T> klass) {
        return (T) componentsByClass.get(klass);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractNodeComponent> T getComponent(Class<T> klass) {
        return (T) componentsByClass.get(klass);
    }

    public boolean hasComponentOf(Class<?> klass) {
        return componentsByClass.containsKey(klass);
    }

    /* Children. */

    public Node addChild(Node node) {
        Objects.requireNonNull(node, "node cannot be null");
        children.add(node);
        if (node.componentsByClass.containsKey(Executable.class))
            fastMappedChildren.put(node.name, node);
        return node;
    }

    public Node addChild(Node node, Consumer<Node> consumer) {
        consumer.accept(addChild(node));
        return this;
    }

    public Node addParameterChild(String name,
                                  ParameterType<?> type) {
        Node node = new Node(name, this);
        node.addComponent(new Parameter(node)).setType(type);
        this.addChild(node);
        return node;
    }

    public Node addParameterChild(String name,
                                  ParameterType<?> type,
                                  BiConsumer<Node, Parameter> consumer) {
        Node node = addParameterChild(name, type);
        if (consumer != null)
            consumer.accept(node, node.getComponent(Parameter.class));
        return this;
    }

    public Node removeChild(Node node) {
        children.remove(node);
        if (node.componentsByClass.containsKey(Executable.class))
            fastMappedChildren.remove(node.name);
        return this;
    }

    public Node getSubcommandByName(String str) {
        return fastMappedChildren.get(str);
    }

    public Selecting getSubnode(Context ctx, StringReader reader) {
        Node node;
        if ((node = fastMappedChildren.get(reader.branch().collect(c -> c != ' '))) != null)
            return node.getComponentOf(Selecting.class);
        Selecting sel;
        for (Node child : children)
            if ((sel = child.getComponentOf(Selecting.class)).selects(ctx, reader.branch()))
                return sel;
        return null;
    }

}
