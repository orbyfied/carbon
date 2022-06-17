package net.orbyfied.carbon.crafting.ingredient;

import java.util.function.BiPredicate;

public interface IngredientInterchangeAdapter<T extends Ingredient, O extends Ingredient> {

    /**
     * Should test if the specified ingredient can
     * merge with the other ingredient.
     * @param ing The ingredient to merge.
     * @param other The other ingredient to merge with.
     * @return -1 = No, 0 = Continue, 1 = Yes
     */
    int testMerge(T ing, O other);

    /////////////////////////////////////////////////

    static <T extends Ingredient, O extends Ingredient> IngredientInterchangeAdapter<T, O> continueIfNo(BiPredicate<T, O> pred) {
        return (ing, other) -> pred.test(ing, other) ? 1 : 0;
    }

}
