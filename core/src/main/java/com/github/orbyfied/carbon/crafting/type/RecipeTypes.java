package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.crafting.Ingredient;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.crafting.inventory.SlotContainer;
import com.github.orbyfied.carbon.crafting.match.RecipeMatchTree;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeTypes {

    public static void registerAll(Registry<RecipeType> registry) {
        registry
                .register(CRAFTING_SHAPED);
    }

    /**
     * Shaped crafting.
     * Like the default Minecraft crafting table.
     */

    public static final RecipeType<ShapedCraftingRecipe> CRAFTING_SHAPED = new RecipeType<>() {

        @Override
        public Identifier getIdentifier() {
            return Identifier.of("minecraft:crafting_shaped");
        }

        @Override
        public RecipeWorker newWorker() {
            return new ShapedCraftingWorker(this);
        }

        @Override
        public ShapedCraftingRecipe newRecipe(Identifier id) {
            return new ShapedCraftingRecipe(id);
        }

    };

    public static class ShapedCraftingWorker extends RecipeWorker {

        final RecipeMatchTree tree = new RecipeMatchTree();
        final ShapedCraftingDefaults defaults = new ShapedCraftingDefaults(this);

        protected ShapedCraftingWorker(RecipeType type) {
            super(type);
            Bukkit.getPluginManager().registerEvents(defaults,
                    CarbonJavaAPI.get().getMain().getPlugin());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void register(Recipe recipe) {
            tree.add(
                    recipe.ingredients().iterator(),
                    recipe
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public void unregister(Recipe recipe) {
            tree.remove(recipe.ingredients().iterator());
        }

        @Override
        public Recipe resolve(CraftMatrix matrix) {
            matrix.normalize();
            RecipeMatchTree.Node n = tree.matchSlots(
                    matrix.input().iterator()
            );
            if (n == null)
                return null;
            return n.getRecipe();
        }

    }

    public static class PrepareCraftWrapper {

        private CraftingInventory inv;

        public List<CompiledStack> stacks;
        public Recipe recipe;
        public int amount;
        public CraftMatrix matrix;

        public PrepareCraftWrapper(CraftingInventory inv) {
            this.inv = inv;
        }

        public CraftingInventory getInventory() {
            return inv;
        }

    }

    public static class ShapedCraftingDefaults implements Listener {

        protected final Plugin plugin = CarbonJavaAPI.get().getMain().getPlugin();
        protected final ShapedCraftingWorker worker;

        public ShapedCraftingDefaults(ShapedCraftingWorker worker) {
            this.worker = worker;
        }

        public void prepareCraft(PrepareCraftWrapper event) {

            // collect compiled stacks
            ItemStack[] inputMatrix = event.getInventory().getMatrix();
            int iml = inputMatrix.length;
            List<CompiledStack> stacks = new ArrayList<>(iml);
            for (int i = 0; i < iml; i++)
                stacks.add(new CompiledStack().wrap(inputMatrix[i]));

            event.stacks = stacks;

            // check if there are any special items
            boolean isVanilla = stacks.stream().noneMatch(c -> c.getState() != null);

            // get inventories
            CraftingInventory inv = event.getInventory();
            ItemStack[] matrix    = event.getInventory().getMatrix();

            // create craft matrix
            Slot resultSlot = Slot.getAndSet(inv::setResult, inv::getResult);
            CraftMatrix cm = new CraftMatrix()
                    .input(SlotContainer.ofArray(matrix))
                    .output(SlotContainer.of(resultSlot));

            event.matrix = cm;

            // resolve recipe
            Recipe recipe = worker.resolve(cm);
            event.recipe = recipe;

            if (recipe == null) {
                if (!isVanilla)
                    event.getInventory().setResult(null);

                return;
            }

            int l;

            // evaluate recipe amount
            int amount = -1;

            l = recipe.ingredients().size();
            for (int i = 0; i < l; i++) {
                Ingredient in       = recipe.ingredient(i);
                CompiledStack stack = stacks.get(i);

                int ia = in.count(stack, cm);
                if (amount == -1 || ia > amount)
                    amount = ia;
            }

            event.amount = amount;

            // put result
            recipe.result().write(
                    cm.output(),
                    recipe,
                    amount
            );

        }

        @EventHandler
        public void onItemPrepareCraft(PrepareItemCraftEvent event) {
            // prepare
            PrepareCraftWrapper w = new PrepareCraftWrapper(event.getInventory());
            prepareCraft(w);
        }

        @EventHandler
        public void onItemCraft(InventoryClickEvent event) {
            if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
            if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

            // prepare first
            CraftingInventory inv = (CraftingInventory) event.getClickedInventory();
            PrepareCraftWrapper w = new PrepareCraftWrapper(inv);
            prepareCraft(w);

            // execute
            Recipe recipe = w.recipe;

            int    amount = w.amount;
            List<CompiledStack> stacks = w.stacks;

            // use items
            int l = recipe.ingredients().size();
            for (int i = 0; i < l; i++) {
                Ingredient in = recipe.ingredient(i);
                CompiledStack stack = stacks.get(i);

                in.used(stack, amount, w.matrix);
            }
        }

    }

}
