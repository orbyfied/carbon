package com.github.orbyfied.carbon.test;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.api.mod.CarbonModInitializer;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.StateAllocator;
import com.github.orbyfied.carbon.item.display.ModelItemDisplayStrategy;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

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

        // create new item
        CarbonItem<?> ruby = new CarbonItem<>(
                Identifier.of("example:ruby"), // use your mod id as the namespace
                // we dont need our item to have any custom data
                // so we will just use the default item state
                CarbonItemState.class
        )
                .setBaseMaterial(Material.REDSTONE) // set base material
                .setDisplayStrategy(ModelItemDisplayStrategy::new, // create the service that will display our item
                        (item, ids) -> ids.setDisplayName("Ruby") // set the display name of the item
                            .addModel("ruby") // add default model
                )
                .register(itemRegistry) // first register our item
                .build(); // VERY IMPORTANT: then build the item

    }

}
