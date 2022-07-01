package net.orbyfied.carbon.util.gui.inventory.impl;

import net.minecraft.world.item.ItemStack;
import net.orbyfied.carbon.util.functional.TriFunction;
import net.orbyfied.carbon.util.gui.inventory.scene.AbstractIGItemComponent;
import net.orbyfied.carbon.util.gui.inventory.scene.IGItem;
import net.orbyfied.carbon.util.gui.inventory.InventoryDisplay;

import java.util.function.BiConsumer;

public class IGItemRendererComponent extends AbstractIGItemComponent {

    TriFunction<IGItem, Integer, Integer, ItemStack>      shader;
    BiConsumer<IGItemRendererComponent, InventoryDisplay> renderer;

    public IGItemRendererComponent(IGItem item) {
        super(item);
    }

    public IGItemRendererComponent setShader(TriFunction<IGItem, Integer, Integer, ItemStack> shader) {
        this.shader = shader;
        return this;
    }

    public IGItemRendererComponent setRenderer(BiConsumer<IGItemRendererComponent, InventoryDisplay> renderer) {
        this.renderer = renderer;
        return this;
    }

}
