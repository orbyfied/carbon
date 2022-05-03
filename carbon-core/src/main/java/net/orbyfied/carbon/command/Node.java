package net.orbyfied.carbon.command;

import net.orbyfied.carbon.command.impl.CommandNodeExecutor;
import net.orbyfied.carbon.command.impl.DefaultSuggester;
import net.orbyfied.carbon.command.parameter.Parameter;
import net.orbyfied.carbon.command.parameter.ParameterType;
import net.orbyfied.carbon.util.ReflectionUtil;
import net.orbyfied.carbon.util.StringReader;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a command node in the
 * command tree. Has components that
 * determine what it is and can do.
 */
public class Node {

    /**
     * The components stored in a linear list.
     */
    protected final ArrayList<NodeComponent> components = new ArrayList<>();

    /**
     * The components mapped by class.
     * Includes all parent classes of a
     * component which are not annotated
     * with {@link NonComponent}
     */
    protected final HashMap<Class<?>, NodeComponent> componentsByClass = new HashMap<>();

    /**
     * The children (subnodes/subcommands) of this node.
     */
    protected final ArrayList<Node> children = new ArrayList<>();

    /**
     * The children (subnodes/subcommands) of this node
     * mapped by name.
     */
    protected final HashMap<String, Node> childrenByName = new HashMap<>();

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

    /**
     * The root node of this tree.
     */
    protected Node root;

    /** Constructor. */
    public Node(final String name,
                final Node parent,
                final Node root) {
        this.name   = name;
        this.parent = parent;
        this.root = Objects.requireNonNullElse(root, this);
        addComponent(new DefaultSuggester(this));
    }

    /* Getters. */

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Map<String, Node> getFastMappedChildren() {
        return Collections.unmodifiableMap(fastMappedChildren);
    }

    public List<NodeComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public Map<Class<?>, NodeComponent> getComponentsByClass() {
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

    public Node getRoot() {
        return root;
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

    public <T extends NodeComponent> T makeComponent(Function<Node, T> constructor) {
        return addComponent(constructor.apply(this));
    }

    public <T extends NodeComponent> Node makeComponent(Function<Node, T> constructor,
                                                       Consumer<T> consumer) {
        T it = addComponent(constructor.apply(this));
        if (consumer != null)
            consumer.accept(it);
        return this;
    }

    public <T extends NodeComponent> T addComponent(T component) {
        Objects.requireNonNull(component, "component cannot be null");
        components.add(component);
        ReflectionUtil.walkParents(component.getClass(),
                c -> !c.isAssignableFrom(NonComponent.class),
                c -> componentsByClass.put(c, component));
        return component;
    }

    public <T extends NodeComponent> Node addComponent(T component, Consumer<T> consumer) {
        T c = addComponent(component);
        if (consumer != null)
            consumer.accept(c);
        return this;
    }

    public Node removeComponent(NodeComponent component) {
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
        childrenByName.put(node.getName(), node);
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
        childrenByName.remove(node.getName());
        if (node.componentsByClass.containsKey(Executable.class))
            fastMappedChildren.remove(node.name);
        return this;
    }

    public Node getSubnode(String name) {
        return childrenByName.get(name);
    }

    public Node getOrCreateSubnode(String name, Function<Node, Node> constructor) {
        Node node;
        if ((node = getSubnode(name)) != null)
            return node;
        node = constructor.apply(this);
        addChild(node);
        return node;
    }

    public Selecting getSubnode(Context ctx, StringReader reader) {
        if (reader.current() == StringReader.DONE)
            return null;
        Node node;
        if ((node = fastMappedChildren.get(reader.branch().collect(c -> c != ' '))) != null)
            return node.getComponentOf(Selecting.class);
        Selecting sel;
        for (Node child : children)
            if ((sel = child.getComponentOf(Selecting.class)).selects(ctx, reader.branch()))
                return sel;
        return null;
    }

    /* QOL Methods. */

    public Node makeExecutable(CommandNodeExecutor executor) {
        addComponent(new Executable(this)).setExecutor(executor);
        return this;
    }

    public Node makeExecutable(CommandNodeExecutor executor, CommandNodeExecutor walked) {
        addComponent(new Executable(this)).setExecutor(executor).setWalkExecutor(walked);
        return this;
    }

    public Node makeParameter(ParameterType<?> type) {
        addComponent(new Parameter(this)).setType(type);
        return this;
    }

    public Node childParameter(String name,
                               ParameterType<?> type) {
        Node node = new Node(name, this, root);
        node.makeParameter(type);
        this.addChild(node);
        return node;
    }

    public Node childParameter(String name,
                               ParameterType<?> type,
                               BiConsumer<Node, Parameter> consumer) {
        Node node = childParameter(name, type);
        if (consumer != null)
            consumer.accept(node, node.getComponent(Parameter.class));
        return this;
    }

    public Node childExecutable(String name, CommandNodeExecutor executor) {
        Node node = new Node(name, this, root);
        node.makeExecutable(executor);
        this.addChild(node);
        return node;
    }

    public Node childExecutable(String name, CommandNodeExecutor executor, CommandNodeExecutor walked) {
        Node node = new Node(name, this, root);
        node.makeExecutable(executor, walked);
        this.addChild(node);
        return node;
    }

}
