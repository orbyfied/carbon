package com.github.orbyfied.carbon.crafting.type;

import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.crafting.Ingredient;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.inventory.CraftMatrix;
import com.github.orbyfied.carbon.crafting.inventory.Slot;
import com.github.orbyfied.carbon.crafting.inventory.SlotContainer;
import com.github.orbyfied.carbon.crafting.match.IngredientNodeLike;
import com.github.orbyfied.carbon.crafting.match.RecipeMatchTree;
import com.github.orbyfied.carbon.crafting.util.WrappedInventoryClickEvent;
import com.github.orbyfied.carbon.crafting.util.WrappedPrepareCraftEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.EventListener;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecipeTypes {

    private static final EventBus EVENT_BUS = new EventBus();

    public static void registerAll(Registry<RecipeType> registry) {
        registry
                .register(CRAFTING_SHAPED)
                .register(CRAFTING_UNSHAPED);

        // register crafting event pipeline
        EVENT_BUS.bake(WrappedPrepareCraftEvent.class);
        EVENT_BUS.bake(WrappedInventoryClickEvent.class);
        Bukkit.getPluginManager().registerEvents(new Listener() {
             @EventHandler
             public void onItemPrepareCraft(PrepareItemCraftEvent event) {
                 EVENT_BUS.post(
                         new WrappedPrepareCraftEvent(event)
                 );
             }

             @EventHandler
             public void onItemCraft(InventoryClickEvent event) {
                 EVENT_BUS.post(
                         new WrappedInventoryClickEvent(event)
                 );
             }
        }, CarbonJavaAPI.get().getMain().getPlugin());
    }

    /*
     * All crafting.
     */

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

    public static class CraftingHandler implements EventListener {

        public static final String EVENT_HANDLED_FLAG =
                "craftingHandler.handled";

        protected final Plugin plugin = CarbonJavaAPI.get().getMain().getPlugin();
        protected final RecipeWorker worker;

        public CraftingHandler(RecipeWorker worker) {
            this.worker = worker;
        }

        public void prepareCraft(PrepareCraftWrapper event,
                                 boolean interact) {

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
            if (interact) {
                recipe.result().write(
                        cm.output(),
                        recipe,
                        amount
                );
            }

        }

        @com.github.orbyfied.carbon.event.EventHandler
        public void onItemPrepareCraft(WrappedPrepareCraftEvent we) {
            if (we.<Boolean>carried(EVENT_HANDLED_FLAG, false))
                return;

            PrepareItemCraftEvent event = we.getEvent();

            // prepare
            PrepareCraftWrapper w = new PrepareCraftWrapper(event.getInventory());
            prepareCraft(w, true);

            if (w.recipe == null)
                return;

            we.carry(EVENT_HANDLED_FLAG, true);
        }

        @com.github.orbyfied.carbon.event.EventHandler
        public void onItemCraft(WrappedInventoryClickEvent we) {
            if (we.<Boolean>carried(EVENT_HANDLED_FLAG, false))
                return;
            InventoryClickEvent event = we.getEvent();

            if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
            if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

            // prepare first
            CraftingInventory inv = (CraftingInventory) event.getClickedInventory();
            PrepareCraftWrapper w = new PrepareCraftWrapper(inv);
            prepareCraft(w, false);

            if (w.recipe == null)
                return;

            we.carry(EVENT_HANDLED_FLAG, true);

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

    /**
     * Shaped crafting.
     * Like the default Minecraft crafting table.
     */

    public static final RecipeType<ShapedCraftingRecipe> CRAFTING_SHAPED = new RecipeType<>() {

        private final Identifier id = Identifier.of("minecraft:crafting_shaped");

        @Override
        public Identifier getIdentifier() {
            return id;
        }

        @Override
        public RecipeWorker newWorker() {
            return new ShapedCraftingWorker(this);
        }

        @Override
        public ShapedCraftingRecipe newRecipe(Identifier id) {
            return new ShapedCraftingRecipe(this, id);
        }

    };

    public static class ShapedCraftingWorker extends RecipeWorker {

        final RecipeMatchTree tree = new RecipeMatchTree();
        final CraftingHandler defaults = new CraftingHandler(this);

        protected ShapedCraftingWorker(RecipeType type) {
            super(type);
            EVENT_BUS.register(defaults);
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
            RecipeMatchTree.Node n = tree.matchMatrix(matrix);
            if (n == null)
                return null;
            return n.getRecipe();
        }

    }

    /*
     * Unshaped crafting.
     */

    public static final RecipeType<ShapelessCraftingRecipe> CRAFTING_UNSHAPED = new RecipeType<ShapelessCraftingRecipe>() {

        private final Identifier id = Identifier.of("minecraft:crafting_unshaped");

        @Override
        public Identifier getIdentifier() {
            return id;
        }

        @Override
        public RecipeWorker newWorker() {
            return new UnshapedCraftingWorker(this);
        }

        @Override
        public ShapelessCraftingRecipe newRecipe(Identifier id) {
            return new ShapelessCraftingRecipe(this, id);
        }

    };

    public static class UnshapedCraftingWorker extends RecipeWorker {

        final RecipeMatchTree tree = new RecipeMatchTree();
        final CraftingHandler defaults = new CraftingHandler(this);

        protected UnshapedCraftingWorker(RecipeType type) {
            super(type);
            EVENT_BUS.register(defaults);
        }

        public void registerNextPermutation(
                IngredientNodeLike node,
                int depth,
                List<Ingredient> ingredients,
                HashSet<Integer> usedIndexes,
                Recipe recipe
        ) {
            if (depth >= ingredients.size()) {
                ((RecipeMatchTree.Node)node).setRecipe(recipe);
                return;
            }

            int il = ingredients.size();
            for (int i = 0; i < il; i++) {
                if (usedIndexes.contains(i)) {
                    continue;
                }

                Ingredient in = ingredients.get(i);
                RecipeMatchTree.Node n = node.getOrCreateChild(in);

                usedIndexes.add(i);
                registerNextPermutation(
                        n,
                        depth + 1,
                        ingredients,
                        usedIndexes,
                        recipe
                );
                usedIndexes.remove(i);
            }

        }

        @Override
        @SuppressWarnings("unchecked")
        public void register(Recipe recipe) {
            registerNextPermutation(
                    tree,
                    0,
                    recipe.ingredients(),
                    new HashSet<>(),
                    recipe
            );

//            tree.debugPrint(System.out, 0);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void unregister(Recipe recipe) {
            tree.remove(recipe.ingredients().iterator());
        }

        @Override
        public Recipe resolve(CraftMatrix matrix) {
            matrix.normalize();
            RecipeMatchTree.Node n = tree.matchMatrix(
                    matrix,
                    (__, item, ___) -> ItemUtil.isEmpty(item)
            );
            if (n == null)
                return null;
            return n.getRecipe();
        }

    }

}
