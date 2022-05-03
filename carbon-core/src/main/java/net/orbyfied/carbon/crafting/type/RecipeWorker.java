package net.orbyfied.carbon.crafting.type;

import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.inventory.CraftMatrix;

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

    public abstract CraftMatrix processMatrix(CraftMatrix matrix);

}
