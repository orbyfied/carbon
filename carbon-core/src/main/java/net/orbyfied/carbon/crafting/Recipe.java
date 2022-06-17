package net.orbyfied.carbon.crafting;

import net.orbyfied.carbon.crafting.ingredient.Ingredient;
import net.orbyfied.carbon.crafting.type.RecipeType;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;

import java.util.*;

@SuppressWarnings("unchecked")
public class Recipe<S extends Recipe> implements Identifiable {

    protected List<Ingredient> ingredients;
    protected Result       result;

    protected RecipeType<S> type;

    protected final Identifier id;

    public Recipe(RecipeType type, Identifier id) {
        this.id = id;
        this.type = type;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    public RecipeType<S> type() {
        return type;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public S ingredients(Ingredient... ingredients) {
        this.ingredients = Arrays.asList(ingredients);
        return (S) this;
    }

    public S ingredients(int size) {
        this.ingredients = new ArrayList<>(size);
        return (S) this;
    }

    public S ingredient(int i, Ingredient ingredient) {
        this.ingredients.set(i, ingredient);
        return (S) this;
    }

    public Ingredient ingredient(int i) {
        return ingredients.get(i);
    }

    public S mutable() {
        // make ingredients mutable
        if (!(ingredients instanceof ArrayList))
            this.ingredients = new ArrayList<>(ingredients);
        return (S) this;
    }

    public Result result() {
        return result;
    }

    public S result(Result result) {
        this.result = result;
        return (S) this;
    }

    //////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(ingredients, recipe.ingredients) && Objects.equals(result, recipe.result) && Objects.equals(type, recipe.type) && Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, result, type, id);
    }

    @Override
    public String toString() {
        return "recipe/" + id;
    }

}
