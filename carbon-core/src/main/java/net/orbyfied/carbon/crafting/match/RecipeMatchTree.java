package net.orbyfied.carbon.crafting.match;

import net.orbyfied.carbon.crafting.ingredient.Ingredient;
import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.TaggedIngredient;
import net.orbyfied.carbon.crafting.inventory.CraftMatrix;
import net.orbyfied.carbon.crafting.inventory.Slot;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.util.CollectionUtil;
import net.orbyfied.carbon.util.functional.TriPredicate;
import net.orbyfied.carbon.util.mc.ItemUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

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
    public Node createChild(Ingredient ingredient) {
        Node node = new Node(this, ingredient);
        heads.add(node);
        return node;
    }

    @Override
    public Node getIngredientChild(Ingredient ingredient) {
        for (Node node : heads)
            if (node.ingredient.equals(ingredient))
                return node;
        return null;
    }

    public Node matchMatrix(CraftMatrix matrix) {
        return matchMatrix(matrix, (i1, i2, i3) -> false);
    }

    public Node matchMatrix(CraftMatrix matrix,
                            TriPredicate<Slot, CompiledStack, IngredientNodeLike> skip) {
        // set up iterations
        IngredientNodeLike curr = this;
        Iterator<Slot> iterator = matrix.input().iterator();
        while (iterator.hasNext()) { // iterate
            // get slot and stack
            Slot slot = iterator.next();
            if (slot == null)
                continue;
            CompiledStack stack = slot.getItem();

            // test
            if (skip.test(slot, stack, curr))
                continue;

            // try to find child
            curr = curr.findChild(stack, matrix);
            if (curr == null)
                return null;

            // store tag
            Node n = (Node) curr;
            if (n.ingredient instanceof TaggedIngredient ti)
                ti.tag(matrix, slot);
        }
        if (!(curr instanceof Node))
            return null;
        return (Node) curr;
    }

    @Deprecated
    public Node matchIngredients(Iterator<CompiledStack> iterator) {
        IngredientNodeLike curr = this;
        while (iterator.hasNext()) {
            CompiledStack stack = iterator.next();
            if (ItemUtil.isEmpty(stack))
                continue;
            curr = curr.findChild(stack, null);
            if (curr == null)
                return null;
            if (curr instanceof Node n && n.recipe != null)
                return n;
        }
        if (!(curr instanceof Node))
            return null;
        return (Node) curr;
    }

    @Deprecated
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
                curr = new Node(prev, in);
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
                ((Node)nc.parent).children.remove(curr);
                break;
            }
        }

        return this;
    }

    /**
     * An ingredient node.
     */
    public static class Node implements IngredientNodeLike {

        public Node(IngredientNodeLike parent,
                    Ingredient ingredient) {
            this.parent     = parent;
            this.ingredient = ingredient;
        }

        /**
         * The parent node.
         */
        protected IngredientNodeLike parent;

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
        public Node createChild(Ingredient ingredient) {
            Node node = new Node(this, ingredient);
            children.add(node);
            return node;
        }

        @Override
        public Node getIngredientChild(Ingredient ingredient) {
            for (Node node : children)
                if (node.ingredient.equals(ingredient))
                    return node;
            return null;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
                    .add("ingredient: " + ingredient)
                    .add("recipe: " + recipe)
                    .toString();
        }

        /* Getters. */

        public IngredientNodeLike getParent() {
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

        /* Setters. */

        public Node setRecipe(Recipe recipe) {
            this.recipe = recipe;
            return this;
        }

    }

}
