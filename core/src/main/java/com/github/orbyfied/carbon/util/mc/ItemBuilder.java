package com.github.orbyfied.carbon.util.mc;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Base class for building item stacks.
 * @param <T> The type of item meta.
 * @param <M> The type of meta builder (must have T as type)
 * @param <Self> The return type of itself. (for inheritance)
 */
@SuppressWarnings("unchecked")
public class ItemBuilder<T extends ItemMeta, M extends MetaBuilder<T, M>, Self extends ItemBuilder<T, M, ?>> {

    @SuppressWarnings("all")
    public static <B extends ItemMeta, A extends MetaBuilder<B, A>> ItemBuilder<B, A, ItemBuilder<B, A, ?>> create() {
        return new ItemBuilder<>();
    }

    @SuppressWarnings("all")
    public static <B extends ItemMeta, A extends MetaBuilder<B, A>> ItemBuilder<B, A, ItemBuilder<B, A, ?>> create(Material material, int amt) {
        return (ItemBuilder<B, A, ItemBuilder<B, A, ?>>) new ItemBuilder<>().setMaterial(material).setAmount(amt);
    }

    @SuppressWarnings("all")
    public static <B extends ItemMeta, A extends MetaBuilder<B, A>> ItemBuilder<B, A, ItemBuilder<B, A, ?>> create(Material material) {
        return create(material, 1);
    }

    @SuppressWarnings("all")
    public static <A extends MetaBuilder<ItemMeta, A>> ItemBuilder<ItemMeta, A, ItemBuilder<ItemMeta, A, ?>> of(ItemStack stack) {
        return (ItemBuilder<ItemMeta, A, ItemBuilder<ItemMeta, A, ?>>) new ItemBuilder<>()
                .setMaterial(stack.getType())
                .setAmount(stack.getAmount())
                .setMeta(stack.getItemMeta());
    }

    ////////////////////////////////////////////////////

    /**
     * The material of the item stack.
     */
    protected Material material;

    /**
     * The amount of the item stack.
     */
    protected int amount = 0;

    /**
     * The item meta (builder).
     */
    protected M meta;

    /**
     * Enchantments that will be put onto the item stack.
     */
    protected HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    /** Protected constructor. */
    protected ItemBuilder() { }

    public Self update() {
        if (meta == null)
            this.meta = (M) new MetaBuilder<T, MetaBuilder<T, ?>>(MetaBuilder.makeFor(material));
        return (Self) this;
    }

    public Self setAmount(int amount) {
        this.amount = amount;
        return (Self) this;
    }

    public Self setMaterial(Material material) {
        this.material = material;
        return (Self) this;
    }

    public M meta() {
        return meta;
    }

    public Self meta(Consumer<M> consumer) {
        update();
        consumer.accept(meta);
        return (Self) this;
    }

    public Self meta(BiConsumer<M, T> consumer) {
        update();
        consumer.accept(meta, meta.it);
        return (Self) this;
    }

    public Self setMeta(T meta) {
        this.meta = (M) MetaBuilder.create(meta);
        return (Self) this;
    }

    public Self add(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return (Self) this;
    }

    public Self remove(Enchantment enchantment) {
        enchantments.remove(enchantment);
        return (Self) this;
    }

    public boolean has(Enchantment enchantment) {
        return enchantments.containsKey(enchantment);
    }

    public int getLevel(Enchantment enchantment) {
        return enchantments.get(enchantment);
    }

    public boolean isUnsafe(Enchantment enchantment) {
        return enchantments.get(enchantment) > enchantment.getMaxLevel();
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(material != null ? material : Material.AIR, amount);
        if (meta != null && meta.it != null) stack.setItemMeta(meta.it);
        stack.addUnsafeEnchantments(enchantments);
        return stack;
    }
}
