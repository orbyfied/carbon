package com.github.orbyfied.carbon.util.mcitem;

import com.github.orbyfied.carbon.util.mc.NMSHelper;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

public class MCItems {

    private static final Class<?> craftItemStackClass = NMSHelper.getCraftBukkitClass("inventory.CraftItemStack");
    private static final Field    craftItemStackHandleField;

    static {

        Field tempCraftItemStackHandleField = null;
        try {
            tempCraftItemStackHandleField = craftItemStackClass.getDeclaredField("handle");
            tempCraftItemStackHandleField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            tempCraftItemStackHandleField = null;
        }
        craftItemStackHandleField = tempCraftItemStackHandleField;

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

}
