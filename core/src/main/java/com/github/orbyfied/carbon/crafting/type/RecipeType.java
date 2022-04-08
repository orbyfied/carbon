package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.registry.Identifiable;

public interface RecipeType extends Identifiable {

    RecipeWorker newWorker();

}
