package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.match.RecipeMatchTree;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.CollectionUtil;

public class RecipeTypes {

    /**
     * Shaped crafting.
     * Like the default Minecraft crafting table.
     */
    public static final RecipeType CRAFTING_SHAPED = new RecipeType() {

        @Override
        public Identifier getIdentifier() {
            return Identifier.of("minecraft:crafting_shaped");
        }

        @Override
        public RecipeWorker newWorker() {
            return new RecipeWorker(this) {

                final RecipeMatchTree tree = new RecipeMatchTree();

                @Override
                public void register(Recipe recipe) {
                    tree.add(
                            recipe.ingredients().iterator(),
                            recipe
                    );
                }

                @Override
                public void unregister(Recipe recipe) {
                    tree.remove(recipe.ingredients().iterator());
                }

                @Override
                public Recipe resolve(CraftMatrix matrix) {
                    return tree.matchSlots(
                            CollectionUtil.iterate(matrix.getInput())
                    ).getRecipe();
                }

            };
        }

    };

}
