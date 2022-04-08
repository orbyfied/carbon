package com.github.orbyfied.carbon.crafting;

import com.github.orbyfied.carbon.crafting.type.RecipeType;
import com.github.orbyfied.carbon.registry.Identifiable;
import com.github.orbyfied.carbon.registry.Identifier;

import java.util.*;

public class Recipe implements Identifiable {

    protected List<Ingredient> ingredients;
    protected Result       result;

    protected RecipeType type;

    protected final Identifier id;

    public Recipe(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    public RecipeType type() {
        return type;
    }

    public Recipe type(RecipeType type) {
        this.type = type;
        return this;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public Recipe ingredients(Ingredient... ingredients) {
        this.ingredients = Arrays.asList(ingredients);
        return this;
    }

    public Recipe ingredients(int size) {
        this.ingredients = new ArrayList<>(size);
        return this;
    }

    public Recipe ingredient(int i, Ingredient ingredient) {
        this.ingredients.set(i, ingredient);
        return this;
    }

    public Recipe mutable() {
        // make ingredients mutable
        if (!(ingredients instanceof ArrayList))
            this.ingredients = new ArrayList<>(ingredients);
        return this;
    }

    public Result result() {
        return result;
    }

    public Recipe result(Result result) {
        this.result = result;
        return this;
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
