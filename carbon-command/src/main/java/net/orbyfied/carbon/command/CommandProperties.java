package net.orbyfied.carbon.command;

public class CommandProperties extends AbstractNodeComponent {

    protected String description;

    protected String label;

    protected String usage;

    public CommandProperties(Node node) {
        super(node);
    }

    public String description() {
        return description;
    }

    public CommandProperties description(String str) {
        this.description = str;
        return this;
    }

    public String label() {
        return label;
    }

    public CommandProperties label(String str) {
        this.label = str;
        return this;
    }

    public String usage() {
        return usage;
    }

    public CommandProperties usage(String str) {
        this.usage = str;
        return this;
    }

}
