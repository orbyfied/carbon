package net.orbyfied.carbon.crafting.type;

import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;

public interface RecipeType<T extends Recipe> extends Identifiable {

    RecipeWorker newWorker();

    T newRecipe(Identifier id);

}
