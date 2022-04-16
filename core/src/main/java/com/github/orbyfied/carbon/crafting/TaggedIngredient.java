package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.item.CompiledStack;

public interface TaggedIngredient extends Ingredient {

    Object tag(CraftMatrix matrix);

    //////////////////////////////////

    static TaggedIngredient of(final Ingredient in,
                               final Object tag) {
        return new TaggedIngredient() {
            @Override
            public Object tag(CraftMatrix matrix) {
                return tag;
            }

            @Override
            public boolean matches(CompiledStack stack, CraftMatrix matrix) {
                return in.matches(stack, matrix);
            }

            @Override
            public boolean equals(Ingredient ingredient) {
                return in.equals(ingredient);
            }

            @Override
            public int count(CompiledStack stack, CraftMatrix matrix) {
                return in.count(stack, matrix);
            }

            @Override
            public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
                in.used(stack, amount, matrix);
            }
        };
    }

}
