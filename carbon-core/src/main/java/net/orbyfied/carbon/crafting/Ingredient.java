package net.orbyfied.carbon.crafting;

import net.orbyfied.carbon.crafting.inventory.CraftMatrix;
import net.orbyfied.carbon.item.CompiledStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.Material;

/**
 * Represents an ingredient that can be matched.
 */
public interface Ingredient {

    /**
     * Checks if this ingredient matches the
     * provided item stack in the context of
     * the provided matrix.
     * @param stack The item stack.
     * @param matrix The matrix.
     * @return If it matches.
     */
    boolean matches(CompiledStack stack, CraftMatrix matrix);

    /**
     * Counts the amount of crafts that can be
     * executed with the provided item stack in
     * the context of the provided matrix.
     * @param stack The item stack.
     * @param matrix The matrix.
     * @return The count of crafts.
     */
    int count(CompiledStack stack, CraftMatrix matrix);

    /**
     * Modifies the provided item stack to 'use it'
     * once a craft is completed in the context of the
     * provided matrix.
     * @param stack The item stack.
     * @param amount The amount of crafts completed.
     * @param matrix The matrix.
     */
    void used(CompiledStack stack, int amount, CraftMatrix matrix);

    /**
     * Checks if this ingredient is comparable
     * to the provided other ingredient.
     * @param ingredient The ingredient to check against.
     * @return If it is comparable.
     */
    boolean equals(Ingredient ingredient);

    default TaggedIngredient tagged(Object tag) {
        return TaggedIngredient.of(this, tag);
    }

    ////////////////////////////////////////

    /**
     * An empty slot.
     */
    Ingredient EMPTY = new Ingredient() {

        @Override
        public boolean matches(CompiledStack stack, CraftMatrix matrix) {
            return stack == null ||
                    stack.getAmount() == 0 ||
                    stack.getItemType() == Items.AIR;
        }

        @Override
        public boolean equals(Ingredient ingredient) {
            return ingredient == this;
        }

        @Override
        public int count(CompiledStack stack, CraftMatrix matrix) {
            // ignore, empty slots should not be accounted for in amount counting
            return -1;
        }

        @Override
        public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
            // ignore, empty slots should not be accounted for in amount counting
        }

        @Override
        public String toString() {
            return "IngredientEMPTY";
        }
    };

    static Ingredient ofItem(Material material, int amt) {
        Item item = ItemUtil.getItem(material);
        return new UnspecificItemIngredient(item, amt);
    }

    /* ---- Implementations ---- */

    class UnspecificItemIngredient implements Ingredient {

        private final Item item;
        private final int amtNeeded;

        public UnspecificItemIngredient(Item item, int amtNeeded) {
            this.item = item;
            this.amtNeeded = amtNeeded;
        }

        @Override
        public boolean matches(CompiledStack stack, CraftMatrix matrix) {
            return stack.getItemType() == item;
        }

        @Override
        public boolean equals(Ingredient ingredient) {
            if (ingredient == this)
                return true;
            if (ingredient instanceof UnspecificItemIngredient uii)
                return uii.item == item && uii.amtNeeded == amtNeeded;
            return false;
        }

        @Override
        public int count(CompiledStack stack, CraftMatrix matrix) {
            return stack.getAmount() / amtNeeded;
        }

        @Override
        public void used(CompiledStack stack, int amount, CraftMatrix matrix) {
            int itemsUsed = amount * amtNeeded;
            int resultStackAmount = stack.getAmount() - itemsUsed;
            System.out.println("from " + this +
                    ": amount: " + amount +
                    ", needed: " + amtNeeded +
                    ", items: " + itemsUsed +
                    ", sourceAmt: " + stack.getAmount() +
                    ", resultAmt: " + resultStackAmount);

            stack.getStack().setCount(resultStackAmount);
        }

        @Override
        public String toString() {
            return "UnspecificIngredient(" + item + " min " + amtNeeded + "x)";
        }

    }

}
