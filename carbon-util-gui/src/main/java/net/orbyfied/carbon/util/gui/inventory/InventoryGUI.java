package net.orbyfied.carbon.util.gui.inventory;

import net.orbyfied.carbon.util.data.ArrayMultiHashMap;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InventoryGUI {

    final ArrayMultiHashMap<Class<?>, IGComponent> componentsMapped = new ArrayMultiHashMap<>();
    final ArrayList<IGComponent> componentsLinear = new ArrayList<>();

    InventoryDisplay inventoryDisplay;

    public InventoryGUI() {

    }

}
