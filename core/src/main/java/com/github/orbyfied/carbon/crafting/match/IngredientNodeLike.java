package com.github.orbyfied.carbon.crafting.match;

import com.github.orbyfied.carbon.crafting.Ingredient;
import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.item.CompiledStack;

import java.util.List;

/**
 * A node-like structure in the
 * {@link RecipeMatchTree}
 */
public interface IngredientNodeLike {

    List<RecipeMatchTree.Node> getChildren();

    IngredientNodeLike addChild(RecipeMatchTree.Node node);

    IngredientNodeLike removeChild(RecipeMatchTree.Node node);

    RecipeMatchTree.Node getIngredientChild(Ingredient ingredient);

    default RecipeMatchTree.Node findChild(CompiledStack stack,
                                           CraftMatrix matrix) {
        List<RecipeMatchTree.Node> children = getChildren();
        int l = children.size();
        RecipeMatchTree.Node c;
        for (int i = 0; i < l; i++)
            if ((c = children.get(i)).ingredient.matches(stack, matrix))
                return c;
        return null;
    }

}
