package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.item.CompiledStack;
import net.minecraft.world.item.Items;

public interface Ingredient {

    boolean matches(CompiledStack stack);

    boolean equals(Ingredient ingredient);

    int count(CompiledStack stack);

    void used(CompiledStack stack, int amount);

    ////////////////////////////////////////

    /**
     * An empty slot.
     */
    Ingredient EMPTY = new Ingredient() {

        @Override
        public boolean matches(CompiledStack stack) {
            return stack == null ||
                    stack.getAmount() == 0 ||
                    stack.getMinecraftItem() == Items.AIR;
        }

        @Override
        public boolean equals(Ingredient ingredient) {
            return ingredient == this;
        }

        @Override
        public int count(CompiledStack stack) {
            // ignore, empty slots should not be accounted for in amount counting
            return -1;
        }

        @Override
        public void used(CompiledStack stack, int amount) {
            // ignore, empty slots should not be accounted for in amount counting
        }

    };

}
