package net.orbyfied.carbon.util.gui.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.carbon.util.ReflectionUtil;
import net.orbyfied.carbon.util.data.Pair;
import net.orbyfied.carbon.util.mc.NmsHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.BiFunction;

public class InventoryDisplay {

    private static final HashMap<Pair<InventoryType, Integer>, Integer> widthMapping = new HashMap<>();

    public static void registerWidthMapping(InventoryType type, int size, int w) {
        widthMapping.put(new Pair<>(type, size), w);
    }

    public static int getWidth(Inventory inventory) {
        return widthMapping.get(new Pair<>(inventory.getType(), inventory.getSize()));
    }

    public static int getHeight(Inventory inventory) {
        return inventory.getSize() / widthMapping.get(new Pair<>(inventory.getType(), inventory.getSize()));
    }

    public static int getHeight(int w, Inventory inventory) {
        return inventory.getSize() / w;
    }

    private static final Class<?> craftInventoryClass = NmsHelper.getCraftBukkitClass("inventory.CraftInventory");
    private static final Field    craftInvContainerField = ReflectionUtil.getDeclaredFieldSafe(craftInventoryClass, "inventory");

    //////////////////////////////////////////////

    final Inventory inventory;
    SimpleContainer container;

    final int w;
    final int h;

    public void setupReflect() {
        this.container = ReflectionUtil.queryFieldSafe(inventory, craftInvContainerField);
    }

    public InventoryDisplay(Inventory inventory) {
        this.inventory = inventory;
        this.w = getWidth(inventory);
        this.h = getHeight(w, inventory);
        setupReflect();
    }

    public InventoryDisplay(String title, int size) {
        this(Bukkit.createInventory(null, size, title));
    }

    public InventoryDisplay(String title, InventoryType type) {
        this(Bukkit.createInventory(null, type, title));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public InventoryDisplay open(Player player) {
        player.openInventory(inventory);
        return this;
    }

    public InventoryDisplay close(Player player) {
        if (player.getOpenInventory().getTopInventory() == inventory)
            player.closeInventory();
        return this;
    }

    /* --------- Render ---------- */

    BiFunction<Integer, Integer, ItemStack> shader;

    public InventoryDisplay flush() {
        container.setChanged();
        return this;
    }

    public InventoryDisplay setShader(BiFunction<Integer, Integer, ItemStack> shader) {
        this.shader = shader;
        return this;
    }

    public int flatten(int x, int y) {
        return y * w + x;
    }

    public InventoryDisplay drawItem(int x, int y, ItemStack item) {
        container.items.set(flatten(x, y), item);
        return this;
    }

    public InventoryDisplay drawItem(int x, int y) {
        container.items.set(flatten(x, y), shader.apply(x, y));
        return this;
    }

    public InventoryDisplay fillRect(int x, int y, int x2, int y2) {
        for (; x < x2; x++)
            for (; y < y2; y++)
                drawItem(x, y, shader.apply(x, y));
        return this;
    }

    public InventoryDisplay drawRect(int x, int y, int x2, int y2) {
        for (int xi = x; xi < x2; xi++)
            drawItem(xi, y, shader.apply(xi, y));
        for (int xi = x; xi < x2; xi++)
            drawItem(xi, y2, shader.apply(xi, y2));
        y++;
        y2--;
        for (int yi = y; yi < y2; yi++)
            drawItem(x, yi, shader.apply(x, yi));
        for (int yi = y; yi < y2; yi++)
            drawItem(x2, yi, shader.apply(x2, yi));
        return this;
    }

}
