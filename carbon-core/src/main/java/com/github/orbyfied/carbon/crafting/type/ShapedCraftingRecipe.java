package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.match.RecipeDimensions;
import com.github.orbyfied.carbon.registry.Identifier;

public class ShapedCraftingRecipe extends Recipe<ShapedCraftingRecipe> {

    protected RecipeDimensions dimensions;

    public ShapedCraftingRecipe(RecipeType type, Identifier id) {
        super(type, id);
    }

    public RecipeDimensions dimensions() {
        return dimensions;
    }

    public ShapedCraftingRecipe dimensions(RecipeDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

}
