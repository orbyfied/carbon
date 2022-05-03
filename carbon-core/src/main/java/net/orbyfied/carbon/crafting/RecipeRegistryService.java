package net.orbyfied.carbon.crafting;

import net.orbyfied.carbon.crafting.type.RecipeType;
import net.orbyfied.carbon.crafting.type.RecipeWorker;
import net.orbyfied.carbon.registry.AbstractRegistryService;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;

import java.util.HashMap;

public class RecipeRegistryService
        extends AbstractRegistryService<Registry<Recipe>, Recipe> {

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

}
