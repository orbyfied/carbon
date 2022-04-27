package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.registry.Identifiable;
import com.github.orbyfied.carbon.registry.Identifier;

public interface RecipeType<T extends Recipe> extends Identifiable {

    RecipeWorker newWorker();

    T newRecipe(Identifier id);

}
