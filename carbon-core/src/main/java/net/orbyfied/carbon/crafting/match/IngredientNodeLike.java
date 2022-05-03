package net.orbyfied.carbon.crafting.match;

import net.orbyfied.carbon.crafting.Ingredient;
import net.orbyfied.carbon.crafting.inventory.CraftMatrix;
import net.orbyfied.carbon.item.CompiledStack;

import java.io.PrintStream;
import java.util.List;

/**
 * A node-like structure in the
 * {@link RecipeMatchTree}
 */
public interface IngredientNodeLike {

    List<RecipeMatchTree.Node> getChildren();

    IngredientNodeLike addChild(RecipeMatchTree.Node node);

    IngredientNodeLike removeChild(RecipeMatchTree.Node node);

    RecipeMatchTree.Node createChild(Ingredient ingredient);

    default RecipeMatchTree.Node getOrCreateChild(Ingredient in) {
        RecipeMatchTree.Node node;
        if ((node = getIngredientChild(in)) == null)
            node = createChild(in);
        return node;
    }

    default void debugPrint(PrintStream out, int depth) {
        out.println("| " + " ".repeat(depth) + "<" + depth + ">" + this);
        for (RecipeMatchTree.Node n : getChildren())
            n.debugPrint(out, depth + 1);
    }

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
