package net.orbyfied.carbon.util.gui.inventory;

public abstract class AbstractIGComponent implements IGComponent {

    final InventoryGUI gui;

    public AbstractIGComponent(InventoryGUI gui) {
        this.gui = gui;
    }

    @Override
    public InventoryGUI getGUI() {
        return gui;
    }

}
