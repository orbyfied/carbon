package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.crafting.inventory.SlotContainer;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

/**
 * The result generator which is
 * supposed to fill the result slot
 * based on the chosen recipe. If
 * no result generator is provided
 * the recipe will just be returned
 * and won't be processed further.
 *
 * This can be used to select recipes
 * in blocks like furnaces which don't
 * require an instant result supply.
 */
public interface Result {

    void write(
            CraftMatrix matrix,
            SlotContainer out,
            Recipe recipe,
            int amount
            /* TODO */
    );

    int count(
            CraftMatrix matrix,
            SlotContainer out,
            Recipe recipe,
            int amount
    );

    //////////////////////////////////

    private static int maxAvailableAdding(SlotContainer c,
                                          int amount,
                                          int perItem,
                                          int maxStackSize) {
        int fAmount = 0; // optimal amount

        for (Slot slot : c) {
            ItemStack item = slot.getBukkitItem();
            if (ItemUtil.isEmpty(item)) {
                fAmount += maxStackSize;
            } else {
                fAmount += (item.getMaxStackSize() - item.getAmount()) / perItem;
            }
        }

        return Math.min(fAmount, amount);
    }

    static Result ofItem(Material mat, int amt) {
        return new Result() {
            @Override
            public void write(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                out.addItem(new CompiledStack().fill(mat, amt * amount));
            }

            @Override
            public int count(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                return maxAvailableAdding(out, amount, amt, mat.getMaxStackSize());
            }
        };
    }

    static Result ofItem(Item mat, int amt) {
        return new Result() {
            @Override
            public void write(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                out.addItem(new CompiledStack().fill(mat, amt * amount));
            }

            @Override
            public int count(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                return maxAvailableAdding(out, amount, amt, mat.getMaxStackSize());
            }
        };
    }

    static Result ofItem(CarbonItem mat, int amt) {
        return new Result() {
            @Override
            public void write(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                out.addItem(new CompiledStack().fill(mat, amt * amount));
            }

            @Override
            public int count(CraftMatrix matrix, SlotContainer out, Recipe recipe, int amount) {
                return maxAvailableAdding(out, amount, amt, mat.getBaseMaterial().getMaxStackSize());
            }
        };
    }

}
