package net.orbyfied.carbon.command;

/**
 * An extension to a node.
 */
public abstract class AbstractNodeComponent implements NodeComponent {

    /**
     * The node that holds this
     * component.
     */
    protected final Node node;

    /**
     * Constructor.
     * @param node The node.
     */
    public AbstractNodeComponent(Node node) {
        this.node = node;
        if (!node.hasComponentOf(this.getClass()))
            node.addComponent(this);
    }

    /**
     * Get the node that holds
     * this component.
     * @return The node.
     */
    @Override
    public Node getNode() {
        return node;
    }

}
