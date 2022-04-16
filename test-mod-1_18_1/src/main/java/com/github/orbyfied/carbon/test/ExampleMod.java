package com.github.orbyfied.carbon.test;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.api.mod.CarbonModInitializer;
import com.github.orbyfied.carbon.crafting.Ingredient;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.match.RecipeDimensions;
import com.github.orbyfied.carbon.crafting.type.RecipeTypes;
import com.github.orbyfied.carbon.event.EventHandler;
import com.github.orbyfied.carbon.event.EventListener;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.item.behaviour.EventItemBehaviourComponent;
import com.github.orbyfied.carbon.item.behaviour.event.ItemInteraction;
import com.github.orbyfied.carbon.item.display.ModelItemDisplayComponent;
import com.github.orbyfied.carbon.item.material.MaterialItemComponent;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.orbyfied.carbon.crafting.Ingredient.EMPTY;

// indicate that this is a Carbon mod
@CarbonMod(
        id = "example" /* the mod id */ ,
        name = "Example" /* the display name */,
        version = "1.0.0" /* the current version */
)
public class ExampleMod
        extends JavaPlugin /* every mod main class needs to be a bukkit plugin */
        implements CarbonModInitializer /* the mod initializer */ {

    /*
        You can set an alternate mod initializer in the mod
        descriptor annotation, the default is the mod class.
        Every mod initializer needs to implement CarbonModInitializer.
     */

    /**
     * Called when initializing the mod.
     * Part of the mod initializer
     */
    @Override
    public void modInitialize(CarbonModAPI api) {

        // get item registry
        final Registry<CarbonItem<?>> itemRegistry = api.getRegistry("minecraft:items");

        // get the recipe registry
        final Registry<Recipe<?>> recipeRegistry = api.getRegistry("minecraft:recipes");

        // create new item
        CarbonItem<?> ruby = new CarbonItem<>(
                Identifier.of("example:ruby"), // use your mod id as the namespace
                // we dont need our item to have any custom data
                // so we will just use the default item state
                CarbonItemState.class
        )
                .setBaseMaterial(Material.REDSTONE) // set base material
                .component(ModelItemDisplayComponent::new, // create the service that will display our item
                        (item, idc) -> idc.displayName("Ruby") // set the display name of the item
                            .addModel("ruby") // add default model
                )
                .component(EventItemBehaviourComponent::new,
                        (item, ibc) -> ibc.adapter().addBehaviour(new MyItemBehaviour())
                )
                .component(MaterialItemComponent::new,
                        (item, mic) -> mic.tag("gem.ruby")
                )
                .register(itemRegistry) // first register our item
                .build(); // VERY IMPORTANT: then build the item

        // create recipe for the item
        RecipeTypes.CRAFTING_SHAPED.newRecipe(Identifier.of("example:ruby_from_dirt"))
                .dimensions(new RecipeDimensions(2).sized(3, 3))
                .ingredients(
                        Ingredient.ofItem(Material.DIRT, 2), EMPTY, EMPTY,
                        EMPTY, EMPTY, EMPTY,
                        EMPTY, EMPTY, EMPTY
                )
                .result((out, recipe, amount) -> {
                    out.addItem(new CompiledStack().fill(ruby, amount));
                })
                .register(recipeRegistry);

    }

    /**
     * Define behaviour for our item.
     */
    public static class MyItemBehaviour implements EventListener {

        // this is called every time a player
        // interacts with or using our item
        @EventHandler
        public void interact(ItemInteraction interaction) {
            // send state as message for testing
            interaction.getEvent().getPlayer().sendMessage(
                    interaction.getState().toString()
            );
        }

    }

}
