package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.inventory.SlotContainer;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CompiledStack;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
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
            SlotContainer out,
            Recipe recipe,
            int amount
            /* TODO */
    );

    //////////////////////////////////

    static Result ofItem(Material mat, int amt) {
        return (out, recipe, amount) -> out.addItem(new CompiledStack().fill(mat, amt * amount));
    }

    static Result ofItem(Item mat, int amt) {
        return (out, recipe, amount) -> out.addItem(new CompiledStack().fill(mat, amt * amount));
    }

    static Result ofItem(CarbonItem mat, int amt) {
        return (out, recipe, amount) -> out.addItem(new CompiledStack().fill(mat, amt * amount));
    }

}
