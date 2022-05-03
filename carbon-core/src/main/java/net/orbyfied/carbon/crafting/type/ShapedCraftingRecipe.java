package net.orbyfied.carbon.crafting.type;

import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.match.RecipeDimensions;
import net.orbyfied.carbon.registry.Identifier;

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
