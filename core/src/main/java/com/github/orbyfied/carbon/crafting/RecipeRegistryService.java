package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.type.RecipeType;
import com.github.orbyfied.carbon.crafting.type.RecipeWorker;
import com.github.orbyfied.carbon.registry.AbstractRegistryService;
import com.github.orbyfied.carbon.registry.FunctionalService;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;

import java.util.HashMap;

public class RecipeRegistryService
        extends AbstractRegistryService<Registry<Recipe>, Recipe>
        implements FunctionalService<Registry<Recipe>, Recipe> {

    public RecipeRegistryService(Registry<Recipe> registry) {
        super(registry);
    }

    /**
     * Workers mapped by recipe type identifier.
     */
    protected final HashMap<Identifier, RecipeWorker> workers = new HashMap<>();

    public RecipeRegistryService addWorker(RecipeWorker worker) {
        workers.put(worker.getType().getIdentifier(), worker);
        return this;
    }

    public RecipeWorker getWorker(Identifier identifier) {
        return workers.get(identifier);
    }

    public RecipeWorker getWorker(RecipeType type) {
        return getWorker(type.getIdentifier());
    }

    @Override
    public void registered(Recipe val) {
        getWorker(val.type).register(val);
    }

    @Override
    public void unregistered(Recipe val) {
        getWorker(val.type).unregister(val);
    }

}
