package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.util.CollectionUtil;

public interface TaggedIngredient extends Ingredient {

    void tag(CraftMatrix matrix, Slot slot);

    //////////////////////////////////

    static TaggedIngredient of(final Ingredient in,
                               final Object tag) {
        return new TaggedIngredient() {
            @Override
            public void tag(CraftMatrix matrix, Slot slot) {
                matrix.put(matrix, slot);
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

    class DelegatingTaggedIngredient implements TaggedIngredient {

        Object[] tags;
        Ingredient in;

        public Object[] getTags() {
            return tags;
        }

        public DelegatingTaggedIngredient setTags(Object[] tags) {
            this.tags = tags;
            return this;
        }

        public DelegatingTaggedIngredient addTag(Object o) {
            tags = CollectionUtil.resize(tags, (int) (tags.length * 1.25));
            tags[tags.length - 1] = o;
            return this;
        }

        @Override
        public void tag(CraftMatrix matrix, Slot slot) {
            for (Object t : tags)
                matrix.put(t, slot);
        }

        @Override
        public boolean matches(CompiledStack stack, CraftMatrix matrix) {
            return in.matches(stack, matrix);
        }

        @Override
        public int count(CompiledStack stack, CraftMatrix matrix) {
            return in.count(stack, matrix);
        }

        @Override
        public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
            in.used(stack, amount, matrix);
        }

        @Override
        public boolean equals(Ingredient ingredient) {
            return in.equals(ingredient);
        }

    }

}
