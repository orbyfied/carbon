package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.ReflectionUtil;
import com.github.orbyfied.carbon.util.StringReader;

import java.util.*;
import java.util.function.Consumer;

public class Node {

    protected final ArrayList<AbstractNodeComponent> components = new ArrayList<>();

    protected final HashMap<Class<?>, AbstractNodeComponent> componentsByClass = new HashMap<>();

    protected final ArrayList<Node> children = new ArrayList<>();

    /**
     * NOTE: Only subcommands will be stored here, no
     * parameters as they have nothing to be mapped to.
     */
    protected final HashMap<String, Node> fastMappedChildren = new HashMap<>();

    protected final String name;

    protected final Node parent;

    public Node(final String name,
                final Node parent) {
        this.name   = name;
        this.parent = parent;
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

    /* Components. */

    public <T extends AbstractNodeComponent> T addComponent(T component) {
        Objects.requireNonNull(component, "component cannot be null");
        components.add(component);
        ReflectionUtil.walkParents(component.getClass(), c ->
                componentsByClass.put(c, component));
        return component;
    }

    public <T extends AbstractNodeComponent> Node addComponent(T component, Consumer<T> consumer) {
        consumer.accept(addComponent(component));
        return this;
    }

    public Node removeComponent(AbstractNodeComponent component) {
        components.remove(component);
        ReflectionUtil.walkParents(component.getClass(), c ->
                componentsByClass.remove(c, component));
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

    public Node removeChild(Node node) {
        children.remove(node);
        if (node.componentsByClass.containsKey(Executable.class))
            fastMappedChildren.remove(node.name);
        return this;
    }

    public Node getSubcommandByName(String str) {
        return fastMappedChildren.get(str);
    }

    public Selecting getSubcommand(StringReader reader) {
        Node node;
        if ((node = fastMappedChildren.get(reader.branch().collect(c -> c != ' '))) != null)
            return node.getComponentOf(Selecting.class);
        Selecting sel;
        for (Node child : children)
            if ((sel = child.getComponentOf(Selecting.class)).selects(reader.branch()))
                return sel;
        return null;
    }

}
