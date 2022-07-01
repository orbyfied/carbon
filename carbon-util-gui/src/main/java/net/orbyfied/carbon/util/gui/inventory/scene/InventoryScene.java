package net.orbyfied.carbon.util.gui.inventory.scene;

import net.orbyfied.carbon.util.gui.inventory.InventoryDisplay;
import net.orbyfied.carbon.util.gui.inventory.InventoryGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A scene to load when switching pages.
 * You can use properties set on the GUI when loading,
 * using these you can make a numbered page
 * system.
 */
public class InventoryScene {

    /**
     * A reference to the GUI.
     */
    InventoryGUI gui;

    /**
     * A custom loader for, for example, dynamically
     * switching pages.
     */
    Consumer<InventoryScene> loader;

    /**
     * The items to load, render and handle.
     */
    List<IGItem> items = new ArrayList<>();

    /**
     * The visual renderer. This won't have any
     * behaviour unless a custom handler is loaded.
     */
    BiConsumer<InventoryScene, InventoryDisplay> renderer;

}
