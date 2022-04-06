package com.github.orbyfied.carbon.util.mc;

import com.github.orbyfied.carbon.util.ReflectionUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

import static com.github.orbyfied.carbon.util.ReflectionUtil.getDeclaredConstructorSafe;
import static com.github.orbyfied.carbon.util.ReflectionUtil.getDeclaredFieldSafe;
import static com.github.orbyfied.carbon.util.mc.Nbt.getOrCreateList;

public class ItemUtil {

    private static final Class<?> craftItemStackClass = NmsHelper.getCraftBukkitClass("inventory.CraftItemStack");
    private static final Field    craftItemStackHandleField =
            getDeclaredFieldSafe(craftItemStackClass, "handle");
    private static final Constructor<?> newCraftItemStack =
            getDeclaredConstructorSafe(craftItemStackClass,
                    Material.class, Integer.TYPE, Short.TYPE, ItemMeta.class);

    public static org.bukkit.inventory.ItemStack newCraftStack(
            Material material, int amount, short dur, ItemMeta meta
    ) {
        return ReflectionUtil.newInstance(
                newCraftItemStack,
                material, amount, dur, meta
        );
    }

    // maybeTODO: replace with better method
    // this works for now though
    public static ItemStack getHandle(org.bukkit.inventory.ItemStack itemStack) {
        return ReflectionUtil.queryFieldSafe(itemStack, craftItemStackHandleField);
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

    public static void setHasGlint(org.bukkit.inventory.ItemStack stack, boolean b) {
        setHasGlint(Objects.requireNonNull(getHandle(stack)).getOrCreateTag(), b);
    }

}
