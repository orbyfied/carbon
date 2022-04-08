package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;

public abstract class RecipeWorker {

    protected final RecipeType type;

    protected RecipeWorker(RecipeType type) {
        this.type = type;
    }

    public RecipeType getType() {
        return type;
    }

    public abstract void register(Recipe recipe);

    public abstract void unregister(Recipe recipe);

    public abstract Recipe resolve(CraftMatrix matrix);

}
