package net.orbyfied.examplemod;

import net.orbyfied.carbon.api.CarbonModAPI;
import net.orbyfied.carbon.api.mod.CarbonMod;
import net.orbyfied.carbon.api.mod.CarbonModInitializer;
import net.orbyfied.carbon.crafting.Ingredient;
import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.Result;
import net.orbyfied.carbon.crafting.match.RecipeDimensions;
import net.orbyfied.carbon.crafting.type.RecipeTypes;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.behaviour.EventItemBehaviourComponent;
import net.orbyfied.carbon.item.behaviour.event.PlayerItemInteraction;
import net.orbyfied.carbon.item.display.ModelItemDisplayComponent;
import net.orbyfied.carbon.item.material.MaterialItemComponent;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

import static net.orbyfied.carbon.crafting.Ingredient.EMPTY;

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
                        (item, idc) -> idc.addModel("ruby") // add default model
                )
                .component(EventItemBehaviourComponent::new,
                        (item, ibc) -> ibc.adapter()
                            // define a behaviour handler for our item
                            .behaviour(PlayerItemInteraction.class, interaction -> {
                                interaction.getEvent().getPlayer().sendMessage(interaction.getItem().toString());
                            })
                )
                .component(MaterialItemComponent::new,
                        // configure gem.ruby as our tag
                        // read more at the documentation
                        // for MaterialTag and MaterialAPI
                        (item, mic) -> mic.tag("gem.ruby")
                );

        System.out.println("AHAHHAAHHAHH");

        ruby
                .register(itemRegistry) // first register our item
                .build(); // VERY IMPORTANT: then build the item

        // create recipe for the item
        RecipeTypes.CRAFTING_SHAPED.newRecipe(Identifier.of("example:ruby_from_dirt"))
                .dimensions(new RecipeDimensions(2).sized(1, 1))
                .ingredients(
                        Ingredient.ofItem(Material.DIRT, 2).tagged("item 1")
                )
                .result(Result.ofItem(ruby, 1))
                .register(recipeRegistry);

        RecipeTypes.CRAFTING_UNSHAPED.newRecipe(Identifier.of("example:ruby_sussy_baka"))
                .ingredients(
                        Ingredient.ofItem(Material.BLAZE_ROD, 1),
                        Ingredient.ofItem(Material.ENDER_PEARL, 1)
                )
                .result(Result.ofItem(ruby, 2))
                .register(recipeRegistry);

        RecipeTypes.CRAFTING_SHAPED.newRecipe(Identifier.of("example:amogus"))
                .ingredients(
                        Ingredient.ofItem(Material.REDSTONE, 1), EMPTY,
                        EMPTY, Ingredient.ofItem(Material.DIAMOND, 1)
                )
                .result(Result.ofItem(ruby, 4))
                .dimensions(new RecipeDimensions(2).sized(2, 2))
                .register(recipeRegistry);

    }

}
