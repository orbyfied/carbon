package com.github.orbyfied.carbon.util.mc;

import com.github.orbyfied.carbon.util.TextFormatting;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Builds item meta's.
 * @param <V>
 * @param <Self>
 */
@SuppressWarnings("unchecked")
public class MetaBuilder<V extends ItemMeta, Self extends MetaBuilder<V, ?>> {

    /** Creates a new item meta for the specified material. */
    public static <T extends ItemMeta> T makeFor(Material material) {
        return (T) Bukkit.getItemFactory().getItemMeta(Objects.requireNonNullElse(material, Material.AIR));
    }

    /**
     * Constructs a new builder with the initial item
     * meta set to the appropriate item meta for the supplied material.
     * @param material The material to create the item meta for.
     *                 For example: If you have a MetaBuilder<CompassMeta>, you
     *                 want to use <code>Material.COMPASS</code> as parameter.
     * @return The new builder.
     */
    public static <T extends ItemMeta> MetaBuilder<T, MetaBuilder<T, ?>> create(Material material) {
        return of(makeFor(material));
    }

    /**
     * Constructs a new builder with the default item meta.
     * @return The new builder.
     */
    public static <T extends ItemMeta> MetaBuilder<T, MetaBuilder<T, ?>> create() {
        return create(Material.AIR);
    }

    /**
     * Constructs a new builder with a <b>clone</b>
     * of the provided item meta as editable meta.
     * @param it The meta.
     * @return The new builder.
     */
    public static <T extends ItemMeta> MetaBuilder<T, MetaBuilder<T, ?>> create(T it) {
        return new MetaBuilder<>((T) it.clone());
    }

    /**
     * Constructs a new builder with a <b>reference</b>
     * of the provided item meta as editable meta.
     * This allows you to use this class to edit already
     * existent item meta's.
     * @param it The item meta.
     * @return The new builder.
     */
    public static <T extends ItemMeta> MetaBuilder<T, MetaBuilder<T, ?>> of(T it) {
        return new MetaBuilder<>(it);
    }

    /////////////////////////////////////////////////

    /**
     * The meta instance.
     */
    protected V it;

    /** Protected constructor. */
    protected MetaBuilder(V it) {
        this.it = it;
    }

    final Self self(Runnable runnable) {
        runnable.run();
        return (Self) this;
    }

    public V get() { return it; }

    public Self setProperty(String name, Object o) {
        try {
            Field f = it.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(it, o);
        } catch (Exception e) { e.printStackTrace(); }
        return (Self) this;
    }

    public <T> T getProperty(String name) {
        try {
            Field f = it.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get(it);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public Self setDisplayName(String name) {
        return self(() -> it.setDisplayName(TextFormatting.translate(name, "&", "&#")));
    }

    public String getDisplayName() {
        return it.getDisplayName();
    }

    public Self setUnbreakable(boolean v) {
        return self(() -> it.setUnbreakable(v));
    }

    public boolean isUnbreakable() {
        return it.isUnbreakable();
    }

}
