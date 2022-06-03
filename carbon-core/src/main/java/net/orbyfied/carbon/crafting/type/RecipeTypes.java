package net.orbyfied.carbon.crafting.type;

import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.crafting.Ingredient;
import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.inventory.CraftMatrix;
import net.orbyfied.carbon.crafting.inventory.Slot;
import net.orbyfied.carbon.crafting.inventory.SlotContainer;
import net.orbyfied.carbon.crafting.match.IngredientNodeLike;
import net.orbyfied.carbon.crafting.match.RecipeMatchTree;
import net.orbyfied.carbon.crafting.util.WrappedInventoryClickEvent;
import net.orbyfied.carbon.crafting.util.WrappedPrepareCraftEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.EventListener;
import net.orbyfied.carbon.event.handler.BasicHandler;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
                                 int amount) {

            // get inventories
            CraftingInventory inv = event.getInventory();
            ItemStack[] matrix    = event.getInventory().getMatrix();

            // create craft matrix
            Slot resultSlot = Slot.getAndSet(inv::setResult, inv::getResult);
            CraftMatrix cm = new CraftMatrix()
                    .dimensions(matrix.length, matrix.length == 9 ? 3 : 2)
                    .input(SlotContainer.ofArray(matrix))
                    .output(SlotContainer.of(resultSlot));

            cm = worker.processMatrix(cm);

            event.matrix = cm;

            // collect compiled stacks
            SlotContainer inc = cm.input();
            int iml = inc.getSize();
            List<CompiledStack> stacks = new ArrayList<>(iml);
            for (int i = 0; i < iml; i++)
                stacks.add(inc.getSlot(i).getItem());

            event.stacks = stacks;

            // check if there are any special items
            boolean isVanilla = stacks.stream().noneMatch(c -> c.getState() != null);

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
            if (amount == -1) {

                l = recipe.ingredients().size();
                for (int i = 0; i < l; i++) {
                    Ingredient in = recipe.ingredient(i);
                    CompiledStack stack = stacks.get(i);

                    int ia = in.count(stack, cm);
                    if (ia == -1)
                        continue;
                    if (amount == -1 || ia < amount)
                        amount = ia;
                }

                amount = recipe.result().count(
                        cm,
                        cm.output(),
                        recipe,
                        amount
                );

            }

            event.amount = amount;

        }

        @BasicHandler
        public void onItemPrepareCraft(WrappedPrepareCraftEvent we) {
            if (we.<Boolean>carried(EVENT_HANDLED_FLAG, false))
                return;

            PrepareItemCraftEvent event = we.getEvent();

            // prepare
            long t1 = System.nanoTime();

            PrepareCraftWrapper w = new PrepareCraftWrapper(event.getInventory());
            prepareCraft(w, 1);

            long t2 = System.nanoTime();
            long t   = t2 - t1;
            long tms = t / 1_000_000;

            System.out.println("[+d] Time Elapsed: " + t + "ns (" + tms + "ms)");

            if (w.recipe == null)
                return;

            // put result
            CraftMatrix cm = w.matrix;
            w.recipe.result().write(
                    cm,
                    cm.output(),
                    w.recipe,
                    w.amount
            );

            we.carry(EVENT_HANDLED_FLAG, true);
        }

        @BasicHandler
        public void onItemCraft(WrappedInventoryClickEvent we) {
            if (we.<Boolean>carried(EVENT_HANDLED_FLAG, false))
                return;
            InventoryClickEvent event = we.getEvent();

            if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
            if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

            // prepare first
            CraftingInventory inv = (CraftingInventory) event.getClickedInventory();
            PrepareCraftWrapper w = new PrepareCraftWrapper(inv);
            prepareCraft(w, -1);

            if (w.recipe == null)
                return;

            we.carry(EVENT_HANDLED_FLAG, true);

            // execute
            Recipe recipe = w.recipe;
            int amount = w.amount;
            List<CompiledStack> stacks = w.stacks;

            System.out.println("TOTAL AMOUNT: " + amount);

            // if normal click, only take one
            if (event.getClick() == ClickType.LEFT)
                amount = 1;

            System.out.println("FINAL AMOUNT: " + amount);

            // use items
            int l = recipe.ingredients().size();
            for (int i = 0; i < l; i++) {
                Ingredient in = recipe.ingredient(i);
                CompiledStack stack = stacks.get(i);

                in.used(stack, amount, w.matrix);
            }

            recipe.result().write(
                    w.matrix,
                    w.matrix.output(),
                    w.recipe,
                    amount - 1
            );
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
            RecipeMatchTree.Node n = tree.matchMatrix(matrix);
            if (n == null)
                return null;
            return n.getRecipe();
        }

        @Override
        public CraftMatrix processMatrix(CraftMatrix matrix) {
            return matrix.normalize();
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
//            matrix.normalize();
            RecipeMatchTree.Node n = tree.matchMatrix(
                    matrix,
                    (__, item, ___) -> ItemUtil.isEmpty(item)
            );
            if (n == null)
                return null;
            return n.getRecipe();
        }

        @Override
        public CraftMatrix processMatrix(CraftMatrix matrix) {
            return matrix.cleanAllEmpty();
        }

    }

}
