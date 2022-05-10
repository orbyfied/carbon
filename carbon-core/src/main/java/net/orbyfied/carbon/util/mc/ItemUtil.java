package net.orbyfied.carbon.util.mc;

import net.minecraft.world.item.Item;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.util.ReflectionUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import static net.orbyfied.carbon.util.ReflectionUtil.*;

/**
 * Utilities for working with both
 * vanilla items and Carbon items.
 */
public class ItemUtil {

    /* ---- Reflection ---- */

    private static final Class<?> craftItemStackClass = NmsHelper.getCraftBukkitClass("inventory.CraftItemStack");
    private static final Field    craftItemStackHandleField =
            getDeclaredFieldSafe(craftItemStackClass, "handle");
    private static final Constructor<?> newCraftItemStack =
            getDeclaredConstructorSafe(craftItemStackClass,
                    Material.class, Integer.TYPE, Short.TYPE, ItemMeta.class);

    private static final Class<?> craftMagicNumbersClass = NmsHelper.getCraftBukkitClass("util.CraftMagicNumbers");
    private static final Method cmnGetItemMethod =
            getDeclaredMethodSafe(craftMagicNumbersClass,
                    "getItem", Material.class);
    private static final Method cmnGetMaterialMethod =
            getDeclaredMethodSafe(craftMagicNumbersClass,
                    "getMaterial", Item.class);

    /**
     * Creates a new {@code CraftItemStack} with the
     * specified values.
     * @param material The material.
     * @param amount The amount.
     * @param dur The duration
     * @param meta The item meta to put on it.
     * @return The Bukkit stack.
     */
    public static org.bukkit.inventory.ItemStack newCraftStack(
            Material material, int amount, short dur, ItemMeta meta
    ) {
        return ReflectionUtil.newInstance(
                newCraftItemStack,
                material, amount, dur, meta
        );
    }

    /**
     * Gets the NMS handle of a CraftItemStack
     * using reflection
     * @param itemStack The Bukkit stack.
     * @return The NMS stack.
     */
    public static ItemStack getHandle(org.bukkit.inventory.ItemStack itemStack) {
        return ReflectionUtil.queryFieldSafe(itemStack, craftItemStackHandleField);
    }

    /**
     * Checks if the provided stack is null or empty.
     * @param stack The stack to check.
     * @return If it is empty.
     */
    public static boolean isEmpty(CompiledStack stack) {
        return stack == null || stack.isEmpty();
    }

    /**
     * Checks if the provided stack is null or empty.
     * @param stack The stack to check.
     * @return If it is empty.
     */
    public static boolean isEmpty(ItemStack stack) {
        return stack == null ||
                stack.getItem() == Items.AIR ||
                stack.getCount() == 0;
    }

    /**
     * Checks if the provided stack is null or empty.
     * @param stack The stack to check.
     * @return If it is empty.
     */
    public static boolean isEmpty(org.bukkit.inventory.ItemStack stack) {
        return stack == null ||
                stack.getType() == Material.AIR ||
                stack.getAmount() == 0;
    }

    /**
     * Method for checking if two NMS item stacks
     * are equal. (have the same content) For some
     * reason Mojang doesn't override this by default.
     * @param a Stack A.
     * @param b Stack B.
     * @return If they are equal.
     */
    public static boolean equalsNmsStack(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        return a.getItem() == b.getItem() &&
                a.getCount() == b.getCount() &&
                Objects.equals(a.tag, b.tag);
    }

    /**
     * Method for turning the data of an NMS item
     * stack into a hash code. For some reason also
     * not overloaded by default.
     * @param stack The stack to hash.
     * @return The hash code.
     */
    public static int hashNmsStack(ItemStack stack) {
        if (stack == null)
            return 0;

        int hash = stack.getCount();
        hash = hash * 31 + stack.getItem().hashCode();
        hash = hash * 31 + Objects.hashCode(stack.tag);
        return hash;
    }

    /**
     * Gets the material corresponding to
     * the provided NMS item type.
     * @param item The NMS item type.
     * @return The Bukkit material.
     */
    public static Material getMaterial(Item item) {
        return invokeSafe(cmnGetMaterialMethod, null, item);
    }

    /**
     * Gets the NMS item type corresponding
     * to the provided Bukkit material.
     * @param material The Bukkit material.
     * @return The NMS item type.
     */
    public static Item getItem(Material material) {
        return invokeSafe(cmnGetItemMethod, null, material);
    }

    /**
     * The enchantment ID for the glint enchant.
     */
    public static final String GLINT_FAKE_ENCH_ID = "misc:glint";

    /**
     * Sets if an item has glint through providing
     * the NBT tag.
     * @param tag The item tag.
     * @param b If it should glint.
     */
    public static void setHasGlint(CompoundTag tag, boolean b) {
        ListTag enchantmentList = Nbt.getOrCreateList(tag, "Enchantments", CompoundTag.TAG_COMPOUND);

        if (b) {
            CompoundTag enchTag = new CompoundTag();
            enchTag.putString("id", GLINT_FAKE_ENCH_ID);
            enchTag.putInt("lvl", 1);
            enchantmentList.add(enchTag);
        } else {
            int theIndex = -1;
            int i = 0;
            for (Tag t : enchantmentList) {
                if (t instanceof CompoundTag ench) {
                    if (ench.getString("id").equals(GLINT_FAKE_ENCH_ID)) {
                        theIndex = i;
                        break;
                    }
                }

                i++;
            }
            if (theIndex != -1)
                enchantmentList.remove(i);
        }
    }

    /**
     * Sets if an item has glint.
     * @param stack The Bukkit stack.
     * @param b If it should glint.
     */
    public static void setHasGlint(org.bukkit.inventory.ItemStack stack, boolean b) {
        setHasGlint(Objects.requireNonNull(getHandle(stack)).getOrCreateTag(), b);
    }

}
