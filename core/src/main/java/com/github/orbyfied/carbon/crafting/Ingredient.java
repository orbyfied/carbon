package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.item.CompiledStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;

public interface Ingredient {

    boolean matches(CompiledStack stack, CraftMatrix matrix);

    int count(CompiledStack stack, CraftMatrix matrix);

    void used(CompiledStack stack, int amount, CraftMatrix matrix);

    boolean equals(Ingredient ingredient);

    ////////////////////////////////////////

    /**
     * An empty slot.
     */
    Ingredient EMPTY = new Ingredient() {

        @Override
        public boolean matches(CompiledStack stack, CraftMatrix matrix) {
            return stack == null ||
                    stack.getAmount() == 0 ||
                    stack.getItemType() == Items.AIR;
        }

        @Override
        public boolean equals(Ingredient ingredient) {
            return ingredient == this;
        }

        @Override
        public int count(CompiledStack stack, CraftMatrix matrix) {
            // ignore, empty slots should not be accounted for in amount counting
            return -1;
        }

        @Override
        public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
            // ignore, empty slots should not be accounted for in amount counting
        }

        @Override
        public String toString() {
            return "IngredientEMPTY";
        }
    };

    static Ingredient ofItem(Material material, int amt) {
        Item item = CraftMagicNumbers.getItem(material);
        return new Ingredient() {
            @Override
            public boolean matches(CompiledStack stack, CraftMatrix matrix) {
                return stack.getItemType() == item;
            }

            @Override
            public boolean equals(Ingredient ingredient) {
                return false;
            }

            @Override
            public int count(CompiledStack stack, CraftMatrix matrix) {
                return stack.getAmount() / amt;
            }

            @Override
            public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
                System.out.println("hi! stack: " + stack + ", amount: " + amount + ", amtreq: " + amt);
                stack.getStack().setCount(stack.getAmount() - amount * amt);
            }
        };
    }

}
