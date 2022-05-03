package net.orbyfied.carbon.content.pack;

public abstract class PackElementBuilder {

    protected final ResourcePackBuilder parent;

    protected String name;

    public PackElementBuilder(ResourcePackBuilder parent) {
        this.parent = parent;
    }

    public ResourcePackBuilder getParent() {
        return parent;
    }

    public abstract void write();

    public abstract void read();

    public String getName() {
        return name;
    }

    public PackElementBuilder setName(String name) {
        this.name = name;
        return this;
    }

}
