package com.github.orbyfied.carbon.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.orbyfied.carbon.util.mc.Nbt.getOrCreateList;

public class ItemUtil {

    private static final Class<?> craftItemStackClass = NmsHelper.getCraftBukkitClass("inventory.CraftItemStack");
    private static final Field    craftItemStackHandleField;
    private static final Constructor<?> newCraftItemStack;

    static {

        Field tempCraftItemStackHandleField;
        Constructor<?> tempNewCraftItemStack;
        try {
            tempCraftItemStackHandleField = craftItemStackClass.getDeclaredField("handle");
            tempCraftItemStackHandleField.setAccessible(true);
            tempNewCraftItemStack = craftItemStackClass.getDeclaredConstructor(
                Material.class, Integer.TYPE, Short.TYPE, ItemMeta.class
            );
            tempNewCraftItemStack.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            tempCraftItemStackHandleField = null;
            tempNewCraftItemStack = null;
        }
        craftItemStackHandleField = tempCraftItemStackHandleField;
        newCraftItemStack = tempNewCraftItemStack;
    }

    public static org.bukkit.inventory.ItemStack newCraftStack(
            Material material, int amount, short dur, ItemMeta meta
    ) {
        try {
            return (org.bukkit.inventory.ItemStack) newCraftItemStack.newInstance(material, amount, dur, meta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: replace with better method
    // this works for now though
    public static ItemStack getHandle(org.bukkit.inventory.ItemStack itemStack) {
        // check if the item is a CraftItemStack
        if (!craftItemStackClass.isAssignableFrom(itemStack.getClass()))
            throw new IllegalArgumentException();

        // get field
        try {
            return (ItemStack) craftItemStackHandleField.get(itemStack);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String GLINT_FAKE_ENCH_ID = "misc:glint";

    public static void setHasGlint(CompoundTag tag, boolean b) {
        ListTag enchantmentList = getOrCreateList(tag, "Enchantments", CompoundTag.TAG_COMPOUND);

        if (b) {
            CompoundTag enchTag = new CompoundTag();
            enchTag.putString("id", GLINT_FAKE_ENCH_ID);
            enchTag.putInt("lvl", 1);
            enchantmentList.add(enchTag);
        } else {
            int theIndex = -1;
            int i = 0;
            for (Tag t : enchantmentList) {
                if (t instanceof CompoundTag ench)
                    if (ench.getString("id").equals(GLINT_FAKE_ENCH_ID))
                        theIndex = i;
                i++;
            }
            if (theIndex != -1)
                enchantmentList.remove(i);
        }
    }

    public static void setHasGlint(org.bukkit.inventory.ItemStack stack, boolean b) {
        setHasGlint(Objects.requireNonNull(getHandle(stack)).getOrCreateTag(), b);
    }

}
