package com.github.orbyfied.carbon.crafting.match;

import com.github.orbyfied.carbon.crafting.Ingredient;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tree structure used for quick recipe matching.
 * Basically, you have multiple layers of nodes, every
 * node being an ingredient and every layer being a sort
 * of index into the crafting matrix.
 *
 * A recipe is matched by going over all slots in the
 * crafting matrix. ({@link CompiledStack}s) For every
 * slot a node is matched from the current layer and the
 * children are used as the next. The bottom/tail nodes
 * store the recipe references themselves.
 */
public class RecipeMatchTree implements IngredientNodeLike {

    /**
     * All the top-level 'head' nodes.
     */
    final List<Node> heads = new ArrayList<>();

    public List<Node> getHeads() {
        return heads;
    }

    @Override
    public List<Node> getChildren() {
        return heads;
    }

    @Override
    public RecipeMatchTree addChild(Node node) {
        heads.add(node);
        return this;
    }

    @Override
    public RecipeMatchTree removeChild(Node node) {
        heads.remove(node);
        return this;
    }

    @Override
    public Node getIngredientChild(Ingredient ingredient) {
        for (Node node : heads)
            if (node.ingredient.equals(ingredient))
                return node;
        return null;
    }

    public Node matchIngredients(Iterator<CompiledStack> iterator) {
        IngredientNodeLike curr = this;
        while (iterator.hasNext()) {
            curr = curr.findChild(iterator.next());
        }
        if (!(curr instanceof Node))
            throw new IllegalArgumentException();
        return (Node) curr;
    }

    public Node matchSlots(Iterator<Slot> iterator) {
        return matchIngredients(CollectionUtil.mappedIterator(iterator, Slot::getItem));
    }

    public RecipeMatchTree add(Iterator<Ingredient> ingredients,
                               Recipe recipe) {
        IngredientNodeLike prev = this;
        IngredientNodeLike curr = this;
        while (ingredients.hasNext()) {
            Ingredient in = ingredients.next();
            curr = curr.getIngredientChild(in);
            if (curr == null) {
                curr = new Node((Node) prev, in);
                ((Node) curr).ingredient = in;
                prev.addChild((Node) curr);
            }

            prev = curr;
        }

        if (curr instanceof Node)
            ((Node) curr).recipe = recipe;

        // return
        return this;
    }

    public RecipeMatchTree remove(Iterator<Ingredient> ingredients) {
        IngredientNodeLike curr = this;
        while (ingredients.hasNext()) {
            Ingredient in = ingredients.next();
            curr = curr.getIngredientChild(in);
            if (curr.getChildren().size() <= 1) {
                Node nc = (Node) curr;
                nc.children = null;
                nc.parent.children.remove(curr);
                break;
            }
        }

        return this;
    }

    /**
     * An ingredient node.
     */
    public static class Node implements IngredientNodeLike {

        public Node(Node parent,
                    Ingredient ingredient) {
            this.parent     = parent;
            this.ingredient = ingredient;
        }

        /**
         * The parent node.
         */
        protected Node parent;

        /**
         * The children of this node.
         */
        protected List<Node> children = new ArrayList<>();

        /**
         * The ingredient this node represents.
         */
        protected Ingredient ingredient;

        /**
         * The recipe.
         * Only in tail nodes.
         */
        protected Recipe recipe;

        /* Functions. */

        @Override
        public Node addChild(Node node) {
            children.add(node);
            return this;
        }

        @Override
        public Node removeChild(Node node) {
            children.remove(node);
            return this;
        }

        @Override
        public Node getIngredientChild(Ingredient ingredient) {
            for (Node node : children)
                if (node.ingredient.equals(ingredient))
                    return node;
            return null;
        }

        /* Getters. */

        public Node getParent() {
            return parent;
        }

        @Override
        public List<Node> getChildren() {
            return children;
        }

        public Ingredient getIngredient() {
            return ingredient;
        }

        public Recipe getRecipe() {
            return recipe;
        }

    }

}
