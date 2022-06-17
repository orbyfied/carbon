package net.orbyfied.carbon.crafting.ingredient;

import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.data.LinkedMultiHashMap;

import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class IngredientType<T extends Ingredient> implements Identifiable {

    /**
     * The identifier of this ingredient type.
     */
    final Identifier identifier;

    /**
     * The loaded interchange adapters.
     */
    final LinkedMultiHashMap<IngredientType<? extends Ingredient>, IngredientInterchangeAdapter<T, ? extends Ingredient>> interchange = new LinkedMultiHashMap<>();

    public IngredientType(Identifier identifier) {
        this.identifier = identifier;
    }

    public IngredientType(String id) {
        this.identifier = Identifier.of(id);
    }

    /**
     * Get the identifier of this type.
     * @return The identifier.
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public LinkedList<IngredientInterchangeAdapter<T, ? extends Ingredient>> getInterchangeAdapters(IngredientType<? extends Ingredient> other) {
        return interchange.getAll(other);
    }

    public LinkedList<IngredientInterchangeAdapter<T, ? extends Ingredient>> getOrCreateInterchangeAdapters(IngredientType<? extends Ingredient> other) {
        return interchange.getOrCreateAll(other);
    }

    public IngredientType<T> putSelfInterchangeLast(IngredientInterchangeAdapter<T, T> adapter) {
        interchange.addFirst(this, adapter);
        return this;
    }

    /**
     * Register a new interchange adapter at the lowest priority.
     * @param other The other ingredient type.
     * @param adapter The adapter.
     * @return This.
     */
    public <O extends Ingredient> IngredientType<T> putInterchangeLast(IngredientType<O> other, IngredientInterchangeAdapter<T, O> adapter) {
        interchange.addLast(other, adapter);
        return this;
    }

    /**
     * Register a new interchange adapter at the highest priority.
     * @param other The other ingredient type.
     * @param adapter The adapter.
     * @return This.
     */
    public <O extends Ingredient> IngredientType<T> putInterchangeFirst(IngredientType<O> other, IngredientInterchangeAdapter<T, O> adapter) {
        interchange.addFirst(other, adapter);
        return this;
    }

    /**
     * Register a new interchange adapter at the given priority.
     * @param index The priority index.
     * @param other The other ingredient type.
     * @param adapter The adapter.
     * @return This.
     */
    public <O extends Ingredient> IngredientType<T> putInterchangeAt(int index, IngredientType<O> other, IngredientInterchangeAdapter<T, O> adapter) {
        interchange.addAt(index, other, adapter);
        return this;
    }

    @SuppressWarnings("unchecked")
    public boolean canMerge(T a, Ingredient other) {
        // check for true identity
        if (a == other)
            return true;

        // get and iterate over adapters
        LinkedList<IngredientInterchangeAdapter<T, ? extends Ingredient>> adapters = getOrCreateInterchangeAdapters(other.getType());
        for (IngredientInterchangeAdapter adapter : adapters) {
            int r = adapter.testMerge(a, other);
            if (r == /* yes */ 1)       return true;
            else if (r == /* no */ -1) return false;
            // otherwise continue
        }

        // if none say yes just return no
        return false;
    }

}
