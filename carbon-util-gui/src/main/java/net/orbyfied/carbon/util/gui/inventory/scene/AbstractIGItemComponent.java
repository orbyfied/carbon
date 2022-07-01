package net.orbyfied.carbon.util.gui.inventory.scene;

public abstract class AbstractIGItemComponent implements IGItemComponent {

    final IGItem item;

    public AbstractIGItemComponent(IGItem item) {
        this.item = item;
    }

    @Override
    public IGItem getItem() {
        return item;
    }

}
